package lu.pcy113.l3.compiler.llvm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;
import lu.pcy113.l3.parser.ast.ctrl.BreakNode;
import lu.pcy113.l3.parser.ast.ctrl.ForNode;
import lu.pcy113.l3.parser.ast.ctrl.IfNode;
import lu.pcy113.l3.parser.ast.ctrl.WhileNode;
import lu.pcy113.l3.parser.ast.ctrl.YieldNode;
import lu.pcy113.l3.parser.ast.expr.AssignmentNode;
import lu.pcy113.l3.parser.ast.expr.CallNode;
import lu.pcy113.l3.parser.ast.expr.ExpressionStatementNode;
import lu.pcy113.l3.parser.ast.expr.NameNode;
import lu.pcy113.l3.parser.ast.expr.NewArrayNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.let.VariableDeclarationNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.macro.MacroCallNode;
import lu.pcy113.l3.parser.ast.macro.PreprocessorDirectiveNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.math.UnaryExpressionNode;
import lu.pcy113.l3.parser.ast.method.MethodDefNode;
import lu.pcy113.l3.parser.ast.method.ParameterNode;
import lu.pcy113.l3.parser.ast.scope.BlockNode;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public final class LlvmIrGenerator {

	private final RuntimeNode runtime;
	private final StringBuilder globals = new StringBuilder();
	private final StringBuilder functions = new StringBuilder();
	private final StringBuilder header = new StringBuilder();
	private final List<MethodDefNode> methods = new ArrayList<>();
	private final Map<String, List<MethodDefNode>> methodsByName = new HashMap<>();
	private final Map<MethodDefNode, String> methodSymbols = new IdentityHashMap<>();
	private final Set<String> declaredExternals = new HashSet<>();
	private int stringCounter = 0;

	protected String createStringConstant(final StringLiteralToken stringLiteralToken) {
		final byte[] bytes = (stringLiteralToken.getValue() + "\0").getBytes(StandardCharsets.UTF_8);
		final String name = ".str." + this.stringCounter++;

		this.globals.append("@").append(name).append(" = private unnamed_addr constant [").append(bytes.length)
				.append(" x i8] c\"").append(LlvmIrGenerator.escapeLlvmString(bytes)).append("\"\n");

		return name;
	}

	private static String escapeLlvmString(final byte[] bytes) {
		final StringBuilder out = new StringBuilder();

		for (final byte b : bytes) {
			final int v = b & 0xff;

			if (v >= 32 && v <= 126 && v != '"' && v != '\\') {
				out.append((char) v);
			} else {
				out.append("\\");
				out.append(String.format("%02X", v));
			}
		}

		return out.toString();
	}

	public LlvmIrGenerator(final RuntimeNode runtime) {
		this.runtime = runtime;
	}

	public String generate() {
		this.collectMethods(this.runtime.getMainNode());
		for (final FileNode file : this.runtime.getFiles()) {
			this.collectMethods(file);
		}

		for (final MethodDefNode method : this.methodSymbols.keySet()) {
			new FunctionEmitter(method).emit();
		}

		this.emitStartFunction();

		return header.toString() + "\n" + this.globals + "\n" + this.functions;
	}

	private void emitStartFunction() {
		if (!this.methodsByName.containsKey("main")) {
			return;
		}

		final MethodDefNode main = this.methodsByName.get("main").get(0);
		final String mainReturnType = LlvmIrGenerator.llvmType(main.getReturnType());

		this.functions.append("define void @_start() {\n");
		this.functions.append("entry:\n");

		if ("void".equals(mainReturnType)) {
			this.functions.append("  call void @main()\n");
			this.functions.append("  call void asm sideeffect \"syscall\", \"{rax},{rdi}\"(i64 60, i64 0)\n");
		} else {
			this.functions.append("  %code = call ").append(mainReturnType).append(" @main()\n");

			if (!"i64".equals(mainReturnType)) {
				this.functions.append("  %exit_code = sext ").append(mainReturnType).append(" %code to i64\n");
				this.functions
						.append("  call void asm sideeffect \"syscall\", \"{rax},{rdi}\"(i64 60, i64 %exit_code)\n");
			} else {
				this.functions.append("  call void asm sideeffect \"syscall\", \"{rax},{rdi}\"(i64 60, i64 %code)\n");
			}
		}

		this.functions.append("  unreachable\n");
		this.functions.append("}\n\n");
	}

	private void collectMethods(final FileNode fileNode) {
		for (final Node node : fileNode) {
			if (node instanceof final MethodDefNode method) {
				this.methods.add(method);
				this.methodsByName.computeIfAbsent(method.getName(), ignored -> new ArrayList<>()).add(method);
			} else {
				throw new IllegalArgumentException(node.toString());
			}
		}

		this.computeMethodSymbols();
	}

	private void computeMethodSymbols() {
		for (final MethodDefNode method : this.methods) {
			final String name = method.getName();

			if ("main".equals(name)) {
				this.methodSymbols.put(method, "main");
				continue;
			}

			final List<MethodDefNode> overloads = this.methodsByName.get(name);

			if (overloads == null || overloads.size() == 1) {
				this.methodSymbols.put(method, name);
				continue;
			}

			final StringBuilder symbol = new StringBuilder(name).append("__");

			for (int i = 0; i < method.getParameters().size(); i++) {
				if (i > 0) {
					symbol.append("_");
				}

				symbol.append(LlvmIrGenerator.mangleType(method.getParameters().get(i).getType()));
			}

			this.methodSymbols.put(method, symbol.toString());
		}
	}

	private static String mangleType(final TypeReferenceNode type) {
		if (type.getPointerDepth() > 0) {
			return "ptr" + type.getPointerDepth();
		}

		return switch (type.getName()) {
		case "void" -> "void";
		case "int8" -> "i8";
		case "int16" -> "i16";
		case "int32" -> "i32";
		case "int64" -> "i64";
		default -> LlvmIrGenerator.sanitizeSymbol(type.getName());
		};
	}

	private static String sanitizeSymbol(final String value) {
		return value.replaceAll("[^a-zA-Z0-9_]", "_");
	}

	private String addStringLiteral(final String value) {
		final String name = ".str." + this.stringCounter++;
		final byte[] bytes = (value + "\0").getBytes(java.nio.charset.StandardCharsets.UTF_8);
		this.globals.append("@").append(name).append(" = private unnamed_addr constant [").append(bytes.length)
				.append(" x i8] c\"");
		for (final byte b : bytes) {
			final int v = b & 0xFF;
			if (v >= 32 && v <= 126 && v != '\\' && v != '"') {
				this.globals.append((char) v);
			} else {
				this.globals.append(String.format("\\%02X", v));
			}
		}
		this.globals.append("\"\n");
		return "getelementptr inbounds ([" + bytes.length + " x i8], ptr @" + name + ", i64 0, i64 0)";
	}

	private static String escapeSymbol(final String symbol) {
		return symbol.replaceAll("[^A-Za-z0-9_$.-]", "_");
	}

	private static String llvmType(final TypeReferenceNode type) {
		if (type == null || type.isVoid()) {
			return "void";
		}
		if (type.getPointerDepth() > 0) {
			return "ptr";
		}
		if (type.isFloatingPoint()) {
			return type.bitWidth() == 32 ? "float" : "double";
		}
		return "i" + type.bitWidth();
	}

	private static String zeroValue(final String llvmType) {
		if (llvmType != null) {
			switch (llvmType) {
			case "void":
				return "";
			case "float":
				return "0.0";
			case "double":
				return "0.0";
			case "ptr":
				return "null";
			default:
				break;
			}
		}
		return "0";
	}

	private record Value(String type, String ir) {
	}

	private static final class Variable {
		final String pointer;
		final String type;

		Variable(final String pointer, final String type) {
			this.pointer = pointer;
			this.type = type;
		}
	}

	private final class FunctionEmitter {
		private final MethodDefNode method;
		private final StringBuilder out = new StringBuilder();
		private final Map<String, Variable> variables = new HashMap<>();
		private final ArrayDeque<String> breakLabels = new ArrayDeque<>();
		private int tempCounter;
		private int labelCounter;
		private boolean terminated;

		FunctionEmitter(final MethodDefNode method) {
			this.method = method;
		}

		void emit() {
			final String returnType = LlvmIrGenerator.llvmType(this.method.getReturnType());
			LlvmIrGenerator.this.functions.append("define ").append(returnType).append(" @")
					.append(LlvmIrGenerator.this.methodSymbols.get(this.method)).append("(");
			for (int i = 0; i < this.method.getParameters().size(); i++) {
				final ParameterNode param = this.method.getParameters().get(i);
				if (i > 0) {
					LlvmIrGenerator.this.functions.append(", ");
				}
				LlvmIrGenerator.this.functions.append(LlvmIrGenerator.llvmType(param.getType())).append(" %")
						.append(LlvmIrGenerator.escapeSymbol(param.getName()));
			}
			LlvmIrGenerator.this.functions.append(") {\n");

			if (this.isRawLlvmBody()) {
				final String rawBody = this.getRawLlvmBody();

				for (final String line : rawBody.split("\\R", -1)) {
					LlvmIrGenerator.this.functions.append(line).append('\n');
				}

				LlvmIrGenerator.this.functions.append("}\n\n");
				return;
			}

			LlvmIrGenerator.this.functions.append("entry:\n");

			for (final ParameterNode param : this.method.getParameters()) {
				final String type = LlvmIrGenerator.llvmType(param.getType());
				final String alloca = this.nextTemp();
				this.emitLine(alloca + " = alloca " + type);
				this.emitLine(
						"store " + type + " %" + LlvmIrGenerator.escapeSymbol(param.getName()) + ", ptr " + alloca);
				this.variables.put(param.getName(), new Variable(alloca, type));
			}

			this.emitBlock(this.method.getBody());
			if (!this.terminated) {
				if ("void".equals(returnType)) {
					this.emitLine("ret void");
				} else {
					this.emitLine("ret " + returnType + " " + LlvmIrGenerator.zeroValue(returnType));
				}
			}
			LlvmIrGenerator.this.functions.append(this.out).append("}\n\n");
		}

		private boolean isRawLlvmBody() {
			if (this.method.getBody().getChildren().size() != 1) {
				return false;
			}

			final Node statement = this.method.getBody().getChildren().get(0);

			if (!(statement instanceof final ExpressionStatementNode exprStmt)
					|| !(exprStmt.getExpression() instanceof final MacroCallNode macro)) {
				return false;
			}

			return "llvm".equals(macro.getName());
		}

		private String getRawLlvmBody() {
			final ExpressionStatementNode exprStmt = (ExpressionStatementNode) this.method.getBody().getChildren()
					.get(0);
			final MacroCallNode macro = (MacroCallNode) exprStmt.getExpression();

			if (macro.getArguments().isEmpty()) {
				throw new L3Exception("llvm#(...) needs one string argument");
			}

			final Node arg = macro.getArguments().get(0);

			if (!(arg instanceof final StringLiteralNode str)) {
				throw new L3Exception("llvm#(...) only accepts a string literal for now");
			}

			return str.getValue().getValue();
		}

		private void emitBlock(final BlockNode block) {
			for (final Node child : block.getChildren()) {
				this.emitStatement(child);
				if (this.terminated) {
					break;
				}
			}
		}

		private void emitStatement(final Node node) {
			if (node instanceof final VariableDeclarationNode variable) {
				this.emitVariable(variable);
			} else if (node instanceof final ExpressionStatementNode expression) {
				this.emitExpression(expression.getExpression());
			} else if (node instanceof final ReturnNode ret) {
				this.emitReturn(ret);
			} else if (node instanceof final IfNode ifNode) {
				this.emitIf(ifNode);
			} else if (node instanceof final WhileNode whileNode) {
				this.emitWhile(whileNode);
			} else if (node instanceof final ForNode forNode) {
				this.emitFor(forNode);
			} else if (node instanceof final BlockNode block) {
				this.emitBlock(block);
			} else if (node instanceof BreakNode) {
				this.emitBreak();
			} else if (node instanceof final MacroCallNode macro) {
				this.emitMacro(macro);
			} else if (node instanceof final YieldNode yield) {
				// Yield only has a value when the block is used as an expression.
				if (yield.getExpression() != null) {
					this.emitExpression(yield.getExpression());
				}
			} else if (node instanceof final PreprocessorDirectiveNode directive) {
				this.emitComment("preprocessor placeholder #" + directive.getName());
			} else {
				throw new L3Exception("LLVM backend does not support statement: " + node.getClass().getName());
			}
		}

		private void emitVariable(final VariableDeclarationNode variable) {
			final String type = LlvmIrGenerator.llvmType(variable.getType());
			final String pointer = this.nextTemp();
			this.emitLine(pointer + " = alloca " + type);
			this.variables.put(variable.getName(), new Variable(pointer, type));
			final Value value = variable.hasInitializer()
					? this.cast(this.emitExpression(variable.getInitializer()), type)
					: new Value(type, LlvmIrGenerator.zeroValue(type));
			if (!"void".equals(type)) {
				this.emitLine("store " + type + " " + value.ir + ", ptr " + pointer);
			}
		}

		private void emitReturn(final ReturnNode ret) {
			final String returnType = LlvmIrGenerator.llvmType(this.method.getReturnType());
			if (!ret.hasExpression() || "void".equals(returnType)) {
				this.emitLine("ret void");
			} else {
				final Value value = this.cast(this.emitExpression(ret.getExpression()), returnType);
				this.emitLine("ret " + returnType + " " + value.ir);
			}
			this.terminated = true;
		}

		private void emitIf(final IfNode ifNode) {
			final Value condition = this.toBool(this.emitExpression(ifNode.getCondition()));
			final String thenLabel = this.nextLabel("if.then");
			final String elseLabel = ifNode.hasElseBranch() ? this.nextLabel("if.else") : null;
			final String endLabel = this.nextLabel("if.end");
			this.emitLine("br i1 " + condition.ir + ", label %" + thenLabel + ", label %"
					+ (elseLabel == null ? endLabel : elseLabel));

			this.emitLabel(thenLabel);
			final boolean oldTerminated = this.terminated;
			this.terminated = false;
			this.emitStatement(ifNode.getThenBranch());
			final boolean thenTerminated = this.terminated;
			if (!thenTerminated) {
				this.emitLine("br label %" + endLabel);
			}

			boolean elseTerminated = false;
			if (ifNode.hasElseBranch()) {
				this.emitLabel(elseLabel);
				this.terminated = false;
				this.emitStatement(ifNode.getElseBranch());
				elseTerminated = this.terminated;
				if (!elseTerminated) {
					this.emitLine("br label %" + endLabel);
				}
			}

			this.emitLabel(endLabel);
			this.terminated = oldTerminated || ifNode.hasElseBranch() && thenTerminated && elseTerminated;
		}

		private void emitWhile(final WhileNode whileNode) {
			final String condLabel = this.nextLabel("while.cond");
			final String bodyLabel = this.nextLabel("while.body");
			final String endLabel = this.nextLabel("while.end");
			this.emitLine("br label %" + condLabel);
			this.emitLabel(condLabel);
			final Value condition = this.toBool(this.emitExpression(whileNode.getCondition()));
			this.emitLine("br i1 " + condition.ir + ", label %" + bodyLabel + ", label %" + endLabel);
			this.emitLabel(bodyLabel);
			this.breakLabels.push(endLabel);
			this.terminated = false;
			this.emitStatement(whileNode.getBody());
			this.breakLabels.pop();
			if (!this.terminated) {
				this.emitLine("br label %" + condLabel);
			}
			this.emitLabel(endLabel);
			this.terminated = false;
			if (whileNode.hasElseBranch()) {
				this.emitStatement(whileNode.getElseBranch());
			}
		}

		private void emitFor(final ForNode forNode) {
			if (forNode.getInitializer() != null) {
				if (forNode.getInitializer() instanceof final VariableDeclarationNode var) {
					this.emitVariable(var);
				} else {
					this.emitExpression(forNode.getInitializer());
				}
			}
			final String condLabel = this.nextLabel("for.cond");
			final String bodyLabel = this.nextLabel("for.body");
			final String updateLabel = this.nextLabel("for.update");
			final String endLabel = this.nextLabel("for.end");
			this.emitLine("br label %" + condLabel);
			this.emitLabel(condLabel);
			if (forNode.getCondition() == null) {
				this.emitLine("br label %" + bodyLabel);
			} else {
				final Value condition = this.toBool(this.emitExpression(forNode.getCondition()));
				this.emitLine("br i1 " + condition.ir + ", label %" + bodyLabel + ", label %" + endLabel);
			}
			this.emitLabel(bodyLabel);
			this.breakLabels.push(endLabel);
			this.terminated = false;
			this.emitStatement(forNode.getBody());
			this.breakLabels.pop();
			if (!this.terminated) {
				this.emitLine("br label %" + updateLabel);
			}
			this.emitLabel(updateLabel);
			this.terminated = false;
			if (forNode.getUpdate() != null) {
				this.emitExpression(forNode.getUpdate());
			}
			this.emitLine("br label %" + condLabel);
			this.emitLabel(endLabel);
			this.terminated = false;
		}

		private void emitBreak() {
			if (this.breakLabels.isEmpty()) {
				throw new L3Exception("break used outside loop.");
			}
			this.emitLine("br label %" + this.breakLabels.peek());
			this.terminated = true;
		}

		private Value emitExpression(final Node node) {
			if (node instanceof final RawValueNode raw) {
				return raw.value;
			} else if (node instanceof final NumericLiteralNode literal) {
				return this.numericLiteral(literal);
			} else if (node instanceof final StringLiteralNode literal) {
				return new Value("ptr", LlvmIrGenerator.this.addStringLiteral(literal.getValue().getValue()));
			} else if (node instanceof final NameNode name) {
				return this.loadName(name.getName());
			} else if (node instanceof final BinaryExpressionNode binary) {
				return this.emitBinary(binary);
			} else if (node instanceof final UnaryExpressionNode unary) {
				return this.emitUnary(unary);
			} else if (node instanceof final AssignmentNode assignment) {
				return this.emitAssignment(assignment);
			} else if (node instanceof final CallNode call) {
				return this.emitCall(call);
			} else if (node instanceof final MacroCallNode macro) {
				this.emitMacro(macro);
				return new Value("void", "");
			} else if (node instanceof final BlockNode block) {
				return this.emitComputedBlock(block);
			} else if (node instanceof final NewArrayNode array) {
				return this.emitNewArray(array);
			} else if (node instanceof final StringLiteralNode str) {
				return this.emitStringLiteral(str);
			}
			throw new L3Exception("LLVM backend does not support expression: " + node.getClass().getName());
		}

		private Value emitStringLiteral(final StringLiteralNode str) {
			final String globalName = LlvmIrGenerator.this.createStringConstant(str.getValue());
			final byte[] bytes = (str.getValue() + "\0").getBytes(StandardCharsets.UTF_8);

			final String tmp = this.nextTemp();

			this.emitLine(tmp + " = getelementptr [" + bytes.length + " x i8], ptr @" + globalName + ", i64 0, i64 0");

			return new Value("ptr", tmp);
		}

		private Value emitNewArray(final NewArrayNode array) {
			final String elementType = LlvmIrGenerator.llvmType(array.getElementType());
			final Value rawLength = this.emitExpression(array.getLength());
			final Value length = this.cast(rawLength, "i64");

			final String allocation = this.nextTemp();
			final String pointer = this.nextTemp();

			this.emitLine(allocation + " = alloca " + elementType + ", i64 " + length.ir);
			this.emitLine(pointer + " = getelementptr " + elementType + ", ptr " + allocation + ", i64 0");

			return new Value("ptr", pointer);
		}

		private Value numericLiteral(final NumericLiteralNode literal) {
			final NumericLiteralToken token = literal.getValue();
			if (token.isBoolean()) {
				return new Value("i1", token.booleanValue() ? "1" : "0");
			}
			if (token.isFloat()) {
				return new Value("float", String.valueOf(token.floatValue()));
			}
			if (token.isDouble()) {
				return new Value("double", String.valueOf(token.doubleValue()));
			}
			final Object value = token.getValue();
			return new Value("i64", String.valueOf(value));
		}

		private Value loadName(final String name) {
			final Variable variable = this.variables.get(name);
			if (variable == null) {
				throw new L3Exception("Unknown variable: " + name);
			}
			final String temp = this.nextTemp();
			this.emitLine(temp + " = load " + variable.type + ", ptr " + variable.pointer);
			return new Value(variable.type, temp);
		}

		private Value emitBinary(final BinaryExpressionNode binary) {
			final Value left = this.emitExpression(binary.getLeft());
			final Value right = this.cast(this.emitExpression(binary.getRight()), left.type);
			final String temp = this.nextTemp();
			final TokenType op = binary.getOperator();

			if (op.matches(TokenType.COMPARAISON)) {
				final String pred = switch (op) {
				case EQUALS -> "eq";
				case NOT_EQUALS -> "ne";
				case LESS -> "slt";
				case LESS_EQUALS -> "sle";
				case GREATER -> "sgt";
				case GREATER_EQUALS -> "sge";
				default -> throw new L3Exception("Unsupported comparison operator: " + op);
				};
				this.emitLine(temp + " = icmp " + pred + " " + left.type + " " + left.ir + ", " + right.ir);
				return new Value("i1", temp);
			}

			final String instruction = switch (op) {
			case PLUS -> "add";
			case MINUS -> "sub";
			case MUL -> "mul";
			case DIV -> "sdiv";
			case MODULO -> "srem";
			case BIT_AND, AND -> "and";
			case BIT_OR, OR -> "or";
			case BIT_XOR, XOR -> "xor";
			case BIT_SHIFT_LEFT -> "shl";
			case BIT_SHIFT_SIGNED_RIGHT -> "ashr";
			case BIT_SHIFT_UNSIGNED_RIGHT -> "lshr";
			default -> throw new L3Exception("Unsupported binary operator: " + op);
			};
			this.emitLine(temp + " = " + instruction + " " + left.type + " " + left.ir + ", " + right.ir);
			return new Value(left.type, temp);
		}

		private Value emitUnary(final UnaryExpressionNode unary) {
			final Value value = this.emitExpression(unary.getChild());
			final String temp = this.nextTemp();
			return switch (unary.getOperator()) {
			case MINUS -> {
				this.emitLine(temp + " = sub " + value.type + " 0, " + value.ir);
				yield new Value(value.type, temp);
			}
			case NOT -> {
				final Value bool = this.toBool(value);
				this.emitLine(temp + " = xor i1 " + bool.ir + ", true");
				yield new Value("i1", temp);
			}
			case BIT_NOT -> {
				this.emitLine(temp + " = xor " + value.type + " " + value.ir + ", -1");
				yield new Value(value.type, temp);
			}
			case PLUS -> value;
			default -> throw new L3Exception("Unsupported unary operator: " + unary.getOperator());
			};
		}

		private Value emitAssignment(final AssignmentNode assignment) {
			if (!(assignment.getTarget() instanceof final NameNode name)) {
				throw new L3Exception("Only simple variable assignment is supported by the first LLVM backend.");
			}
			final Variable variable = this.variables.get(name.getName());
			if (variable == null) {
				throw new L3Exception("Unknown variable: " + name.getName());
			}
			Value value = this.cast(this.emitExpression(assignment.getValue()), variable.type);
			if (assignment.getOperator() != TokenType.STRICT_ASSIGN) {
				final Value current = this.loadName(name.getName());
				final TokenType op = switch (assignment.getOperator()) {
				case PLUS_ASSIGN -> TokenType.PLUS;
				case MINUS_ASSIGN -> TokenType.MINUS;
				case MUL_ASSIGN -> TokenType.MUL;
				case DIV_ASSIGN -> TokenType.DIV;
				case MODULO_ASSIGN -> TokenType.MODULO;
				case BIT_AND_ASSIGN -> TokenType.BIT_AND;
				case BIT_OR_ASSIGN -> TokenType.BIT_OR;
				case BIT_XOR_ASSIGN -> TokenType.BIT_XOR;
				default -> throw new L3Exception("Unsupported assignment operator: " + assignment.getOperator());
				};
				value = this
						.emitBinary(new BinaryExpressionNode(new RawValueNode(current), op, new RawValueNode(value)));
			}
			this.emitLine("store " + variable.type + " " + value.ir + ", ptr " + variable.pointer);
			return value;
		}

		private Value emitCall(final CallNode call) {
			final List<Value> args = new ArrayList<>();

			for (final Node argument : call.getArguments()) {
				args.add(this.emitExpression(argument));
			}

			System.err.println(call.getTarget());
			final MethodDefNode target = this.resolveMethod(((NameNode) call.getTarget()).getName(), args);
			final String symbol = LlvmIrGenerator.this.methodSymbols.get(target);
			final String returnType = LlvmIrGenerator.llvmType(target.getReturnType());

			final StringBuilder line = new StringBuilder();

			final String result;

			if ("void".equals(returnType)) {
				result = "";
				line.append("call void @").append(symbol).append("(");
			} else {
				result = this.nextTemp();
				line.append(result).append(" = call ").append(returnType).append(" @").append(symbol).append("(");
			}

			for (int i = 0; i < args.size(); i++) {
				if (i > 0) {
					line.append(", ");
				}

				final Value arg = args.get(i);
				final TypeReferenceNode expectedType = target.getParameters().get(i).getType();
				final String expectedLlvmType = LlvmIrGenerator.llvmType(expectedType);

				final Value casted = this.cast(arg, expectedLlvmType);

				line.append(expectedLlvmType).append(" ").append(casted.ir);
			}

			line.append(")");
			this.emitLine(line.toString());

			return new Value(returnType, result);
		}

		private MethodDefNode resolveMethod(final String name, final List<Value> args) {
			final List<MethodDefNode> candidates = LlvmIrGenerator.this.methodsByName.get(name);

			if (candidates == null || candidates.isEmpty()) {
				throw new L3Exception("Unknown function: " + name);
			}

			MethodDefNode best = null;
			int bestScore = Integer.MAX_VALUE;

			for (final MethodDefNode candidate : candidates) {
				if (candidate.getParameters().size() != args.size()) {
					continue;
				}

				int score = 0;
				boolean valid = true;

				for (int i = 0; i < args.size(); i++) {
					final String actual = args.get(i).type;
					final String expected = LlvmIrGenerator.llvmType(candidate.getParameters().get(i).getType());

					if (actual.equals(expected)) {
						continue;
					}

					if (FunctionEmitter.canCast(actual, expected)) {
						score += 10;
						continue;
					}

					valid = false;
					break;
				}

				if (valid && score < bestScore) {
					best = candidate;
					bestScore = score;
				}
			}

			if (best == null) {
				throw new L3Exception("No matching overload for call: " + name + "("
						+ args.stream().map(v -> v.type).collect(Collectors.joining(", ")) + ")");
			}

			return best;
		}

		private static boolean canCast(final String actual, final String expected) {
			if (actual.equals(expected) || (actual.startsWith("i") && expected.startsWith("i"))) {
				return true;
			}

			return false;
		}

		private void emitMacro(final MacroCallNode macro) {
			if (macro.isBreakpoint()) {
				this.emitComment("break#" + macro.getArguments().stream().map(Node::toSourceString).toList());
				this.emitLine("call void asm sideeffect \"int3\", \"\"()");
			} else if (macro.isInlineAssembly()) {
				final String asm = macro.getArguments().isEmpty() ? ""
						: this.stringArgument(macro.getArguments().get(0));
				final String comment = macro.getArguments().size() > 1
						? this.stringArgument(macro.getArguments().get(1))
						: "";
				if (!comment.isEmpty()) {
					this.emitComment("asm#: " + comment.replace("\n", " "));
				}
				this.emitLine("call void asm sideeffect \"" + this.escapeInlineAsm(asm)
						+ "\", \"~{dirflag},~{fpsr},~{flags}\"()");
			} else if (macro.isLlvm()) {
				if (macro.getArguments().isEmpty()) {
					this.emitComment("llvm#() empty");
				} else {
					this.emitComment("llvm# raw IR");
					this.emitLine(this.stringArgument(macro.getArguments().get(0)));
				}
			} else {
				this.emitComment("unknown macro #" + macro.getName() + " ignored by first LLVM backend");
			}
		}

		private Value emitComputedBlock(final BlockNode block) {
			Value yielded = null;
			for (final Node child : block.getChildren()) {
				if (child instanceof final YieldNode yield) {
					yielded = yield.getExpression() == null ? new Value("i64", "0")
							: this.emitExpression(yield.getExpression());
					break;
				}
				this.emitStatement(child);
				if (this.terminated) {
					break;
				}
			}
			return yielded == null ? new Value("i64", "0") : yielded;
		}

		private String stringArgument(final Node node) {
			if (node instanceof final StringLiteralNode string) {
				return string.getValue().getValue();
			}
			final Value value = this.emitExpression(node);
			return value.ir;
		}

		private Value toBool(final Value value) {
			if ("i1".equals(value.type)) {
				return value;
			}
			final String temp = this.nextTemp();
			this.emitLine(
					temp + " = icmp ne " + value.type + " " + value.ir + ", " + LlvmIrGenerator.zeroValue(value.type));
			return new Value("i1", temp);
		}

		private Value cast(final Value value, final String targetType) {
			if (value.type.equals(targetType) || "void".equals(value.type) || "void".equals(targetType)) {
				return value;
			}
			if ("ptr".equals(value.type) || "ptr".equals(targetType)) {
				return value;
			}
			if (value.type.startsWith("i") && targetType.startsWith("i")) {
				final int sourceBits = Integer.parseInt(value.type.substring(1));
				final int targetBits = Integer.parseInt(targetType.substring(1));
				final String temp = this.nextTemp();
				if (sourceBits > targetBits) {
					this.emitLine(temp + " = trunc " + value.type + " " + value.ir + " to " + targetType);
				} else if (sourceBits < targetBits) {
					this.emitLine(temp + " = sext " + value.type + " " + value.ir + " to " + targetType);
				} else {
					return value;
				}
				return new Value(targetType, temp);
			}
			return value;
		}

		private String nextTemp() {
			return "%t" + this.tempCounter++;
		}

		private String nextLabel(final String prefix) {
			return prefix + "." + this.labelCounter++;
		}

		private void emitLine(final String line) {
			if (!line.isBlank()) {
				this.out.append("  ").append(line).append('\n');
			}
		}

		private void emitComment(final String lines) {
			if (lines.isBlank()) {
				return;
			}
			Arrays.stream(lines.split("\n")).forEach(c -> this.out.append("  ; " + c + "\n"));
		}

		private void emitLabel(final String label) {
			this.out.append(label).append(":\n");
			this.terminated = false;
		}

		private String escapeInlineAsm(final String asm) {
			return asm.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\0A");
		}
	}

	private static final class RawValueNode extends Node {
		private final Value value;

		RawValueNode(final Value value) {
			this.value = value;
		}
	}
}
