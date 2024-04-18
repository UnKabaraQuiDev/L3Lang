package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.ArrayInit;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.ComparisonOpNode;
import lu.pcy113.l3.parser.ast.ElseDefNode;
import lu.pcy113.l3.parser.ast.FinallyDefNode;
import lu.pcy113.l3.parser.ast.ForDefNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.IfDefNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.LocalizingNode;
import lu.pcy113.l3.parser.ast.LogicalOpNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.WhileDefNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.pclib.GlobalLogger;

public class X86Compiler extends L3Compiler {

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writetextln("global _start");
		writedataln("esp_start dd 0");
		writeln("_start:");

		compile(root);

		appendText();
		appendData();

		flushAndClose();

		File dir = outFile.getAbsoluteFile().getParentFile();
		try {

			String oOutFile = FileUtils.replaceExtension(outFile.getName(), "o");
			String rmExtOutFile = FileUtils.removeExtension(outFile.getName());
			String outDirPath = outFile.getPath();
			exec("nasm -f elf32 -g -o " + oOutFile + " " + outDirPath, dir);
			// exec("gcc -m32 elf_i386 -o " + rmExtOutFile + " " + oOutFile, dir);
			exec("ld -m elf_i386 -o " + rmExtOutFile + " " + oOutFile, dir);
			exec("./" + rmExtOutFile, dir);

		} catch (IOException | InterruptedException e) {
			throw new CompilerException("Could not exec: '" + outFile + "', '" + FileUtils.removeExtension(outFile.getName()) + "' and '" + FileUtils.replaceExtension(outFile.getName(), "o") + "' in " + dir, e);
		}
	}

	private int STACK_INDEX = 0;
	private Stack<Node> vStack = new Stack<Node>();

	public Node push(Node n) throws CompilerException {
		GlobalLogger.log();
		GlobalLogger.log("push: " + n);
		STACK_INDEX += getStackSize(n);
		return vStack.push(n);
	}

	public Node pop() throws CompilerException {
		GlobalLogger.log();
		Node pop = vStack.pop();
		STACK_INDEX -= getStackSize(pop);
		GlobalLogger.log("pop: " + pop);
		return pop;
	}

	private void compile(Node node) throws CompilerException {
		// GlobalLogger.log();

		ScopeContainer container = node.getClosestContainer();

		if (node instanceof RuntimeNode) {
			// Setup static vars
			compileMainFile(((RuntimeNode) node).getMainFile());

			for (Node n : ((RuntimeNode) node).getSecondaryFiles()) {
				compile(n);
			}
		} else if (node instanceof FileNode) {
			for (Node n : ((FileNode) node)) {
				compile(n);
			}
		}else if (node instanceof FunDefNode) {
			if(((FunDefNode) node).isMain()) {
				return;
			}
			
			compileFunDef(container.getFunDescriptor((FunDefNode) node));
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDef(container.getLetTypeDefDescriptor((LetTypeDefNode) node));
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
		} else if (node instanceof ForDefNode) {
			compileForDefNode((ForDefNode) node);
		} else if (node instanceof ScopeBodyNode) {
			for (Node n : node) {
				compile(n);
			}
		} else if (node instanceof PackageDefNode) {
			// ignore package def node
		} else {
			implement(node);
		}
	}

	private void compileMainFile(FileNode node) throws CompilerException {
		ScopeContainer container = node;

		for (ScopeDescriptor desc : container.getLocalDescriptors().values().stream().flatMap(List::stream).collect(Collectors.toList())) {
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

		compile(node);
	}

	private void compileForDefNode(ForDefNode node) throws CompilerException {
		String whileName = newSection();
		node.setAsmName(whileName);

		ScopeBodyNode body = node.getBody();

		writeln(node.getAsmName() + ":  ; For at: " + node.getToken().getPosition());

		if (node.hasLetTypeDef()) {
			compile(node.getLetTypeDef());
		}

		node.setAsmName("." + node.getAsmName());
		writeln(node.getAsmName() + ":");
		compileForDefConditionNode(node);

		final int startStackIndex = vStack.size() - 1;
		body.setStartStackIndex(startStackIndex);

		for (Node n : body) {
			compile(n);
		}

		if (node.hasShortBody()) {
			compile(node.getShortBody());
		}

		writeinstln("jmp " + node.getAsmName());

		int size = getStackSize(startStackIndex);

		// cleanup for loop
		writeln(body.getClnAsmName() + ":");
		writeinstln("add esp, " + size + "  ; Free mem");

		for (ScopeDescriptor n : node.getBody().getLocalDescriptors().values().stream().flatMap(List::stream).filter(c -> c instanceof LetScopeDescriptor).collect(Collectors.toList())) {
			pop();
		}

		writeln(node.getAsmName() + "_end:");
	}

	private void compileForDefConditionNode(ForDefNode node) throws CompilerException {
		Node expr = node.getCondition();
		compileComputeExpr("eax", expr);
		writeinstln("cmp eax, 0");
		writeinstln("je " + node.getAsmName() + "_end");
	}

	private void compileWhileDefNode(WhileDefNode node) throws CompilerException {
		String whileName = newSection();
		node.setAsmName(whileName);

		ScopeBodyNode body = node.getBody();

		final String startName = "." + node.getAsmName();
		final String elseName = startName + "_else";
		final String finallyName = startName + "_finally";
		final String endName = startName + "_end";

		writeln(node.getAsmName() + ":  ; While at: " + node.getToken().getPosition());
		compileWhileDefConditionNode(node, node.hasElse() ? elseName : endName);

		writeln(startName + ":  ; While condition");
		compileWhileDefConditionNode(node, node.hasFinally() ? finallyName : endName);

		bodyGen: {
			final int startStackIndex = vStack.size() - 1;
			body.setStartStackIndex(startStackIndex);
			body.setClnAsmName(startName + "_cln");

			compile(body);

			int size = getStackSize(startStackIndex);

			writeln(body.getClnAsmName() + ":");
			writeinstln("add esp, " + size + "  ; Free mem");

			for (ScopeDescriptor n : node.getBody().getLocalDescriptors().values().stream().flatMap(List::stream).filter(c -> c instanceof LetScopeDescriptor).collect(Collectors.toList())) {
				pop();
			}

			writeinstln("jmp " + startName);
		}

		if (node.hasElse()) {

			ElseDefNode _else = node.getElse();
			_else.setAsmName(elseName);

			writeln(elseName + ":");

			compile(_else.getBody());

			writeln("jmp " + endName + "  ; After else");
		}

		if (node.hasFinally()) {

			FinallyDefNode _finally = node.getFinally();
			_finally.setAsmName(finallyName);

			writeln(finallyName + ":");

			compile(_finally.getBody());

			writeln("jmp " + endName + "  ; After finally");
		}

		writeln(endName + ":");
	}

	private void compileWhileDefConditionNode(WhileDefNode node, String jumpToIfFalse) throws CompilerException {
		Node expr = node.getCondition();
		compileComputeExpr("eax", expr);
		writeinstln("cmp eax, 0");
		writeinstln("je " + jumpToIfFalse);
	}

	private void compileIfContainerNode(IfContainerNode node) throws CompilerException {
		String ifContainerName = newSection();
		node.setAsmName(ifContainerName);

		int i = 0;

		writeln(node.getAsmName() + ":  ; If container at: " + ((IfDefNode) node.getChildren().getFirst()).getToken().getPosition());

		// generate conditions
		for (Node n : node) {
			if (n instanceof IfDefNode) {
				((IfDefNode) n).setAsmName("." + ifContainerName + "_" + i++);

				compileIfDefConditionNode((IfDefNode) n);
			} else if (n instanceof ElseDefNode) {
				((ElseDefNode) n).setAsmName("." + ifContainerName + "_" + i++);

				writeinstln("jmp " + ((ElseDefNode) n).getAsmName());
			} else if (n instanceof FinallyDefNode) {
				// pass for now
			} else {
				implement(n);
			}
		}

		if (node.getChildren().stream().anyMatch(c -> c instanceof ElseDefNode)) {
			writeinstln("jmp ." + node.getAsmName() + "_finally  ; Jump to finally if Else is present");
		} else {
			writeinstln("jmp ." + node.getAsmName() + "_end  ; Jump to end if Else is not present");
		}

		for (Node n : node) {
			if (!(n instanceof FinallyDefNode)) {
				compileIfElseDefBodyNode(n);
			}
		}

		if (node.getChildren().getLast() instanceof FinallyDefNode) {
			// writeinstln("jmp " + ((FinallyDefNode)
			// node.getChildren().getLast()).getAsmName());
			((FinallyDefNode) node.getChildren().getLast()).setAsmName("." + ifContainerName + "_finally");
			compileIfElseDefBodyNode(node.getChildren().getLast());
		} else {
			writeln("." + node.getAsmName() + "_finally:");
		}

		writeln("." + node.getAsmName() + "_end:");
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
		} else if (node instanceof FinallyDefNode) {
			body = ((FinallyDefNode) node).getBody();
			asmName = ((FinallyDefNode) node).getAsmName();
			asmNameComment = "Finally node at: " + ((FinallyDefNode) node).getToken().getPosition();
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

		writeln(body.getClnAsmName() + ":");
		writeinstln("add esp, " + size + "  ; Free mem");

		if (!(node instanceof FinallyDefNode)) {
			writeinstln("jmp ." + container.getAsmName() + "_finally  ; Jump to final");
		}
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
		LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(node.getLetIdent().getValue());
		LetTypeDefNode def = desc.getNode();

		compileComputeExpr("eax", node.getExpr());

		push(node.getExpr());
		writeinstln("push eax");

		compileLoadComputeExpr("ecx", node.getLet());

		pop();
		writeinstln("pop eax");

		writeinstln("mov [ecx], eax  ; compileLetTypeSet(" + node + ")");
	}

	private void compileLoadComputeExpr(String reg, Node expr) throws CompilerException {
		if (expr instanceof FunCallNode) {
			compileComputeExpr(reg, expr);
		} else if (expr instanceof VarNumNode) {
			VarNumNode node = (VarNumNode) expr;

			ScopeContainer container = node.getClosestContainer();
			String ident = node.getIdent().getValue();
			if (!container.containsDescriptor(ident)) {
				throw new CompilerException("Cannot find: '" + ident + "' in current scope");
			}
			LetScopeDescriptor desc = (LetScopeDescriptor) container.getClosestDescriptor(ident);
			LetTypeDefNode def = desc.getNode();

			if (node.isArrayOffset()) {
				compileComputeExpr("ebx", node.getOffset());
				writeinstln("imul ebx, 4");

				if (def.isiStatic()) { // static
					writeinstln("lea dword " + reg + ", [" + desc.getAsmName() + " + ebx]  ; compileLoadComputeExpr(" + node + "): static");
				} else { // local
					writeinstln("lea dword " + reg + ", [esp + " + (STACK_INDEX - def.getStackIndex() - 4) + "]  ; Loading pointer, index = " + def.getStackIndex() + ", size = " + STACK_INDEX);
					writeinstln("add " + reg + ", ebx  ; compileLoadComputeExpr(" + node + "): local");
				}
			} else {
				if (def.isiStatic()) { // static
					writeinstln("lea dword " + reg + ", [" + desc.getAsmName() + "]  ; compileLoadComputeExpr(" + node + "): static");
				} else {
					writeinstln("lea dword " + reg + ", [esp + " + (STACK_INDEX - def.getStackIndex() - 4) + "] ; compileLoadComputeExpr(" + node + "): local");
				}
			}
		} else if (expr instanceof LocalizingNode) {
			compileLocalizing(reg, (LocalizingNode) expr);
		} else {
			implement(expr);
		}
	}

	private void compileReturn(ReturnNode node) throws CompilerException {
		FunScopeDescriptor desc = (FunScopeDescriptor) node.getClosestContainer().getFunDescriptor(getFunDefParent(node));

		FunDefNode fun = desc.getNode();

		if (!fun.getReturnType().isVoid() && node.hasExpr()) {
			compileComputeExpr("eax", node.getExpr());
		}

		if (node.getParent() instanceof ScopeBodyNode) {
			int size = getStackSize(fun.getStartStackIndex());

			writeinstln("; return node: " + node.getReturnType());
			writeinstln("add esp, " + size + "  ; Free mem from local scope bc of return");
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
		node.setStartStackIndex(startStackIndex);

		writeln(desc.getAsmName() + ":  ; " + desc.getIdentifier().getValue());
		for (Node n : node.getBody().getChildren()) {
			compile(n);
		}

		// remove args from stack BEFORE cleaning up local vars
		int size = getStackSize(startStackIndex);

		writeinstln("add esp, " + size);
		writeln(desc.getAsmName() + "_cln:");
		writeinstln("ret");

		for (ScopeDescriptor n : node.getBody().getLocalDescriptors().values().stream().flatMap(List::stream).filter(c -> c instanceof LetScopeDescriptor).collect(Collectors.toList())) {
			pop();
		}
	}

	private void compileFunDef(FunScopeDescriptor desc) throws CompilerException {
		FunDefNode node = desc.getNode();

		for (Node c : node.getArgs()) {
			((FunArgDefNode) c).getLet().setStackIndex(STACK_INDEX);
			push(((FunArgDefNode) c).getLet());
		}

		push(node); // call/ret

		final int startStackIndex = vStack.size() - 1;
		node.setStartStackIndex(startStackIndex);

		writeln(desc.getAsmName() + ":  ; " + desc.getIdentifier().getValue());
		for (Node n : node.getBody().getChildren()) {
			compile(n);
		}

		int size = getStackSize(startStackIndex);

		writeinstln("add esp, " + size);
		writeln(desc.getAsmName() + "_cln:");
		writeinstln("ret");

		printStack();

		for (ScopeDescriptor n : node.getBody().getLocalDescriptors().values().stream().flatMap(List::stream).filter(c -> c instanceof LetScopeDescriptor).collect(Collectors.toList())) {
			pop();
		}

		pop(); // call/ret

		for (Node n : node.getArgs()) {
			pop();
		}
	}

	private void compileLetTypeDef(LetScopeDescriptor desc) throws CompilerException {
		LetTypeDefNode node = desc.getNode();

		String asmName = desc.getAsmName();
		String name = node.getIdent().getValue();
		String pos = node.getIdent().getPosition();

		final int typeSize = node.getType().getSize();
		int size = node.getType().getSize();
		final int currentStackIndex = STACK_INDEX;

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
					writeinstln("lea eax, [esp]");
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

		System.err.println("after: " + node.getIdent().getValue());
		printStack();
	}

	private void generateExprRecursive(String reg, Node node) throws CompilerException {
		if (node instanceof BinaryOpNode || node instanceof LogicalOpNode || node instanceof ComparisonOpNode) {
			Node left = null;
			Node right = null;
			TokenType operator = null;

			if (node instanceof BinaryOpNode) {
				left = ((BinaryOpNode) node).getLeft();
				right = ((BinaryOpNode) node).getRight();
				operator = ((BinaryOpNode) node).getOperator();
			} else if (node instanceof LogicalOpNode) {
				left = ((LogicalOpNode) node).getLeft();
				right = ((LogicalOpNode) node).getRight();
				operator = ((LogicalOpNode) node).getOperator();
			} else if (node instanceof ComparisonOpNode) {
				left = ((ComparisonOpNode) node).getLeft();
				right = ((ComparisonOpNode) node).getRight();
				operator = ((ComparisonOpNode) node).getOperator();
			}

			if (right instanceof BinaryOpNode || right instanceof LogicalOpNode || right instanceof ComparisonOpNode) {
				generateExprRecursive("ebx", right);
				writeinstln("push ebx");
				push(right);
			}
			if (left instanceof BinaryOpNode || left instanceof LogicalOpNode || left instanceof ComparisonOpNode) {
				generateExprRecursive("eax", left);
				writeinstln("push eax");
				push(left);
			}

			if (right instanceof NumLitNode || right instanceof VarNumNode || right instanceof FunCallNode) {
				compileComputeExpr("ebx", right);
				writeinstln("push ebx");
				push(right);
			}

			if (left instanceof NumLitNode || left instanceof VarNumNode || left instanceof FunCallNode) {
				compileComputeExpr("eax", left);
				writeinstln("push eax");
				push(left);
			}

			writeinstln("pop eax");
			pop();

			writeinstln("pop ebx");
			pop();

			switch (operator) {
			case OR:
			case PLUS:
				writeinstln("add eax, ebx  ; " + left + " " + operator + " " + right + " -> " + reg);
				break;
			case MINUS:
				writeinstln("sub eax, ebx  ; " + left + " " + operator + " " + right + " -> " + reg);
				break;
			case MODULO:
			case DIV:
				writeinstln("xor edx, edx  ; zero-ing out edx for divison");
				writeinstln("idiv ebx  ; " + left + " " + operator + " " + right + " -> " + reg);
				break;
			case AND:
			case MUL:
				writeinstln("imul eax, ebx  ; " + left + " " + operator + " " + right + " -> " + reg);
				break;
			case XOR:
				writeinstln("xor eax, ebx  ; " + left + " " + operator + " " + right + " -> " + reg);
				break;
			case EQUALS:
				writeinstln("cmp eax, ebx");
				writeinstln("sete al");
				break;
			case NOT_EQUALS:
				writeinstln("cmp eax, ebx");
				writeinstln("setne al");
				break;
			case LESS:
				writeinstln("cmp eax, ebx");
				writeinstln("setl al");
				break;
			case LESS_EQUALS:
				writeinstln("cmp eax, ebx");
				writeinstln("setle al");
				break;
			case GREATER:
				writeinstln("cmp eax, ebx");
				writeinstln("setg al");
				break;
			case GREATER_EQUALS:
				writeinstln("cmp eax, ebx");
				writeinstln("setge al");
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
			implement(node);
		}
	}

	private void compileComputeExpr(String reg, Node node) throws CompilerException {
		if (node instanceof FunCallNode) {
			compileFunCall((FunCallNode) node);
			writeinstln("mov " + reg + ", eax");
		} else if (node instanceof NumLitNode) {
			if (((NumLitNode) node).getValue() instanceof NumericLiteralToken) {
				writeinstln("mov dword " + reg + ", " + ((NumericLiteralToken) ((NumLitNode) node).getValue()).getValue() + "  ; compileComputeExpr(" + node + ")");
			} else if (((Token) ((NumLitNode) node).getValue()).getType().softEquals(TokenType.BOOLEAN)) {
				writeinstln("mov dword " + reg + ", " + (((Token) ((NumLitNode) node).getValue()).getType().equals(TokenType.FALSE) ? 0 : 1) + "  ; compileComputeExpr(" + node + ")");
			}
		} else if (node instanceof VarNumNode) {
			compileLoadVarNum(reg, (VarNumNode) node);
		} else if (node instanceof BinaryOpNode || node instanceof LogicalOpNode || node instanceof ComparisonOpNode) {
			generateExprRecursive(reg, node);
		} else if (node instanceof StringLitNode) {
			compileArrayInit((StringLitNode) node);
			writeinstln("mov " + reg + ", [esp + " + (((StringLitNode) node).getStackSize() - 4) + "]  ; Loading StringLitNode pointer");
		} else if (node instanceof LocalizingNode) {
			compileLocalizing(reg, (LocalizingNode) node);
		} else {
			implement(node);
		}
	}

	private void compileLocalizing(String reg, LocalizingNode node) throws CompilerException {
		compileComputeExpr(reg, node.getNode());
		writeinstln("mov " + reg + ", [" + reg + "]  ; Loading " + node.getNode());
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

			if (name.equals("stack")) {
				System.err.println("Stack at: " + STACK_INDEX + " = " + node.getIdent().getPosition());
				printStack();
			} else if (name.equals("break")) {
				System.err.println("Stack at: " + STACK_INDEX + " = " + node.getIdent().getPosition());
				printStack();
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
			} else if (name.equals("data")) {
				writedataln(((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr()).getString().getValue());
			} else if (name.equals("text")) {
				writetextln(((StringLitNode) ((FunArgValNode) node.getArgs().getChildren().get(0)).getExpr()).getString().getValue());
			} else {
				throw new CompilerException("Preset function not found: " + name);
			}

		} else {

			FunScopeDescriptor desc = container.getFunDescriptor(node);
			if (desc == null) {
				throw new CompilerException("Couldn't find descriptor for: " + name);
			}
			FunDefNode def = desc.getNode();

			int wantedArgCount = def.getArgs().getChildren().size();
			int gotArgCount = node.getArgs().getChildren().size();

			if (wantedArgCount != gotArgCount) {
				throw new CompilerException("Argument count do not match, got " + gotArgCount + " but wanted " + wantedArgCount);
			}

			final int startStackIndex = vStack.size() - 1;

			for (Node arg : node.getArgs()) {
				if (!(arg instanceof FunArgValNode)) {
					throw new CompilerException("Arg val should be " + FunArgValNode.class.getSimpleName());
				}
				compileComputeExpr("eax", ((FunArgValNode) arg).getExpr());
				writeinstln("push eax");

				((FunArgValNode) arg).setStackSize(getStackSize(((FunArgValNode) arg).getExpr()));

				if (((FunArgValNode) arg).getExpr() instanceof ArrayInit) {
					// on the spot init
					((FunArgValNode) arg).setStackSize(((FunArgValNode) arg).getStackSize() + 4);
				}

				push(arg);
			}

			push(node);

			printStack();
			System.err.println(startStackIndex + " = " + vStack.get(startStackIndex) + "size: " + (getStackSize(startStackIndex) - 4) + " last: " + vStack.peek());

			writeinstln("call " + desc.getAsmName() + "  ; " + desc.getIdentifier().getValue());

			pop();
			int size = getStackSize(startStackIndex);
			writeinstln("add dword esp, " + size + "  ; Free mem from fun call");

			for (Node n : node.getArgs()) {
				pop();
			}
		}
	}

	private void printStack() {
		GlobalLogger.log();
		vStack.forEach(System.out::println);
	}

	private int getStackSize(Node node) throws CompilerException {
		if (node instanceof VarNumNode) {
			return ((LetTypeDefNode) ((LetScopeDescriptor) node.getParentContainer().getLetTypeDefDescriptor((VarNumNode) node)).getNode()).getType().getSize();
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
			return ((ArrayInit) node).getStackSize();
		} else if (node instanceof BinaryOpNode || node instanceof LogicalOpNode || node instanceof ComparisonOpNode) {
			return 4;
		} else {
			implement(node);
		}
		return 0;
	}

	private int getStackSize(int startStackIndex) throws CompilerException {
		GlobalLogger.log();
		int size = 0;
		for (int i = vStack.size() - 1; i > startStackIndex; i--) {
			Node stackNode = vStack.get(i);
			System.err.println("comp: " + stackNode);
			size += getStackSize(stackNode);
		}
		System.err.println(" == " + size);
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

		if (def.getType().isPointer()) { // pointer
			if (node.isArrayOffset()) { // array
				compileComputeExpr("ebx", node.getOffset());
				writeinstln("imul ebx, 4");

				if (def.isiStatic()) { // static
					writeinstln("mov " + reg + ", [" + desc.getAsmName() + " + ebx]  ; compileLoadVarNum(" + node + "): static");
				} else { // local
					writeinstln("mov ecx, [esp + " + (STACK_INDEX - def.getStackIndex() - 4) + "]  ; Loading pointer, index = " + STACK_INDEX + ", size = " + STACK_INDEX);
					writeinstln("add ecx, ebx");
					writeinstln("mov " + reg + ", [ecx] ; compileLoadVarNum(" + node + "): local");
				}
			} else {
				if (def.isiStatic()) { // static
					writeinstln("mov " + reg + ", " + desc.getAsmName() + "  ; compileLoadVarNum(" + node + "): static");
				} else {
					System.err.println("cindex! " + def.isArg() + " = " + def.getIdent().getValue() + " = " + def.getStackIndex() + " & " + STACK_INDEX + " & " + def.getStackSize());
					writeinstln("mov " + reg + ", [esp + " + (STACK_INDEX - def.getStackIndex() - 4) + "] ; compileLoadVarNum(" + node + "): local");
				}
			}
		} else if (!def.getType().isPointer()) { // direct access
			if (def.isiStatic()) {
				writeinstln("mov " + reg + ", [" + desc.getAsmName() + "]  ; compileLoadVarNum(" + node + "): static");
			} else {
				writeinstln("mov " + reg + ", [esp + " + (STACK_INDEX - def.getStackIndex() - 4) + "]  ; compileLoadVarNum(" + node + "): local");
			}
		}
	}

	private void implement() throws CompilerException {
		throw new CompilerException("not implemented; ");
	}

	private void implement(Object obj) throws CompilerException {
		throw new CompilerException("not implemented: " + obj.getClass().getName());
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
