package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.LocalizingNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.TypeNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.l3.utils.MemorySize;
import lu.pcy113.pclib.GlobalLogger;

public class X86Compiler extends L3Compiler {

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writeln("section .text");
		writeinstln("global _start");
		writeinstln("global " + root.getClosestDescriptor("main").getAsmName());
		writeln("_start:");

		compile(root);

		appendData();

		flushAndClose();

		File dir = outFile.getAbsoluteFile().getParentFile();
		try {
			exec("nasm -f elf32 -g -o " + FileUtils.replaceExtension(outFile.getName(), "o") + " " + outFile.getPath(), dir);
			exec("ld -m elf_i386 -o " + FileUtils.removeExtension(outFile.getName()) + " " + FileUtils.replaceExtension(outFile.getName(), "o"), dir);
			exec("./" + FileUtils.removeExtension(outFile.getName()), dir);
		} catch (IOException | InterruptedException e) {
			throw new CompilerException("Could not exec: '" + outFile + "', '" + FileUtils.removeExtension(outFile.getName()) + "' and '" + FileUtils.replaceExtension(outFile.getName(), "o") + "' in " + dir, e);
		}
	}

	int stackPos = 0;

	private void compile(Node node) throws CompilerException {
		if (node instanceof RuntimeNode) {
			compileStaticLetTypeDefNodes((RuntimeNode) node);
			compileMainFunDefNode((RuntimeNode) node);
			for (Node n : node.getChildren()) {
				if (n instanceof FunDefNode && !((FunDefNode) n).getIdent().getIdentifier().equals("main")) {
					compileFunDefNode((FunDefNode) n);
				}
			}
		} else if (node instanceof FunCallNode) {
			compileFunCallNode((FunCallNode) node);
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDefNode((LetTypeDefNode) node);
		} else if (node instanceof ReturnNode) {
			compileReturnNode((ReturnNode) node);
		} else if (node instanceof LetTypeSetNode) {
			compileLetTypeSetNode((LetTypeSetNode) node);
		} else {
			throw new CompilerException("Unknown node: " + node);
		}
	}

	private void compileLetTypeSetNode(LetTypeSetNode node) throws CompilerException {
		String ident = node.getLet().getIdent().getIdentifier();
		ScopeContainer container = node.getParentContainer();
		if (!node.getParentContainer().containsDescriptor(ident)) {
			throw new CompilerException("Couldn't find: " + ident + " in scope: " + node.getParentContainer() + " (" + node.getLet().getIdent().getLine() + ":" + node.getLet().getIdent().getColumn() + ")");
		}
		LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

		if (node.getExpr() instanceof FunCallNode) {
			String funName = ((FunCallNode) node.getExpr()).getName().getIdentifier();
			if (!node.getParentContainer().containsDescriptor(funName)) {
				throw new CompilerException("Function: " + funName + ", not found in scope.");
			}
			FunScopeDescriptor fun = (FunScopeDescriptor) node.getParentContainer().getClosestDescriptor(funName);
			if (def.getNode() instanceof LetTypeDefNode) {
				checkReturnTypeMatch(fun.getNode(), ((LetTypeDefNode) def.getNode()).getType());
			} else if (def.getNode() instanceof LetTypeDefNode) {
				checkReturnTypeMatch(fun.getNode(), ((LetTypeDefNode) def.getNode()).getType());
			}
		}

		compileExprCompute("eax", node.getExpr());

		int localVarCount = getLocalVarCount(container);

		if (!(def.getNode() instanceof LetTypeDefNode))
			throw new CompilerException("Unknown node type");

		// int argOffset = def.getNode().isArg() ? 1 : 0;

		if (node.getLet().isPointer() && node.getLet().isArrayOffset()) { // array set
			if (!def.getNode().getType().isPointer()) {
				throw new CompilerException("Var with offset is not a pointer.");
			}

			compileCalcOffset(node.getLet().getOffset());

			if (def.getNode().isiStatic()) {
				writeinstln("add ebx, " + def.getAsmName() + "  ; Adding offset for arr (static)");
				writeinstln("mov " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [ebx], eax ; load static " + def + " = " + node.getExpr());
			} else {
				writeinstln("add ebx, esp  ; Adding offset for arr");
				writeinstln("mov dword [ebx + " + (localVarCount - def.getNode().getLetIndex()) * 4 + "], eax ; load local " + def + " = " + node.getExpr());
			}
		} else if (node.getLet().isPointer() && !node.getLet().isArrayOffset()) { // pointer value set
			if (!def.getNode().getType().isPointer()) {
				throw new CompilerException("Var with offset is not a pointer.");
			}

			if (def.getNode().isiStatic()) {
				writeinstln("mov " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [esp], eax ; load static pointer " + def + " = " + node.getExpr());
			} else {
				writeinstln("mov dword [esp + " + (localVarCount - def.getNode().getLetIndex()) * 4 + "], eax ; load local pointer " + def + " = " + node.getExpr());
			}
		} else { // just set
			if(node.getExpr() instanceof LocalizingNode) { // localize var address and then set
				LocalizingNode locNode = (LocalizingNode) node.getExpr();
				VarNumNode numNode = locNode.getNode();
				
				if(numNode.isPointer()) {
					// remove pointer bc localizing a de-localized var
					numNode.getChildren().clear();
					node.getChildren().clear();
					node.add(numNode);
					compileLetTypeSetNode(node);
				}else {
					load("eax", node.getExpr());
					
					/*int scopeLetCount = getLocalVarCount(node.getClosestContainer());
					
					if (def.getNode().isiStatic()) {
						writeinstln("lea " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = " + def.getNode().getExpr());
					} else {
						writeinstln("lea " + reg + ", dword [esp + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load local 2 " + def + " = " + def.getNode().getExpr());
					}*/
				}
			}else {
				if (def.getNode().getType().isPointer() ^ node.getLet().isPointer()) {
					throw new CompilerException("Cannot set pointer to lit value. "+def.getNode()+"  "+node.getLet());
				}
			}
			
			// just set value
			

			if (def.getNode().isiStatic()) {
				writeinstln("mov " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "], eax ; load static " + def + " = " + node.getExpr());
			} else {
				writeinstln("mov dword [esp + " + (localVarCount - def.getNode().getLetIndex()) * 4 + "], eax ; load local " + def + " = " + node.getExpr());
			}
		}
	}

	/**
	 * Preserves EAX
	 */
	private void compileCalcOffset(Node node) throws CompilerException {
		if (node instanceof FunCallNode)
			writeinstln("push eax  ; Pushing to stack in case offset calc uses eax");
		compileExprCompute("ecx", node); // computing offset into ebx
		writeinstln("imul ebx, ecx, 4  ; Bc stack var size = 4B");
		if (node instanceof FunCallNode)
			writeinstln("pop eax  ; Poping from stack to get value back");
	}

	private int getLocalVarCount(ScopeContainer container) {
		return (int) (long) container.getLocalDescriptors().values().stream().map((ScopeDescriptor i) -> {
			if (i instanceof LetScopeDescriptor) {
				LetScopeDescriptor letDesc = (LetScopeDescriptor) i;
				if (letDesc.getNode() instanceof LetTypeDefNode) {
					LetTypeDefNode letNode = (LetTypeDefNode) letDesc.getNode();
					return letNode.isiStatic() ? 0 : (letNode.getType().isPointer() && letNode.getExpr() instanceof ArrayInitNode ? ((ArrayInitNode) letNode.getExpr()).getArraySize() : 1);
				}
			}
			return 0;
		}).reduce(0, (a, b) -> a + b);
	}

	private void compileMainFunDefNode(RuntimeNode node) throws CompilerException {
		FunScopeDescriptor mainDescr = (FunScopeDescriptor) node.getClosestDescriptor("main");
		FunDefNode mainNode = mainDescr.getNode();
		writeinstln("call " + mainDescr.getAsmName() + "  ; main\n");
		compileExit("eax");
		compileFunDefNode(mainNode);
	}

	private void compileStaticLetTypeDefNodes(RuntimeNode node) throws CompilerException {
		for (Node n : node.getChildren()) {
			if (n instanceof LetTypeDefNode && ((LetTypeDefNode) n).isiStatic()) {
				compileLetTypeDefNode((LetTypeDefNode) n);
			}
		}
	}

	private void compileExit(String reg) throws CompilerException {
		writeinstln("; Exit program");
		writeinstln("mov ebx, " + reg);
		writeinstln("mov eax, 1 ; Syscall exit");
		writeinstln("int 0x80   ; Syscall call");
	}

	private void compileReturnNode(ReturnNode node) throws CompilerException {
		FunDefNode fun = (FunDefNode) node.getParent(FunDefNode.class);
		if (fun == null) {
			throw new CompilerException("No parent function.");
		}

		FunScopeDescriptor funDesc = (FunScopeDescriptor) fun.getClosestDescriptor(fun.getIdent().getIdentifier());

		writeinstln("; Return");
		if (!node.returnsVoid()) {
			compileExprCompute("eax", node.getExpr());
		}

		writeinstln("jmp " + funDesc.getAsmName() + "_cln");
	}

	private void compileFunDefNode(FunDefNode node) throws CompilerException {
		String ident = node.getIdent().getIdentifier();
		FunScopeDescriptor funDesc = (FunScopeDescriptor) node.getClosestDescriptor(ident);
		writeln(funDesc.getAsmName() + ":  ; " + ident);
		if (node.isLeaf())
			return;

		for (Node n : node.getBody().getChildren()) {
			if (n instanceof ReturnNode) {
				ReturnNode rn = (ReturnNode) n;
				checkReturnTypeMatch(node, rn.getReturnType());
			}
			compile(n);
		}

		GlobalLogger.log();
		GlobalLogger.log(node);
		writeinstln("; Cleanup & Return");
		writeln(funDesc.getAsmName() + "_cln:");

		int localVarCount = getLocalVarCountWOArgs(node);

		if (localVarCount != 0) {
			writeinstln("add esp, " + (localVarCount * 4) + "  ; Removing: " + localVarCount + " var(s)");
		}
		writeinstln("ret");
	}

	private int getLocalVarCountWOArgs(FunDefNode node) {
		return (int) (long) node.getLocalDescriptors().values().stream().map((ScopeDescriptor i) -> {
			if (i instanceof LetScopeDescriptor) {
				LetScopeDescriptor letDesc = (LetScopeDescriptor) i;
				if (letDesc.getNode() instanceof LetTypeDefNode) {
					LetTypeDefNode letNode = (LetTypeDefNode) letDesc.getNode();
					if (letNode.isArg())
						return 0;
					return letNode.isiStatic() ? 0 : (letNode.getType().isPointer() && letNode.getExpr() instanceof ArrayInitNode ? ((ArrayInitNode) letNode.getExpr()).getArraySize() : 1);
				}
			}
			return 0;
		}).reduce(0, (a, b) -> a + b);
	}

	private void checkReturnTypeMatch(FunDefNode node, TypeNode rn) throws CompilerException {
		if (node.getReturnType().isGeneric() && node.getReturnType().getIdent().getType().softEquals(rn.getIdent().getType())) {
			// generic type and matches
		} else if (!node.getReturnType().isGeneric()) {
			assert false : "Not generic return type";
		} else {
			throw new CompilerException("Function return type doesn't match with return return type: " + node.getReturnType() + " and " + rn);
		}
	}

	private void compileLetTypeDefNode(LetTypeDefNode node) throws CompilerException {
		String ident = node.getIdent().getIdentifier();
		ScopeContainer container = node.getParentContainer();
		LetScopeDescriptor descr = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

		if (!node.hasExpr()) {
			warn(node.getIdent() + " is not initialized at definiton");
		}

		if (node.isiStatic()) {
			if (node.getType().getIdent().getType().softEquals(TokenType.TYPE)) { // generic type
				int memSize = MemorySize.getBytes(node.getType().getIdent().getType());
				String typeSize = getDataTypeNameBySize(memSize);
				if (typeSize == null) {
					throw new CompilerException("Cannot declare static, generic variable of type: " + node.getType().getIdent().getType());
				}

				if (node.hasExpr() && node.getExpr() instanceof StringLitNode) { // string type
					// declare in data
					writedataln(descr.getAsmName() + " " + typeSize + " \"" + ((StringLitNode) node.getExpr()).getString().getValue() + "\", 0  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at "
							+ node.getIdent().getLine() + ":" + node.getIdent().getColumn());
					writedataln(descr.getAsmName() + "_len equ $ - " + descr.getAsmName() + " ; " + node.getType().getIdent().getType().getStringValue() + " length " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
				} else { // int type
					if (node.hasExpr()) {
						writeinstln("; Setup static: " + ident + " -> " + descr.getAsmName());

						if (node.getExpr() instanceof ArrayInitNode && ((ArrayInitNode) node.getExpr()).isEmpty()) {
							// declare in data
							writedataln(descr.getAsmName() + " " + typeSize + " " + ((ArrayInitNode) node.getExpr()).getArraySize() + " dup (0)  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at "
									+ node.getIdent().getLine() + ":" + node.getIdent().getColumn());
						} else {
							// declare in data
							writedataln(descr.getAsmName() + " " + typeSize + " 0  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
							// setup in _start
							compileExprCompute(getMovTypeNameBySize(MemorySize.getBytes(node.getType().getIdent().getType())) + " [" + descr.getAsmName() + "]", node.getExpr());
						}
					}
				}
			}
		} else { // local variable
			if (node.getType().getIdent().getType().softEquals(TokenType.TYPE)) { // generic type

				if (node.getType().isPointer()) {
					if (node.hasExpr() && node.getExpr() instanceof ArrayInitNode) { // array
						ArrayInitNode arrInit = (ArrayInitNode) node.getExpr();
						if (arrInit.isEmpty()) { // alloc space
							writeinstln("; Setup local: " + ident);
							writeinstln("sub esp, " + (4 * arrInit.getArraySize()));
						} else { // alloc space & set
							throw new CompilerException("//TODO: Set array values");
						}
					} else {
						throw new CompilerException("//TODO: Set pointer value & not array");
					}

				} else {

					if (node.hasExpr()) { // alloc space and init
						writeinstln("; Setup local: " + ident);
						compileExprCompute("eax", node.getExpr());
						writeinstln("push eax");
					} else { // alloc space
						writeinstln("; Setup local: " + ident);
						writeinstln("sub esp, 4");
					}

				}
			}
		}
	}

	private void compileFunCallNode(FunCallNode node) throws CompilerException {
		String funName = node.getName().getIdentifier();

		if (node.isPreset()) {
			if (funName.equals("exit")) {
				writeinstln("; Exit program");
				Node arg0 = node.getArgs().getChildren().get(0);
				if (arg0 instanceof FunArgValNode) {
					Node arg0node = ((FunArgValNode) arg0).getExpression();
					compileExprCompute("ebx", arg0node);
				} else {
					assert false;
				}
				writeinstln("mov eax, 1 ; Syscall exit");
				writeinstln("int 0x80   ; Syscall call");
			} else if (funName.equals("asm")) {
				StringLitNode arg0 = (StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression();
				writeinstln(arg0.getString().getValue());
			} else if (funName.equals("data")) {
				StringLitNode arg0 = (StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression();
				writedataln(arg0.getString().getValue());
			} else if (funName.equals("printout")) {
				String arg0 = ((VarNumNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression()).getIdent().getIdentifier();
				if (!node.getClosestContainer().containsDescriptor(arg0)) {
					throw new CompilerException("String buffer " + arg0 + " not defined");
				}
				LetScopeDescriptor def = (LetScopeDescriptor) node.getClosestContainer().getClosestDescriptor(arg0);
				writeinstln(";  Printout");
				writeinstln("mov eax, 4");
				writeinstln("mov ebx, 1");
				writeinstln("mov ecx, " + def.getAsmName());
				writeinstln("mov edx, " + def.getAsmName() + "_len");
				writeinstln("int 0x80");
			}
		} else {

			if (!node.getParentContainer().containsDescriptor(funName)) {
				throw new CompilerException("Function: " + funName + ", not found in scope.");
			}

			FunDefNode fun = ((FunScopeDescriptor) node.getParentContainer().getClosestDescriptor(funName)).getNode();
			int wantedArgCount = fun.getArgs().getChildren().size();

			FunArgsValNode args = node.getArgs();
			int gotArgCount = args.getChildren().size();

			if (wantedArgCount != gotArgCount) {
				throw new CompilerException("Function: " + funName + " expected " + wantedArgCount + " arguments, got " + gotArgCount);
			}

			writeinstln("; Call: " + fun.getIdent().getIdentifier());

			for (int i = wantedArgCount - 1; i >= 0; i--) {
				compileExprCompute("eax", ((FunArgValNode) args.getChildren().get(i)).getExpression());
				writeinstln("push eax ; adding arg: " + ((LetTypeDefNode) fun.getArgs().getChildren().get(i)).getIdent().getIdentifier());
			}

			writeinstln("call " + node.getParentContainer().getClosestDescriptor(funName).getAsmName() + "  ; " + funName);

			/*
			 * for (int i = 0; i < wantedArgCount; i++) {
			 * writeinstln("add esp, 4  ; removing arg: " + ((LetTypeDefNode)
			 * fun.getArgs().getChildren().get(i)).getIdent().getIdentifier()); }
			 */

			writeinstln("add esp, " + (4 * wantedArgCount) + "  ; removing " + wantedArgCount + " arg(s)");
		}
	}

	private void compileExprCompute(String reg, Node node) throws CompilerException {
		if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, node);
			writeinstln("");
		} /*else if (node instanceof NumLitNode) {
			long val = (long) ((NumLitNode) node).getValue();
			writeinstln("mov " + reg + ", " + val + "  ; compileExprCompute " + ((NumLitNode) node).getValue());
		} */else if (node instanceof VarNumNode || node instanceof NumLitNode || node instanceof LocalizingNode) {
			load(reg, node);
		} else if (node instanceof FunCallNode) {
			FunCallNode funCall = (FunCallNode) node;
			compileFunCallNode(funCall);
			if (!reg.equals("eax"))
				writeinstln("mov " + reg + ", eax");
		} else {
			throw new CompilerException("Expression not implemented: " + node);
		}
	}

	private void generateExprRecursive(String to, Node node) throws CompilerException {
		if (node instanceof BinaryOpNode) {
			BinaryOpNode binaryNode = (BinaryOpNode) node;

			Node left = binaryNode.getLeft();
			Node right = binaryNode.getRight();

			if (right instanceof BinaryOpNode) {
				generateExprRecursive("ebx", ((BinaryOpNode) right));
			}
			if (left instanceof BinaryOpNode) {
				generateExprRecursive("eax", ((BinaryOpNode) left));
			}

			if (left instanceof NumLitNode || left instanceof VarNumNode) {
				load("eax", left);
			}
			if (right instanceof NumLitNode || right instanceof VarNumNode) {
				load("ebx", right);
			}

			TokenType operator = binaryNode.getOperator();

			switch (operator) {
			case PLUS:
				writeinstln("add eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MINUS:
				writeinstln("sub eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MODULO:
			case DIV:
				writeinstln("xor edx, edx  ; zero-ing out edx for divison");
				writeinstln("idiv ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MUL:
				writeinstln("imul eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			default:
				throw new CompilerException("Operation not supported: " + operator);
			}

			if (TokenType.MODULO.equals(operator)) {
				writeinstln("mov " + to + ", edx");
			} else if (to != "eax") {
				writeinstln("mov " + to + ", eax");
			}

		} else if (node instanceof NumLitNode) {

		} else {
			throw new IllegalArgumentException("Unknown node type: " + node.getClass().getSimpleName());
		}

	}

	private void load(String reg, Node node) throws CompilerException {
		if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			String ident = numNode.getIdent().getIdentifier();

			if (!node.getParentContainer().containsDescriptor(ident)) {
				throw new CompilerException("Couldn't find: " + numNode.getIdent().getIdentifier() + " in scope: " + node.getParentContainer() + " (" + numNode.getIdent().getLine() + ":" + numNode.getIdent().getColumn() + ")");
			}

			LetScopeDescriptor def = (LetScopeDescriptor) node.getClosestContainer().getClosestDescriptor(ident);

			int scopeLetCount = getLocalVarCount(node.getClosestContainer());

			if (numNode.isPointer()) { // is array var

				if (numNode.isArrayOffset()) { // is array

					writeinstln("; Compute offset into ebx");
					writeinstln("push eax  ; Pushing to stack in case offset calc uses eax");
					compileCalcOffset(numNode.getOffset());
					writeinstln("pop eax  ; Poping from stack to get value back");
					if (def.getNode().isiStatic()) {
						writeinstln("lea ecx, " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; Load static address");
						writeinstln("add ebx, ecx  ; Add static address to offset ebx");
					} else {
						writeinstln("add ebx, esp  ; Add pointer to offset ebx");
					}

					if (def.getNode().hasExpr()) { // let
						if (def.getNode().isiStatic()) {
							writeinstln("mov " + reg + ", [ebx]  ; load static " + def + " = " + def.getNode().getExpr());
						} else {
							writeinstln("mov " + reg + ", [ebx + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load local 1 " + def + " = " + def.getNode().getExpr());
						}
					} else if (!def.getNode().hasExpr()) { // arg
						writeinstln("mov " + reg + ", [ebx + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load arg 1 " + def + " = stack index " + def.getNode().getLetIndex());
					}

				} else { // is regular pointer
					if (def.getNode().isiStatic()) {
						writeinstln("lea ebx, " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; Load static address");
					} else {
						writeinstln("mov ebx, esp  ; Add pointer to offset ebx");
					}

					if (def.getNode().hasExpr()) { // let
						if (def.getNode().isiStatic()) {
							writeinstln("mov " + reg + ", [ebx]  ; load static " + def + " = " + def.getNode().getExpr());
						} else {
							writeinstln("mov " + reg + ", [ebx + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load local 1 " + def + " = " + def.getNode().getExpr());
						}
					} else if (!def.getNode().hasExpr()) { // arg
						writeinstln("mov " + reg + ", [ebx + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load arg 1 " + def + " = stack index " + def.getNode().getLetIndex());
					}
				}

			} else { // is direct var

				if (def.getNode().hasExpr()) { // let
					if (def.getNode().isiStatic()) {
						writeinstln("mov " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = " + def.getNode().getExpr());
					} else {
						writeinstln("mov " + reg + ", dword [esp + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load local 2 " + def + " = " + def.getNode().getExpr());
					}
				} else if (def.getNode() instanceof LetTypeDefNode && !def.getNode().hasExpr()) { // arg // TODO
					writeinstln("mov " + reg + ", dword [esp + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load arg 2 " + def + " = stack index " + def.getNode().getLetIndex());
				}

			}
		} else if (node instanceof NumLitNode) {
			writeinstln("mov " + reg + ", " + ((NumLitNode) node).getValue());
		} else if (node instanceof LocalizingNode) {
			LocalizingNode locNode = (LocalizingNode) node;
			VarNumNode numNode = locNode.getNode();
			
			if(numNode.isPointer()) {
				// remove pointer bc localizing a de-localized var
				numNode.getChildren().clear();
				load(reg, numNode);
			}else {
				String ident = numNode.getIdent().getIdentifier();

				if (!node.getParentContainer().containsDescriptor(ident)) {
					throw new CompilerException("Couldn't find: " + numNode.getIdent().getIdentifier() + " in scope: " + node.getParentContainer() + " (" + numNode.getIdent().getLine() + ":" + numNode.getIdent().getColumn() + ")");
				}

				LetScopeDescriptor def = (LetScopeDescriptor) node.getClosestContainer().getClosestDescriptor(ident);
				
				int scopeLetCount = getLocalVarCount(node.getClosestContainer());
				
				if (def.getNode().isiStatic()) {
					writeinstln("lea " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(def.getNode().getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = " + def.getNode().getExpr());
				} else {
					writeinstln("lea " + reg + ", dword [esp + " + (scopeLetCount - def.getNode().getLetIndex()) * 4 + "]  ; load local 2 " + def + " = " + def.getNode().getExpr());
				}
			}
		}

	}

	private String getDataTypeNameBySize(int bytes) {
		return "dd";
		/*
		 * switch (bytes) { case 1: return "db"; case 2: return "dw"; case 4: return
		 * "dd"; case 8: return "dq"; } return null;
		 */
	}

	private String getMovTypeNameBySize(int bytes) {
		return "dword";
		/*
		 * switch (bytes) { case 1: return "byte"; case 2: return "word"; case 4: return
		 * "dword"; case 8: return "qword"; } return null;
		 */
	}

	private void warn(String string) {
		GlobalLogger.warning(string);
	}

}
