package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgNumLitValueNode;
import lu.pcy113.l3.parser.ast.FunArgTypeDefNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
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
				if(n instanceof FunDefNode && !((FunDefNode) n).getIdent().getIdentifier().equals("main")) {
					compileFunDefNode((FunDefNode) n);
				}
			}
		} else if (node instanceof FunCallNode) {
			compileFunCallNode((FunCallNode) node);
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDefNode((LetTypeDefNode) node);
		}else if(node instanceof ReturnNode) {
			compileReturnNode((ReturnNode) node);
		}
	}

	private void compileMainFunDefNode(RuntimeNode node) throws CompilerException {
		FunScopeDescriptor mainDescr = (FunScopeDescriptor) node.getClosestDescriptor("main");
		FunDefNode mainNode = mainDescr.getNode();
		writeinstln("call "+mainDescr.getAsmName()+"  ; main\n");
		compileExit("eax");
		compileFunDefNode(mainNode);
	}

/*
 * push eax
 * push ebx
 * push ecx
 * push edx
 * ENTER FUN:
 * call fun
 * mov eax, ret
 * LEAVE FUN:
 * ret
 */
	
	private void compileStaticLetTypeDefNodes(RuntimeNode node) throws CompilerException {
		for (Node n : node.getChildren()) {
			if(n instanceof LetTypeDefNode && ((LetTypeDefNode) n).isiStatic()) {
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
		compileExprCompute("eax", node.getExpr());
		writeinstln("ret");
	}

	private void compileFunDefNode(FunDefNode node) throws CompilerException {
		String ident = node.getIdent().getIdentifier();
		writeln(node.getClosestDescriptor(ident).getAsmName() + ":  ; " + ident);
		if(node.isLeaf())
			return;
		
		for(ScopeDescriptor sd : node.getLocalDescriptors().values()) {
			if(sd instanceof LetScopeDescriptor) {
				// writeinstln("pop eax");
			}
		}
		
		for (Node n : node.getChildren()) {
			compile(n);
		}
	}

	private void compileLetTypeDefNode(LetTypeDefNode node) throws CompilerException {
		if (node.isiStatic()) {
			String ident = node.getIdent().getIdentifier();

			ScopeContainer container = node.getParentContainer();
			LetScopeDescriptor descr = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);
			/*if (descr != null) {
				throw new CompilerException("Variable: " + node.getIdent().getIdentifier() + ", already defined: " + descr.getIdent());
			}*/

			if (node.getType().getType().softEquals(TokenType.TYPE)) { // generic type
				String typeSize = getDataTypeNameBySize(MemorySize.getBytes(node.getType().getType()));
				if (typeSize == null) {
					throw new CompilerException("Cannot declare static, generic variable of type: " + node.getType());
				}
				writedataln(descr.getAsmName() + " " + typeSize + " 0  ; " + node.getType().getType().getStringValue() + " " + ident + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
			}

			writeinstln("; Setup static: " + ident + " -> " + descr.getAsmName());
			compileExprCompute(getMovTypeNameBySize(MemorySize.getBytes(node.getType().getType())) + " [" + descr.getAsmName() + "]", node.getChildren().get(0));
		}
	}

	private void compileFunCallNode(FunCallNode node) throws CompilerException {
		if (node.isPreset()) {
			if (node.getName().getIdentifier().equals("exit")) {
				writeinstln("; Exit program");
				Node arg0 = node.getChildren().get(0);
				if (arg0 instanceof FunArgNumLitValueNode) {
					Node arg0node = ((FunArgNumLitValueNode) arg0).getNode();
					compileExprCompute("ebx", arg0node);
				} else {
					assert false;
				}
				writeinstln("mov eax, 1 ; Syscall exit");
				writeinstln("int 0x80   ; Syscall call");
			}
		}else {
			FunDefNode fun = ((FunScopeDescriptor) node.getParentContainer().getClosestDescriptor(node.getName().getIdentifier())).getNode();
			int wantedArgCount = fun.getLocalDescriptors().size();
			int gotArgCount = fun.getChildren().size();
			if(wantedArgCount != gotArgCount) {
				throw new CompilerException("Function: " + node.getName().getIdentifier() + " expected " + wantedArgCount + " arguments, got " + gotArgCount);
			}
			
			for (int i = 0; i < wantedArgCount; i++) {
				compileExprCompute("eax", ((FunArgNumLitValueNode) node.getChildren().get(i)).getNode());
				writeinstln("push eax");
			}
			
			writeinstln("call " + node.getParentContainer().getClosestDescriptor(node.getName().getIdentifier()).getAsmName() + "  ; " + node.getName().getIdentifier());
		}
	}

	private void compileExprCompute(String reg, Node node) throws CompilerException {
		System.err.println("compile expr compute: "+reg+" "+node);
		if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, node);
			// writeinstln("mov " + reg + ", " + "ebx");
		} else if (node instanceof NumLitNode) {
			long val = (long) ((NumLitNode) node).getValue();
			writeinstln("mov " + reg + ", " + val);
		} else if (node instanceof VarNumNode) {
			VarNumNode numNode = (VarNumNode) node;
			load(reg, node);
		} else {
			throw new CompilerException("Expression not implemented: "+node);
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
				writeinstln("add eax, ebx ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MINUS:
				writeinstln("sub eax, ebx ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MODULO:
			case DIV:
				writeinstln("div ebx ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			case MUL:
				writeinstln("imul eax, ebx ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + to);
				break;
			default:
				throw new CompilerException("Operation not supported: " + operator);
			}

			if (TokenType.MODULO.equals(operator)) {
				writeinstln("mov " + to + ", edx\n");
			} else if (to != "eax") {
				writeinstln("mov " + to + ", eax\n");
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
				throw new CompilerException("Couldn't find: " + numNode.getIdent().getIdentifier() + " in scope: "+node.getParentContainer()+" (" + numNode.getIdent().getLine() + ":" + numNode.getIdent().getColumn() + ")");
			}

			LetScopeDescriptor def = (LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(ident);
			
			if(def.getNode() instanceof LetTypeDefNode) {
				writeinstln("mov " + reg + ", " + getMovTypeNameBySize(MemorySize.getBytes(((LetTypeDefNode) def.getNode()).getType().getType())) + " [" + def.getAsmName() + "]");	
			}else if(def.getNode() instanceof FunArgTypeDefNode) {
				writeinstln("mov " + reg + ", dword [esp + "+(((FunArgTypeDefNode) def.getNode()).getArgIndex()+1)*4+"]"); // TODO: calculate offset by arg index + size
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
