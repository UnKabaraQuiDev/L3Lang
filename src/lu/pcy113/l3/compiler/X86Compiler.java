package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.ArrayInit;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.ElseDefNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.IfDefNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.WhileDefNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.pclib.GlobalLogger;

public class X86Compiler extends L3Compiler {

	private int heapSize = 1024;

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writetextln("global _start");
		writedataln("esp_start dd 0");
		// writedataln("heap_space resb " + heapSize);
		// writedataln("heap_ptr dd heap_space");
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

	private Stack<Node> vStack = new Stack<Node>();

	public Node push(Node n) {
		GlobalLogger.log();
		GlobalLogger.log("push: " + n);
		return vStack.push(n);
	}

	public Node pop() {
		GlobalLogger.log();
		Node pop = vStack.pop();
		GlobalLogger.log("pop: " + pop);
		return pop;
	}

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

				writeinstln("mov eax, esp");
				writeinstln("sub eax, 8  ; Add offset for main fun call");
				writeinstln("mov [esp_start], eax");

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
				} else if (n instanceof FunDefNode && ((FunDefNode) n).getIdent().getValue().equals("main")) {
					continue; // already declared
				}

				compile(n);
			}
		} else if (node instanceof FunDefNode) {
			compileFunDef((FunScopeDescriptor) container.getClosestDescriptor(((FunDefNode) node).getIdent().getValue()));
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDef((LetScopeDescriptor) container.getClosestDescriptor(((LetTypeDefNode) node).getIdent().getValue()));
		} else if (node instanceof LetTypeSetNode) {
			compileLetTypeSet((LetTypeSetNode) node);
		} else if (node instanceof FunCallNode) {
			compileFunCall((FunCallNode) node);
		} else if (node instanceof IfContainerNode) {
			compileIfContainerNode((IfContainerNode) node);
		} else if (node instanceof ReturnNode) {
			compileReturn((ReturnNode) node);
		} else if (node instanceof WhileDefNode) {
			compileWhileDefNode((WhileDefNode) node);
		} else {
			implement(node);
		}
	}

	private void compileWhileDefNode(WhileDefNode node) throws CompilerException {
		String whileName = newSection();
		node.setAsmName(whileName);

		ScopeBodyNode body = node.getBody();

		writeln(node.getAsmName() + ":  ; While at: " + ((WhileDefNode) node).getToken().getPosition());
		compileWhileDefConditionNode(node);

		final int startStackIndex = vStack.size() - 1;
		body.setStartStackIndex(startStackIndex);

		for (Node n : body) {
			compile(n);
		}
		writeinstln("jmp " + node.getAsmName());

		int size = getStackSize(startStackIndex);

		writeln(body.getClnAsmName() + ":");
		writeinstln("add esp, " + size + "  ; Free mem");

		writeln(node.getAsmName() + "_end:");
	}

	private void compileWhileDefConditionNode(WhileDefNode node) throws CompilerException {
		Node expr = node.getCondition();
		compileComputeExpr("eax", expr);
		writeinstln("cmp eax, 0");
		writeinstln("je " + node.getAsmName() + "_end");
	}

	private void compileIfContainerNode(IfContainerNode node) throws CompilerException {
		String ifContainerName = newSection();
		node.setAsmName(ifContainerName);

		int i = 0;

		writeln(node.getAsmName() + ":  ; If container at: " + ((IfDefNode) node.getChildren().getFirst()).getToken().getPosition());

		for (Node n : node) {
			if (n instanceof IfDefNode) {
				((IfDefNode) n).setAsmName(ifContainerName + "_" + i++);

				compileIfDefConditionNode((IfDefNode) n);
			} else if (n instanceof ElseDefNode) {
				((ElseDefNode) n).setAsmName(ifContainerName + "_" + i++);

				writeinstln("jmp " + ((ElseDefNode) n).getAsmName());
			} else {
				implement(n);
			}
		}

		if (!(node.getChildren().getLast() instanceof ElseDefNode)) {
			writeinstln("jmp " + node.getAsmName() + "_end");
		}

		for (Node n : node) {
			if (n instanceof IfDefNode) {
				compileIfElseDefBodyNode((IfDefNode) n);
			} else if (n instanceof ElseDefNode) {
				compileIfElseDefBodyNode((ElseDefNode) n);
			}
		}

		writeln(node.getAsmName() + "_end:");
	}

	private void compileIfElseDefBodyNode(Node node) throws CompilerException {
		ScopeBodyNode body = null;
		String asmName = null;
		String asmNameComment = null;
		if (node instanceof IfDefNode) {
			body = ((IfDefNode) node).getBody();
			asmName = ((IfDefNode) node).getAsmName();
			asmNameComment = "If node at: " + ((IfDefNode) node).getToken().getPosition();
		} else if (node instanceof ElseDefNode) {
			body = ((ElseDefNode) node).getBody();
			asmName = ((ElseDefNode) node).getAsmName();
			asmNameComment = "Else node at: " + ((ElseDefNode) node).getToken().getPosition();
		} else {
			implement(node);
		}

		IfContainerNode container = (IfContainerNode) node.getParent();

		final int startStackIndex = vStack.size() - 1;
		body.setStartStackIndex(startStackIndex);

		writeln(asmName + ":  ; " + asmNameComment);
		for (Node n : body) {
			compile(n);
		}

		int size = getStackSize(startStackIndex);

		writeln(body.getClnAsmName());
		writeinstln("add esp, " + size + "  ; Free mem");

		writeinstln("jmp " + container.getAsmName() + "_end");
	}

	private FunDefNode getFunDefParent(Node node) throws CompilerException {
		Node parent = node.getParent();
		while (!(parent instanceof FunDefNode)) {
			parent = parent.getParent();
			if (parent == null) {
				throw new CompilerException("Node has no FunDefNode parent.");
			}
		}
		return (FunDefNode) parent;
	}

	private void compileIfDefConditionNode(IfDefNode n) throws CompilerException {
		Node expr = n.getCondition();
		compileComputeExpr("eax", expr);
		writeinstln("cmp eax, 0");
		writeinstln("jne " + n.getAsmName());
	}

	private void compileLetTypeSet(LetTypeSetNode node) throws CompilerException {
		ScopeContainer container = node.getClosestContainer();
		LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(node.getLet().getIdent().getValue());
		LetTypeDefNode def = desc.getNode();

		compileComputeExpr("eax", node.getExpr());

		if (node.getLet().isPointer() && node.getLet().isArrayOffset()) { // array set
			compileComputeExpr("ebx", node.getLet().getOffset());
			writeinstln("imul ebx, 4");

			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + " + ebx], eax  ; compileLetTypeSet(" + node + "): static pointer");
			} else { // local
				writeinstln("mov ecx, [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "]  ; Loading pointer, index = " + def.getStackIndex() + ", size = " + getStackIndex());
				// writeinstln("mov ecx, [ecx - " + (def.getStackIndex()) + "] ; index = " +
				// def.getStackIndex());
				writeinstln("add ecx, ebx");
				writeinstln("mov [ecx], eax  ; compileLetTypeSet(" + node + "): local pointer");
			}
		} else if (node.getLet().isPointer() && !node.getLet().isArrayOffset()) { // pointer set
			implement();
			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + "], eax  ; compileLetTypeSet(" + node + "): static");
			} else { // local
				writeinstln("mov [ecx], [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "]  ; compileLetTypeSet(" + node + "): local, index = " + def.getStackIndex());
				// writeinstln("mov [ecx - " + (def.getStackIndex()) + "], eax ;
				// compileLetTypeSet(" + node + "): local, index = " + def.getStackIndex());
			}
		} else { // direct var set
			if (def.isiStatic()) { // static
				writeinstln("mov [" + desc.getAsmName() + "], eax  ; compileLetTypeSet(" + node + "): static");
			} else { // local
				// writeinstln("mov eax, [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "]  ; compileLetTypeSet(" + node + "): local, index = " + def.getStackIndex());
				// writeinstln("mov [ecx], eax");
				writeinstln("mov [esp - " + (getStackIndex() - def.getStackIndex() - 4) + "], eax ; compileLetTypeSet(" + node + "): local, index = " + def.getStackIndex());
			}
		}
	}

	private void compileReturn(ReturnNode node) throws CompilerException {
		FunScopeDescriptor desc = (FunScopeDescriptor) node.getClosestContainer().getClosestDescriptor(getFunDefParent(node).getIdent().getValue());

		FunDefNode fun = desc.getNode();

		if (!fun.getReturnType().isVoid()) {
			compileComputeExpr("eax", node.getExpr());
		}

		if (node.getParent() instanceof ScopeBodyNode) {
			int size = getStackSize(((ScopeContainerNode) node.getParent()).getStartStackIndex());

			writeinstln("add esp, " + size + "  ; Free mem from local scope bc of return");

			((ScopeContainerNode) node.getParent()).getLocalDescriptors().values().forEach(c -> pop());
		}

		writeinstln("jmp " + desc.getAsmName() + "_cln  ; " + node);
	}

	private void compileStaticLetTypeDef(LetScopeDescriptor desc) throws CompilerException {
		LetTypeDefNode node = desc.getNode();
		String name = node.getIdent().getValue();
		String asmName = desc.getAsmName();

		if (node.hasExpr()) {
			if (node.getExpr() instanceof ArrayInit) {
				if (node.getExpr() instanceof ArrayInitNode && ((ArrayInitNode) node.getExpr()).isRaw()) {
					writedataln(asmName + " dd "
							+ (((ArrayInitNode) node.getExpr()).getChildren().subList(1, ((ArrayInitNode) node.getExpr()).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString())).collect(Collectors.joining(", ")) + "  ; "
							+ name);
				} else if (node.getExpr() instanceof StringLitNode) {
					writedataln(asmName + " dd "
							+ (((StringLitNode) node.getExpr()).getChildren().subList(0, ((StringLitNode) node.getExpr()).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString())).collect(Collectors.joining(", ")) + "  ; "
							+ name);
				}
			} else {
				writedataln(asmName + " dd 0  ; " + desc.getIdentifier().getValue());

				compileComputeExpr("eax", node.getExpr());

				writeinstln("mov [" + asmName + "], eax  ; Setting: " + name);
			}
		} else {
			writedataln(asmName + " dd 0");
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

		final int startStackIndex = vStack.size() - 1;

		writeln(desc.getAsmName() + ":  ; " + desc.getIdentifier().getValue());
		for (Node n : node.getBody().getChildren()) {
			compile(n);
		}

		// remove args from stack BEFORE cleaning up local vars
		int size = getStackSize(startStackIndex);

		writeln(desc.getAsmName() + "_cln:");
		writeinstln("add esp, " + size);
		writeinstln("ret");
	}

	private void compileFunDef(FunScopeDescriptor desc) throws CompilerException {
		FunDefNode node = desc.getNode();

		// add args to stack
		// node.getArgs().getChildren().forEach(c -> push(((FunArgDefNode)
		// c).getLet()));

		push(node); // call/ret

		final int startStackIndex = vStack.size() - 1;

		writeln(desc.getAsmName() + ":  ; " + desc.getIdentifier().getValue());
		for (Node n : node.getBody().getChildren()) {
			compile(n);
		}

		int size = getStackSize(startStackIndex);

		writeln(desc.getAsmName() + "_cln:");
		writeinstln("add esp, " + size);
		writeinstln("ret");

		pop(); // call/ret
	}

	private void compileLetTypeDef(LetScopeDescriptor desc) throws CompilerException {
		LetTypeDefNode node = desc.getNode();

		String asmName = desc.getAsmName();
		String name = node.getIdent().getValue();
		String pos = node.getIdent().getPosition();

		final int typeSize = node.getType().getSize();
		int size = node.getType().getSize();
		final int currentStackIndex = getStackIndex();

		if (node.hasExpr()) { // set
			if (node.getExpr() instanceof ArrayInit) {
				ArrayInit expr = (ArrayInit) node.getExpr();

				int arrSize = expr.getArraySize() * typeSize;

				if (expr.hasExpr()) {
					writeinstln("mov eax, esp");
					writeinstln("sub eax, " + (arrSize + 4));
					writeinstln("push eax  ; Setup array pointer");

					writeinstln("sub esp, " + arrSize);

					if (expr instanceof StringLitNode || (expr instanceof ArrayInitNode && ((ArrayInitNode) expr).isRaw())) {
						if (expr instanceof StringLitNode) {
							writedataln(asmName + " dd " + (((StringLitNode) expr).getChildren().subList(0, ((StringLitNode) expr).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString()).collect(Collectors.joining(", ")))
									+ "  ; " + name + " at " + pos);
						} else if (expr instanceof ArrayInitNode && ((ArrayInitNode) expr).isRaw()) {
							writedataln(asmName + " dd " + (((ArrayInitNode) expr).getChildren().subList(1, ((ArrayInitNode) expr).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString()).collect(Collectors.joining(", ")))
									+ "  ; " + name + " at " + pos);
						}

						writeinstln("sub esp, 12");
						writeinstln("mov dword [esp + 8], " + asmName + "  ; From");
						writeinstln("mov dword [esp + 4], eax  ; To");
						writeinstln("mov dword [esp + 0], " + expr.getArraySize() + "  ; Length");
						writeinstln("call " + node.getClosestContainer().getClosestDescriptor("memcpy").getAsmName());
						writeinstln("add esp, 12");
					} else {
						for (int i = 0; i < expr.getArraySize(); i++) {
							Node child = expr.getExpr(i);
							compileComputeExpr("eax", child);
							writeinstln("mov [esp + " + (i * typeSize) + "], eax");
						}
					}
				} else {
					writeinstln("mov eax, esp");
					writeinstln("sub eax, " + (arrSize + size));
					writeinstln("push eax  ; Setup empty pointer " + name + " -> " + asmName);
					writeinstln("sub esp, " + arrSize);
				}

				node.setStackSize(arrSize + size);
			} else {
				compileComputeExpr("eax", node.getExpr());
				writeinstln("push dword eax  ; Push var: " + desc.getIdentifier().getValue());

				node.setStackSize(size);
			}
		} else { // alloc only

			writeinstln("sub esp, " + size);

			node.setStackSize(size);

		}

		node.setStackIndex(currentStackIndex);

		push(node);
	}

	private void generateExprRecursive(String reg, Node node) throws CompilerException {
		if (node instanceof BinaryOpNode) {
			BinaryOpNode binaryNode = (BinaryOpNode) node;

			Node left = binaryNode.getLeft();
			Node right = binaryNode.getRight();

			if (right instanceof BinaryOpNode) {
				generateExprRecursive("ebx", ((BinaryOpNode) right));
				writeinstln("push ebx");
				push(right);
			}
			if (left instanceof BinaryOpNode) {
				generateExprRecursive("eax", ((BinaryOpNode) left));
				writeinstln("push eax");
				push(right);
			}

			if (left instanceof NumLitNode || left instanceof VarNumNode || left instanceof FunCallNode) {
				compileComputeExpr("eax", left);
				writeinstln("push eax");
				push(left);
			}
			if (right instanceof NumLitNode || right instanceof VarNumNode || right instanceof FunCallNode) {
				compileComputeExpr("ebx", right);
				writeinstln("push ebx");
				push(right);
			}

			if (left instanceof BinaryOpNode) {
				writeinstln("pop eax");
				pop();
			}
			if (right instanceof BinaryOpNode) {
				writeinstln("pop ebx");
				pop();
			}

			if (right instanceof NumLitNode || right instanceof VarNumNode || right instanceof FunCallNode) {
				writeinstln("pop ebx");
				pop();
			}
			if (left instanceof NumLitNode || left instanceof VarNumNode || left instanceof FunCallNode) {
				writeinstln("pop eax");
				pop();
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

		} else {
			implement();
		}
	}

	private void compileComputeExpr(String reg, Node node) throws CompilerException {
		if (node instanceof FunCallNode) {
			compileFunCall((FunCallNode) node);
			writeinstln("mov " + reg + ", eax");
		} else if (node instanceof NumLitNode) {
			writeinstln("mov dword " + reg + ", " + ((NumLitNode) node).getValue() + "  ; compileComputeExpr(" + node + ")");
		} else if (node instanceof VarNumNode) {
			compileLoadVarNum(reg, (VarNumNode) node);
		} else if (node instanceof BinaryOpNode) {
			generateExprRecursive(reg, (BinaryOpNode) node);
		} else if (node instanceof StringLitNode) {
			compileArrayInit((StringLitNode) node);
			writeinstln("mov " + reg + ", [esp + " + (((StringLitNode) node).getStackSize() - 4) + "]  ; Loading StringLitNode pointer");
		} else {
			implement(node);
		}
	}

	private void compileArrayInit(Node node) throws CompilerException {
		if (node instanceof StringLitNode) {
			ArrayInit expr = (ArrayInit) node;

			String asmName = newVar();
			String name = ((StringLitNode) node).getString().getPosition();

			int typeSize = 4;
			int arrSize = expr.getArraySize() * typeSize;
			int size = typeSize; // pointer size

			if (expr.hasExpr()) {
				writeinstln("mov eax, esp");
				writeinstln("sub eax, " + (arrSize + 4));
				writeinstln("push eax  ; Setup array pointer");

				writeinstln("sub esp, " + arrSize);

				if (expr instanceof StringLitNode || (expr instanceof ArrayInitNode && ((ArrayInitNode) expr).isRaw())) {
					if (expr instanceof StringLitNode) {
						writedataln(asmName + " dd " + (((StringLitNode) expr).getChildren().subList(0, ((StringLitNode) expr).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString()).collect(Collectors.joining(", ")))
								+ "  ; " + name);
					} else if (expr instanceof ArrayInitNode && ((ArrayInitNode) expr).isRaw()) {
						writedataln(asmName + " dd " + (((ArrayInitNode) expr).getChildren().subList(1, ((ArrayInitNode) expr).getChildren().size()).stream().map((c) -> ((NumLitNode) c).getValue().toString()).collect(Collectors.joining(", ")))
								+ "  ; " + name);
					}

					writeinstln("sub esp, 12");
					writeinstln("mov dword [esp + 8], " + asmName + "  ; From");
					writeinstln("mov dword [esp + 4], eax  ; To");
					writeinstln("mov dword [esp + 0], " + expr.getArraySize() + "  ; Length");
					writeinstln("call " + node.getClosestContainer().getClosestDescriptor("memcpy").getAsmName());
					writeinstln("add esp, 12");
				} else {
					for (int i = 0; i < expr.getArraySize(); i++) {
						Node child = expr.getExpr(i);
						compileComputeExpr("eax", child);
						writeinstln("mov [esp + " + (i * typeSize) + "], eax");
					}
				}
			} else {
				throw new CompilerException("Cannot compile empty array.");
			}

			expr.setStackSize(arrSize + size);
		} else {
			implement(node);
		}
	}

	private void compileFunCall(FunCallNode node) throws CompilerException {
		String name = node.getIdent().getValue();
		ScopeContainer container = node.getClosestContainer();

		if (node.isPreset()) {

			if (name.equals("break")) {
				String _break = ((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().getFirst()).getExpr()).getString().getValue();
				writeln(_break + ":  ; breakpoint at: " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
				writetextln("global " + _break);
			} else if (name.equals("exit")) {
				compileExit(((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr());
			} else if (name.equals("printwrite")) {
				compilePrintWrite();
			} else if (name.equals("print")) {
				compilePrint(((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr(), ((FunArgValNode) node.getArgs().getChildren().get(1)).getExpr());
			} else if (name.equals("asm")) {
				writeinstln(((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr()).getString().getValue());
			} else if (name.equals("asmlb")) {
				writeln(((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr()).getString().getValue());
			} else {
				throw new CompilerException("Preset function not found: " + name);
			}

		} else {

			FunScopeDescriptor desc = (FunScopeDescriptor) node.getClosestContainer().getClosestDescriptor(name);
			if (desc == null) {
				throw new CompilerException("Couldn't find descriptor for: " + name);
			}
			FunDefNode def = desc.getNode();

			int wantedArgCount = def.getArgs().getChildren().size();
			int gotArgCount = node.getArgs().getChildren().size();

			if (wantedArgCount != gotArgCount) {
				throw new CompilerException("Argument count do not match, got " + gotArgCount + " but wanted " + wantedArgCount);
			}

			for (Node arg : node.getArgs().getChildren()) {
				if (!(arg instanceof FunArgValNode)) {
					throw new CompilerException("Arg val should be " + FunArgValNode.class.getSimpleName());
				}
				compileComputeExpr("eax", ((FunArgValNode) arg).getExpr());
				writeinstln("push eax");

				((FunArgValNode) arg).setStackSize(getStackSize(((FunArgValNode) arg).getExpr()));
			}

			final int startStackIndex = vStack.size() - 1;

			node.getArgs().getChildren().forEach(c -> push((FunArgValNode) c));
			push(node);

			writeinstln("call " + desc.getAsmName() + "  ; " + desc.getIdentifier().getValue());

			pop();
			int size = getStackSize(startStackIndex);
			writeinstln("add dword esp, " + size + "  ; Free mem from fun call");

			node.getArgs().getChildren().forEach(c -> pop());
		}
	}

	private int getStackIndex() throws CompilerException {
		int stackIndex = 0;
		for (Node n : vStack) {
			stackIndex += getStackSize(n);
		}
		return stackIndex;
	}

	private int getStackSize(Node node) throws CompilerException {
		if (node instanceof VarNumNode) {
			return ((LetTypeDefNode) ((LetScopeDescriptor) node.getParentContainer().getClosestDescriptor(((VarNumNode) node).getIdent().getValue())).getNode()).getType().getSize();
		} else if (node instanceof NumLitNode) {
			return ((NumLitNode) node).getStackSize();
		} else if (node instanceof LetTypeDefNode) {
			return ((LetTypeDefNode) node).getStackSize();
		} else if (node instanceof FunCallNode) {
			return 4;
		} else if (node instanceof FunArgValNode) {
			return ((FunArgValNode) node).getStackSize();
		} else if (node instanceof FunDefNode) {
			return 4;
		} else if (node instanceof FunArgDefNode) {
			return ((FunArgDefNode) node).getLet().getType().getSize();
		} else if (node instanceof ArrayInit) {
			return ((ArrayInit) node).getStackSize() + 4;
		} else {
			implement(node);
		}
		return 0;
	}

	private int getStackSize(int startStackIndex) throws CompilerException {
		int size = 0;
		for (int i = vStack.size() - 1; i > startStackIndex; i--) {
			Node stackNode = vStack.get(i);
			size += getStackSize(stackNode);
		}
		return size;
	}

	private void compilePrintWrite() throws CompilerException {
		// pointer in ecx
		writeinstln("; Print write");
		writeinstln("mov eax, 4  ; Write");
		writeinstln("mov ebx, 1  ; Stdout");
		writeinstln("mov edx, 1  ; Length");
		writeinstln("int 0x80  ; Syscall");
	}

	private void compilePrint(Node expr, Node length) throws CompilerException {
		if (expr instanceof StringLitNode) {
			implement();
		} else if (expr instanceof VarNumNode) {
			writeinstln("; Println");
			compileComputeExpr("edx", length); // length
			compileComputeExpr("ecx", expr); // pointer
			writeinstln("mov eax, 4  ; Write");
			writeinstln("mov ebx, 1  ; Stdout");
			writeinstln("int 0x80  ; Syscall");
		}
	}

	private void compileLoadVarNum(String reg, VarNumNode node) throws CompilerException {
		ScopeContainer container = node.getClosestContainer();
		String ident = node.getIdent().getValue();
		if (!container.containsDescriptor(ident)) {
			throw new CompilerException("Cannot find: '" + ident + "' in current scope");
		}
		LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(ident);
		LetTypeDefNode def = desc.getNode();

		if (def.getType().isPointer() /* && node.isArrayOffset() */) { // array
			if (node.isArrayOffset()) {
				compileComputeExpr("ebx", node.getOffset());
				writeinstln("imul ebx, 4");

				if (def.isiStatic()) { // static
					writeinstln("mov " + reg + ", [" + desc.getAsmName() + " + ebx]  ; compileLoadVarNum(" + node + "): static");
				} else { // local
					writeinstln("mov ecx, [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "]  ; Loading pointer, index = " + def.getStackIndex() + ", size = " + getStackIndex());
					// writeinstln("mov ecx, [ecx - " + (def.getStackIndex()) + "] ; index = " +
					// def.getStackIndex());
					writeinstln("add ecx, ebx");
					writeinstln("mov " + reg + ", [ecx] ; compileLoadVarNum(" + node + "): local");
				}
			} else {
				if (def.isiStatic()) { // static
					writeinstln("mov " + reg + ", " + desc.getAsmName() + "  ; compileLoadVarNum(" + node + "): static");
				} else {
					writeinstln("mov " + reg + ", [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "] ; compileLoadVarNum(" + node + "): local");
					// writeinstln("mov " + reg + ", [ecx] ; index = " + def.getStackIndex());
				}
			}
		} else if (!def.getType().isPointer()) { // direct access
			if (def.isiStatic()) {
				writeinstln("mov " + reg + ", [" + desc.getAsmName() + "]  ; compileLoadVarNum(" + node + "): static");
			} else {
				writeinstln("mov " + reg + ", [esp + " + (getStackIndex() - def.getStackIndex() - 4) + "]  ; compileLoadVarNum(" + node + "): local");
				// writeinstln("mov " + reg + ", [ecx - " + (def.getStackIndex()) + "] ; index =
				// " + def.getStackIndex());
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
