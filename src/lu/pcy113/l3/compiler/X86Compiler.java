package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
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

	private int heapSize = 2048;

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writeln("section .text");
		writeinstln("global _start");
		writeln("_start:");

		compile(root);

		appendData();

		flushAndClose();

		File dir = outFile.getAbsoluteFile().getParentFile();
		try {
			exec("nasm -f elf32 " + outFile.getPath(), dir);
			exec("ld -m elf_i386 -o " + FileUtils.removeExtension(outFile.getName()) + " " + FileUtils.replaceExtension(outFile.getName(), "o"), dir);
			exec("./" + FileUtils.removeExtension(outFile.getName()), dir);
		} catch (IOException | InterruptedException e) {
			throw new CompilerException("Could not exec: '" + outFile + "', '" + FileUtils.removeExtension(outFile.getName()) + "' and '" + FileUtils.replaceExtension(outFile.getName(), "o") + "' in " + dir, e);
		}
	}

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
			throw new CompilerException("Unkown node: " + node);
		}
	}

	private void compileLetTypeSetNode(LetTypeSetNode node) throws CompilerException {
		String ident = node.getLet().getIdent().getIdentifier();
		ScopeContainer container = node.getParentContainer();
		LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

		System.out.println("let: " + def.getClass().getSimpleName() + " and from: " + def.getNode().getClass().getSimpleName() + " new: " + node.getExpr().getClass().getSimpleName());

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
		
		writeinstln("add ebx, esp");

		if (def.getNode() instanceof LetTypeDefNode) {
			if (((LetTypeDefNode) def.getNode()).isiStatic()) {
				writeinstln("mov " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getIdent().getType())) + " [" + def.getAsmName() + "], eax ; load static " + def + " = "
						+ ((LetTypeDefNode) def.getNode()).getExpr());
			} else {
				writeinstln("mov dword [ebx + " + (((LetTypeDefNode) def.getNode()).getLetIndex()) * 4 + "], eax ; load local " + def + " = " + ((LetTypeDefNode) def.getNode()).getExpr());
			}
		} else if (def.getNode() instanceof LetTypeDefNode) {
			writeinstln("mov dword [ebx + " + (((LetTypeDefNode) def.getNode()).getLetIndex() + 1) * 4 + "], eax  ; load arg " + def + " = stack index " + ((LetTypeDefNode) def.getNode()).getLetIndex()); // TODO remove ?
		}
	}

	private void compileMainFunDefNode(RuntimeNode node) throws CompilerException {
		FunScopeDescriptor mainDescr = (FunScopeDescriptor) node.getClosestDescriptor("main");
		FunDefNode mainNode = mainDescr.getNode();
		writeinstln("call " + mainDescr.getAsmName() + "  ; main\n");
		compileExit("eax");
		compileFunDefNode(mainNode);
	}

	/*
	 * push eax push ebx push ecx push edx ENTER FUN: call fun mov eax, ret LEAVE
	 * FUN: ret
	 */

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

		GlobalLogger.log();
		GlobalLogger.log(node);
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

		for (ScopeDescriptor sd : node.getLocalDescriptors().values()) {
			if (sd instanceof LetScopeDescriptor) {
				// writeinstln("pop eax");
			}
		}

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

		int localVarCount = (int) (long) node.getLocalDescriptors().values().stream().map((ScopeDescriptor i) -> {
			if (i instanceof LetScopeDescriptor) {
				LetScopeDescriptor letDesc = (LetScopeDescriptor) i;
				if (letDesc.getNode() instanceof LetTypeDefNode) {
					LetTypeDefNode letNode = (LetTypeDefNode) letDesc.getNode();
					if (node.getArgs().getChildren().contains(letNode))
						return 0; // ignore bc argument and not local variable
					return letNode.isiStatic() ? 0 : (letNode.getType().isPointer() && letNode.getExpr() instanceof ArrayInitNode ? ((ArrayInitNode) letNode.getExpr()).getArraySize() : 1);
				}
			}
			return 0;
		}).reduce(0, (a, b) -> a + b);

		if (localVarCount != 0) {
			writeinstln("add esp, " + (localVarCount * 4) + "  ; Removing: " + localVarCount + " var(s)");
		}
		writeinstln("ret");

		/*
		 * if (!(node.getBody().getChildren().getLast() instanceof ReturnNode)) {
		 * writeinstln("ret  ; Default return"); }
		 */
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
					if(node.hasExpr()) {
						writeinstln("; Setup static: " + ident + " -> " + descr.getAsmName());
						
						if(node.getExpr() instanceof ArrayInitNode && ((ArrayInitNode) node.getExpr()).isEmpty()) {
							// declare in data
							writedataln(descr.getAsmName() + " " + typeSize + " "+((ArrayInitNode) node.getExpr()).getArraySize()+" dup (0)  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
						}else {
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
							assert false : "//TODO: Set array values";
						}
					} else {
						assert false : "//TODO: Set pointer value & not array";
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
			int wantedArgCount = fun.getLocalDescriptors().size();

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
		GlobalLogger.log();
		GlobalLogger.log("reg: " + reg + " > " + node);
		if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, node);
			writeinstln("");
		} else if (node instanceof NumLitNode) {
			long val = (long) ((NumLitNode) node).getValue();
			writeinstln("mov " + reg + ", " + val + "  ; compileExprCompute " + ((NumLitNode) node).getValue());
		} else if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			load(reg, node);
		} else if (node instanceof FunCallNode) {
			FunCallNode funCall = (FunCallNode) node;
			compileFunCallNode(funCall);
		} else { 
			throw new CompilerException("Expression not implemented: " + node);
		}
	}

	private void generateExprRecursive(String to, Node node) throws CompilerException {
		if (node instanceof BinaryOpNode) {
			BinaryOpNode binaryNode = (BinaryOpNode) node;

			Node left = binaryNode.getLeft();
			Node right = binaryNode.getRight();

			System.out.println(left + " " + binaryNode.getOperator().getValue() + " " + right);

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
		GlobalLogger.log();
		GlobalLogger.log("reg: " + reg + " > " + node);

		if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			String ident = numNode.getIdent().getIdentifier();

			if (!node.getParentContainer().containsDescriptor(ident)) {
				throw new CompilerException("Couldn't find: " + numNode.getIdent().getIdentifier() + " in scope: " + node.getParentContainer() + " (" + numNode.getIdent().getLine() + ":" + numNode.getIdent().getColumn() + ")");
			}

			LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

			if (numNode.hasOffset()) { // is array var
				
				compileExprCompute("ecx", numNode.getOffset());
				writeinstln("lea ebx, dword [esp + ecx]");
				
				if (def.getNode() instanceof LetTypeDefNode && ((LetTypeDefNode) def.getNode()).hasExpr()) { // let
					System.out.println("load: " + def.getClass().getSimpleName() + " and node: " + def.getNode().getClass().getSimpleName());
					if (((LetTypeDefNode) def.getNode()).isiStatic()) {
						writeinstln("mov " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = "
								+ ((LetTypeDefNode) def.getNode()).getExpr());
					} else {
						writeinstln("mov " + reg + ", dword [ebx + " + (((LetTypeDefNode) def.getNode()).getLetIndex()) * 4 + "]  ; load local " + def + " = " + ((LetTypeDefNode) def.getNode()).getExpr());
					}
				} else if (def.getNode() instanceof LetTypeDefNode && !((LetTypeDefNode) def.getNode()).hasExpr()) { // arg
					writeinstln("mov " + reg + ", dword [ebx + " + (((LetTypeDefNode) def.getNode()).getLetIndex() + 1) * 4 + "]  ; load arg " + def + " = stack index " + ((LetTypeDefNode) def.getNode()).getLetIndex());
				}
				
			} else { // is direct var
				
				if (def.getNode() instanceof LetTypeDefNode && ((LetTypeDefNode) def.getNode()).hasExpr()) { // let
					System.out.println("load: " + def.getClass().getSimpleName() + " and node: " + def.getNode().getClass().getSimpleName());
					if (((LetTypeDefNode) def.getNode()).isiStatic()) {
						writeinstln("mov " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = "
								+ ((LetTypeDefNode) def.getNode()).getExpr());
					} else {
						writeinstln("mov " + reg + ", dword [esp + " + (((LetTypeDefNode) def.getNode()).getLetIndex()) * 4 + "]  ; load local " + def + " = " + ((LetTypeDefNode) def.getNode()).getExpr());
					}
				} else if (def.getNode() instanceof LetTypeDefNode && !((LetTypeDefNode) def.getNode()).hasExpr()) { // arg
					writeinstln("mov " + reg + ", dword [esp + " + (((LetTypeDefNode) def.getNode()).getLetIndex() + 1) * 4 + "]  ; load arg " + def + " = stack index " + ((LetTypeDefNode) def.getNode()).getLetIndex()); // TODO: calculate offset by
																																																							// arg
																																																							// index + size
				}
				
			}
		} else if (node instanceof NumLitNode) {
			writeinstln("mov " + reg + ", " + ((NumLitNode) node).getValue());
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
