package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
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
import lu.pcy113.pclib.GlobalLogger;

public class X86Compiler extends L3Compiler {

	private int heapSize;

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writetextln("global _start");
		writeln("_start:");

		// writedataln("heap dd " + heapSize + " dup (0)");

		compile(root);

		appendText();
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

	private int STACK_POS = 0;

	private void compile(Node node) throws CompilerException {
		GlobalLogger.log();

		ScopeContainer container = node.getClosestContainer();

		if (node instanceof RuntimeNode) {
			// Setup static vars
			for (ScopeDescriptor desc : container.getLocalDescriptors().values()) {
				if (desc instanceof LetScopeDescriptor) {
					if (((LetTypeDefNode) ((LetScopeDescriptor) desc).getNode()).isiStatic()) {
						compileStaticLetTypeDef((LetScopeDescriptor) desc);
					}
				}
			}

			if (!container.localContainsDescriptor("main")) {
				throw new CompilerException("Cannot find main function");
			} else {
				FunScopeDescriptor desc = (FunScopeDescriptor) container.getLocalDescriptor("main");
				desc.setAsmName("main");
				writetextln("global main");

				writeinstln("call " + desc.getAsmName() + "  ; Call main");

				writeinstln("; Exit program");
				writeinstln("mov ebx, eax  ; Move return to ebx");
				writeinstln("mov eax, 1 ; Syscall exit");
				writeinstln("int 0x80   ; Syscall call");

				compileMainFun(desc);
			}

			for (Node n : node.getChildren()) {
				if (n instanceof LetTypeDefNode && !((LetTypeDefNode) n).isiStatic()) {
					throw new CompilerException("Cannot declare non-static let in RuntimeNode.");
				} else if (n instanceof LetTypeDefNode && ((LetTypeDefNode) n).isiStatic()) {
					continue; // already declared
				} else if (n instanceof FunDefNode && ((FunDefNode) n).getIdent().getIdentifier().equals("main")) {
					continue; // already declared
				}

				compile(n);
			}
		} else if (node instanceof FunDefNode) {
			compileFunDef((FunScopeDescriptor) container.getClosestDescriptor(((FunDefNode) node).getIdent().getIdentifier()));
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDef((LetScopeDescriptor) container.getClosestDescriptor(((LetTypeDefNode) node).getIdent().getIdentifier()));
		} else if (node instanceof LetTypeSetNode) {
			compileLetTypeSet((LetTypeSetNode) node);
		} else if (node instanceof FunCallNode) {
			compileFunCall((FunCallNode) node);
		} else {
			implement(node);
		}
	}

	private void compileLetTypeSet(LetTypeSetNode node) throws CompilerException {
		ScopeContainer container = node.getClosestContainer();
		LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(node.getLet().getIdent().getIdentifier());
		LetTypeDefNode def = desc.getNode();

		compileComputeExpr("eax", node.getExpr());

		if (node.getLet().isPointer() && node.getLet().isArrayOffset()) { // array set
			compileComputeExpr("ebx", node.getLet().getOffset());
			writeinstln("imul ebx, 4");

			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + " + ebx], eax  ; compileLetTypeSet(" + node + "): static");
			} else { // local
				writeinstln("mov ecx, [esp + " + (STACK_POS - (def.getLetIndex() - 1) * def.getType().getSize()) + "]  ; Loading pointer");
				writeinstln("add ecx, ebx");
				writeinstln("mov [ecx], eax  ; compileLetTypeSet(" + node + "): local");
			}
		} else if (node.getLet().isPointer() && !node.getLet().isArrayOffset()) { // pointer set
			implement();
			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + "], eax  ; compileLetTypeSet(" + node + "): static");
			} else { // local
				writeinstln("mov [esp + " + (STACK_POS - def.getLetIndex() * def.getType().getSize()) + "], eax  ; compileLetTypeSet(" + node + "): local");
			}
		} else { // direct var set
			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + "], eax  ; compileLetTypeSet(" + node + "): static");
			} else { // local
				writeinstln("mov [esp + " + (STACK_POS - def.getLetIndex() * def.getType().getSize()) + "], eax  ; compileLetTypeSet(" + node + "): local");
			}
		}
	}

	private void compileReturn(FunScopeDescriptor desc, ReturnNode node) throws CompilerException {
		FunDefNode fun = desc.getNode();

		if (!fun.getReturnType().isVoid()) {
			compileComputeExpr("eax", node.getExpr());
		}

		writeinstln("jmp " + desc.getAsmName() + "_cln  ; " + node);
	}

	private void compileStaticLetTypeDef(LetScopeDescriptor desc) throws CompilerException {
		LetTypeDefNode node = desc.getNode();

		if (node.hasExpr()) {
			if (node.getExpr() instanceof ArrayInitNode) {
				writedataln(desc.getAsmName() + " dd " + ((ArrayInitNode) node.getExpr()).getArraySize() + " dup (0)  ; " + desc.getIdent().getIdentifier());
			} else {
				writedataln(desc.getAsmName() + " dd 0  ; " + desc.getIdent().getIdentifier());

				compileComputeExpr("eax", node.getExpr());

				writeinstln("mov [" + desc.getAsmName() + "], eax  ; Setting: " + desc.getIdent().getIdentifier());
			}
		} else {
			writedataln(desc.getAsmName() + " dd 0");
		}

	}

	private void compileExit(Node node) throws CompilerException {
		writeinstln("; Exit program");

		compileComputeExpr("ebx", node);

		// writeinstln("mov ebx, " + reg);
		writeinstln("mov eax, 1 ; Syscall exit");
		writeinstln("int 0x80   ; Syscall call");
	}

	private void compileMainFun(FunScopeDescriptor desc) throws CompilerException {
		FunDefNode node = desc.getNode();
		
		compileFunDef(desc);
	}

	private void compileFunDef(FunScopeDescriptor desc) throws CompilerException {
		FunDefNode node = desc.getNode();

		// add args to stack
		GlobalLogger.log("1 " + desc + " stackpos: " + STACK_POS);
		STACK_POS += node.getArgs().getChildren().stream().map(c -> ((FunArgDefNode) c).getLet().getType().getSize()).reduce(0, (a, b) -> a + b);
		// STACK_POS += 4; // bc of call / ret
		GlobalLogger.log("2 stackpos: " + STACK_POS);

		writeln(desc.getAsmName() + ":  ; " + desc.getIdent().getIdentifier());
		for (Node n : node.getBody().getChildren()) {
			if (n instanceof ReturnNode) {
				compileReturn(desc, (ReturnNode) n);
			} else {
				compile(n);
			}
		}

		// remove args from stack BEFORE cleaning up local vars
		GlobalLogger.log("3 " + desc + " stackpos: " + STACK_POS);
		STACK_POS -= node.getArgs().getChildren().stream().map(c -> ((FunArgDefNode) c).getLet().getType().getSize()).reduce(0, (a, b) -> a + b);
		// STACK_POS -= 4; // bc of call / ret
		GlobalLogger.log("4 stackpos: " + STACK_POS);
		
		writeln(desc.getAsmName() + "_cln:");
		writeinstln("add esp, " + STACK_POS);
		writeinstln("ret");
		
		STACK_POS = 0;
	}

	private void compileLetTypeDef(LetScopeDescriptor desc) throws CompilerException {
		LetTypeDefNode node = desc.getNode();

		int size = node.getType().getSize();

		if (node.hasExpr()) { // set
			if (node.getExpr() instanceof ArrayInitNode) {
				int arrSize = ((ArrayInitNode) node.getExpr()).getArraySize() * node.getType().getSize();
				size += arrSize;

				writeinstln("push esp   ; Setup array pointer");
				writeinstln("sub dword [esp], " + size);

				writeinstln("sub esp, " + arrSize + "  ; Setup array");

				/*
				 * writeinstln("mov eax, esp  ; Setup array pointer");
				 * writeinstln("add eax, 1");
				 */
			} else {
				compileComputeExpr("eax", node.getExpr());
				writeinstln("push eax  ; Push var: " + desc.getIdent().getIdentifier());
			}
		} else { // alloc only

			writeinstln("sub esp, " + size);

		}

		GlobalLogger.log("1 " + desc + " stackpos: " + STACK_POS);
		STACK_POS += size;
		GlobalLogger.log("2 stackpos: " + STACK_POS);
	}

	private void generateExprRecursive(String reg, Node node) throws CompilerException {
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

			if (left instanceof NumLitNode || left instanceof VarNumNode || left instanceof FunCallNode) {
				compileComputeExpr("eax", left);
			}
			if (right instanceof NumLitNode || right instanceof VarNumNode || right instanceof FunCallNode) {
				compileComputeExpr("ebx", right);
			}

			TokenType operator = binaryNode.getOperator();

			switch (operator) {
			case PLUS:
				writeinstln("add eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + reg);
				break;
			case MINUS:
				writeinstln("sub eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + reg);
				break;
			case MODULO:
			case DIV:
				writeinstln("xor edx, edx  ; zero-ing out edx for divison");
				writeinstln("idiv ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + reg);
				break;
			case MUL:
				writeinstln("imul eax, ebx  ; " + left + " " + binaryNode.getOperator().getValue() + " " + right + " -> " + reg);
				break;
			default:
				throw new CompilerException("Operation not supported: " + operator);
			}

			if (TokenType.MODULO.equals(operator)) {
				writeinstln("mov " + reg + ", edx");
			} else if (reg != "eax") {
				writeinstln("mov " + reg + ", eax");
			}

		} else if (node instanceof NumLitNode) {
			implement(node);
		} else {
			implement(node);
		}

	}

	private void compileComputeExpr(String reg, Node node) throws CompilerException {
		if (node instanceof FunCallNode) {
			compileFunCall((FunCallNode) node);
			writeinstln("mov " + reg + ", eax");
		} else if (node instanceof NumLitNode) {
			writeinstln("mov " + reg + ", " + ((NumLitNode) node).getValue() + "  ; compileComputeExpr(" + node + ")");
		} else if (node instanceof VarNumNode) {
			compileLoadVarNum(reg, (VarNumNode) node);
		} else if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, (BinaryOpNode) node);
		} else {
			implement(node);
		}
	}

	private void compileFunCall(FunCallNode node) throws CompilerException {
		if (node.isPreset()) {

			if (node.getIdent().getIdentifier().equals("break")) {
				String _break = ((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().getFirst()).getExpr()).getString().getValue();
				writeln(_break + ":  ; breakpoint at: " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
				writetextln("global " + _break);
			}

		} else {

			FunScopeDescriptor desc = (FunScopeDescriptor) node.getClosestContainer().getClosestDescriptor(node.getIdent().getIdentifier());
			FunDefNode def = desc.getNode();

			int wantedArgCount = def.getArgs().getChildren().size();
			int gotArgCount = node.getArgs().getChildren().size();

			if (wantedArgCount != gotArgCount) {
				throw new CompilerException("Argument count do not match, got " + gotArgCount + " but wanted " + wantedArgCount);
			}

			int gotArgSize = def.getArgs().getChildren().stream().map((c) -> ((FunArgDefNode) c).getLet().getType().getSize()).reduce(0, (a, b) -> a + b);

			for (Node arg : node.getArgs().getChildren()) {
				if (!(arg instanceof FunArgValNode)) {
					throw new CompilerException("Arg val should be " + FunArgValNode.class.getSimpleName());
				}
				compileComputeExpr("eax", ((FunArgValNode) arg).getExpr());
				writeinstln("push eax");
			}

			writeinstln("call " + desc.getAsmName() + "  ; " + desc.getIdent().getIdentifier());

			writeinstln("add dword esp, " + gotArgSize);

		}
	}

	private void compileLoadVarNum(String reg, VarNumNode node) throws CompilerException {
		ScopeContainer container = node.getClosestContainer();
		String ident = node.getIdent().getIdentifier();
		if (!container.containsDescriptor(ident)) {
			throw new CompilerException("Cannot find: '" + ident + "' in current scope");
		}
		LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(ident);
		LetTypeDefNode def = desc.getNode();

		if (def.getType().isPointer() && node.isArrayOffset()) { // array
			compileComputeExpr("ebx", node.getOffset());
			writeinstln("imul ebx, 4");

			if (def.isiStatic()) { // static
				writeinstln("mov " + reg + ", [" + desc.getAsmName() + " + ebx]  ; compileLoadVarNum(" + node + "): static");
			} else { // local
				GlobalLogger.log("1 "+node+" stackpos: "+STACK_POS);
				writeinstln("mov ecx, [esp + " + (STACK_POS - (def.getLetIndex() - 1) * def.getType().getSize()) + "]  ; Loading pointer");
				writeinstln("add ecx, ebx");
				writeinstln("mov " + reg + ", [ecx] ; compileLoadVarNum(" + node + "): local");
			}
		} else if (def.getType().isPointer() && !node.isArrayOffset()) { // is only pointer
			if (def.isiStatic()) { // static
				writeinstln("lea " + reg + ", [" + desc.getAsmName() + "]  ; compileLoadVarNum(" + node + "): static");
			} else { // local
				GlobalLogger.log("2 "+node+" stackpos: "+STACK_POS);
				writeinstln("lea "+reg+", [esp + " + (STACK_POS - (def.getLetIndex()) * def.getType().getSize()) + "]  ; Loading pointer");
				// writeinstln("mov " + reg + ", [ecx] ; compileLoadVarNum(" + node + "): local");
			}
		} else if (!def.getType().isPointer()) { // direct access
			if (def.isiStatic()) {
				writeinstln("mov " + reg + ", [" + desc.getAsmName() + "]  ; compileLoadVarNum(" + node + "): static");
			} else {
				GlobalLogger.log("3 "+node+" stackpos: "+STACK_POS);
				writeinstln("mov " + reg + ", [esp + " + (STACK_POS - (def.getLetIndex() + 1 - (def.isArg() ? 1 : 0)) * def.getType().getSize()) + "]  ; compileLoadVarNum(" + node + "): local; STACK_POS = " + STACK_POS);
			}
		}
	}

	private void implement() throws CompilerException {
		throw new CompilerException("not implemented; ");
	}

	private void implement(Object obj) throws CompilerException {
		throw new CompilerException("not implemented: " + obj.getClass().getSimpleName());
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

	private void warn(String msg) {
		GlobalLogger.warning(msg);
	}

}
