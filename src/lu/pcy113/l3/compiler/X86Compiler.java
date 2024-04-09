package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.l3.utils.MemorySize;

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
		String ident = node.getIdent().getIdentifier();
		ScopeContainer container = node.getParentContainer();
		LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

		// System.out.println("let: "+def.getClass().getSimpleName() + " and from: " +
		// def.getNode().getClass().getSimpleName()+" new:
		// "+node.getExpr().getClass().getSimpleName());

		compileExprCompute("eax", node.getExpr());

		if (def.getNode() instanceof LetTypeDefNode) {
			if (((LetTypeDefNode) def.getNode()).isiStatic()) {
				writeinstln("mov " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getIdent().getType())) + " [" + def.getAsmName() + "], eax ; load static " + def + " = "
						+ ((LetTypeDefNode) def.getNode()).getExpr());
			} else {
				writeinstln("mov dword [esp + " + (((LetTypeDefNode) def.getNode()).getLetIndex()) * 4 + "], eax ; load local " + def + " = " + ((LetTypeDefNode) def.getNode()).getExpr());
			}
		} else if (def.getNode() instanceof FunArgDefNode) {
			writeinstln("mov dword [esp + " + (((FunArgDefNode) def.getNode()).getArgIndex() + 1) * 4 + "], eax  ; load arg " + def + " = stack index " + ((FunArgDefNode) def.getNode()).getArgIndex()); // TODO: calculate offset by arg
																																																			// index + size
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
		writeinstln("; Return");
		compileExprCompute("eax", node.getExpr());
		writeinstln("ret");
	}

	private void compileFunDefNode(FunDefNode node) throws CompilerException {
		String ident = node.getIdent().getIdentifier();
		writeln(node.getClosestDescriptor(ident).getAsmName() + ":  ; " + ident);
		if (node.isLeaf())
			return;

		for (ScopeDescriptor sd : node.getLocalDescriptors().values()) {
			if (sd instanceof LetScopeDescriptor) {
				// writeinstln("pop eax");
			}
		}

		for (Node n : node.getBody().getChildren()) {
			compile(n);
		}

		if (!(node.getBody().getChildren().getLast() instanceof ReturnNode)) {
			writeinstln("ret  ; Default return");
		}
	}

	private void compileLetTypeDefNode(LetTypeDefNode node) throws CompilerException {
		String ident = node.getIdent().getIdentifier();
		ScopeContainer container = node.getParentContainer();
		LetScopeDescriptor descr = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

		if (node.isiStatic()) {
			if (node.getType().getIdent().getType().softEquals(TokenType.TYPE)) { // generic type
				int memSize = MemorySize.getBytes(node.getType().getIdent().getType());
				String typeSize = getDataTypeNameBySize(memSize);
				if (typeSize == null) {
					throw new CompilerException("Cannot declare static, generic variable of type: " + node.getType().getIdent().getType());
				}

				if (node.getExpr() instanceof StringLitNode) { // string type
					// declare in data
					writedataln(descr.getAsmName()+ " " + typeSize + " \"" + ((StringLitNode) node.getExpr()).getString().getValue() + "\", 0  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at " + node.getIdent().getLine() + ":"
							+ node.getIdent().getColumn());
					writedataln(descr.getAsmName() + "_len equ $ - " + descr.getAsmName() + " ; " + node.getType().getIdent().getType().getStringValue() + " length " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
				} else { // int type
					// declare in data
					writedataln(descr.getAsmName() + " " + typeSize + " 0  ; " + node.getType().getIdent().getType().getStringValue() + " " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());

					// setup in _start
					writeinstln("; Setup static: " + ident + " -> " + descr.getAsmName());
					compileExprCompute(getMovTypeNameBySize(MemorySize.getBytes(node.getType().getIdent().getType())) + " [" + descr.getAsmName() + "]", node.getExpr());
				}
			}
		} else {
			if (node.getType().getIdent().getType().softEquals(TokenType.TYPE)) { // generic type
				writeinstln("; Setup local: " + ident);
				compileExprCompute("eax", node.getExpr());
				writeinstln("push eax");
			}
		}
	}

	private void compileFunCallNode(FunCallNode node) throws CompilerException {
		if (node.isPreset()) {
			if (node.getName().getIdentifier().equals("exit")) {
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
			} else if (node.getName().getIdentifier().equals("asm")) {
				StringLitNode arg0 = (StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression();
				writeinstln(arg0.getString().getValue());
			} else if (node.getName().getIdentifier().equals("data")) {
				StringLitNode arg0 = (StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression();
				writedataln(arg0.getString().getValue());
			} else if (node.getName().getIdentifier().equals("printout")) {
				String arg0 = ((VarNumNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpression()).getIdent().getIdentifier();
				if(!node.getClosestContainer().containsDescriptor(arg0)) {
					throw new CompilerException("String buffer "+arg0+" not defined");
				}
				LetScopeDescriptor def = (LetScopeDescriptor) node.getClosestContainer().getClosestDescriptor(arg0);
				writeinstln(";  Printout");
				writeinstln("mov eax, 4");
				writeinstln("mov ebx, 1");
				writeinstln("mov ecx, "+def.getAsmName());
				writeinstln("mov edx, "+def.getAsmName()+"_len");
				writeinstln("int 0x80");
			}
		} else {
			FunDefNode fun = ((FunScopeDescriptor) node.getParentContainer().getClosestDescriptor(node.getName().getIdentifier())).getNode();
			int wantedArgCount = fun.getLocalDescriptors().size();

			FunArgsValNode args = node.getArgs();
			int gotArgCount = args.getChildren().size();

			if (wantedArgCount != gotArgCount) {
				throw new CompilerException("Function: " + node.getName().getIdentifier() + " expected " + wantedArgCount + " arguments, got " + gotArgCount);
			}

			writeinstln("; Call: " + fun.getIdent().getIdentifier());

			for (int i = wantedArgCount - 1; i >= 0; i--) {
				compileExprCompute("eax", ((FunArgValNode) args.getChildren().get(i)).getExpression());
				writeinstln("push eax ; adding arg: " + ((FunArgDefNode) fun.getArgs().getChildren().get(i)).getIdent().getIdentifier());
			}

			writeinstln("call " + node.getParentContainer().getClosestDescriptor(node.getName().getIdentifier()).getAsmName() + "  ; " + node.getName().getIdentifier());

			for (int i = 0; i < wantedArgCount; i++) {
				writeinstln("pop eax  ; removing arg: " + ((FunArgDefNode) fun.getArgs().getChildren().get(i)).getIdent().getIdentifier());
			}
		}
	}

	private void compileExprCompute(String reg, Node node) throws CompilerException {
		System.err.println(node + " -> " + reg);
		if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, node);
			writeinstln("");
		} else if (node instanceof NumLitNode) {
			long val = (long) ((NumLitNode) node).getValue();
			writeinstln("mov " + reg + ", " + val + "  ; compileExprCompute " + ((NumLitNode) node).getValue());
		} else if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			load(reg, node);
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
		if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			String ident = numNode.getIdent().getIdentifier();

			if (!node.getParentContainer().containsDescriptor(ident)) {
				throw new CompilerException("Couldn't find: " + numNode.getIdent().getIdentifier() + " in scope: " + node.getParentContainer() + " (" + numNode.getIdent().getLine() + ":" + numNode.getIdent().getColumn() + ")");
			}

			LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);

			if (def.getNode() instanceof LetTypeDefNode) {
				System.out.println("load: " + def.getClass().getSimpleName() + " and node: " + def.getNode().getClass().getSimpleName());
				if (((LetTypeDefNode) def.getNode()).isiStatic()) {
					writeinstln("mov " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getIdent().getType())) + " [" + def.getAsmName() + "]  ; load static " + def + " = "
							+ ((LetTypeDefNode) def.getNode()).getExpr());
				} else {
					writeinstln("mov " + reg + ", dword [esp + " + (((LetTypeDefNode) def.getNode()).getLetIndex()) * 4 + "]  ; load local " + def + " = " + ((LetTypeDefNode) def.getNode()).getExpr());
				}
			} else if (def.getNode() instanceof FunArgDefNode) {
				writeinstln("mov " + reg + ", dword [esp + " + (((FunArgDefNode) def.getNode()).getArgIndex() + 1) * 4 + "]  ; load arg " + def + " = stack index " + ((FunArgDefNode) def.getNode()).getArgIndex()); // TODO: calculate offset by arg
																																																						// index + size
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

}
