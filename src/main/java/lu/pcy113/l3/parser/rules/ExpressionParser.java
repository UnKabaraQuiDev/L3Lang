package lu.pcy113.l3.parser.rules;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.expr.AssignmentNode;
import lu.pcy113.l3.parser.ast.expr.CallNode;
import lu.pcy113.l3.parser.ast.expr.NameNode;
import lu.pcy113.l3.parser.ast.expr.NewArrayNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.macro.MacroCallNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.math.UnaryExpressionNode;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public final class ExpressionParser {

	private static final Map<TokenType, Integer> PRECEDENCE = new EnumMap<>(TokenType.class);

	static {
		ExpressionParser.PRECEDENCE.put(TokenType.STRICT_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.PLUS_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.MINUS_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.MUL_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.DIV_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.MODULO_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_OR_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_AND_ASSIGN, 1);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_XOR_ASSIGN, 1);

		ExpressionParser.PRECEDENCE.put(TokenType.OR, 2);
		ExpressionParser.PRECEDENCE.put(TokenType.XOR, 3);
		ExpressionParser.PRECEDENCE.put(TokenType.AND, 4);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_OR, 5);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_XOR, 6);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_AND, 7);
		ExpressionParser.PRECEDENCE.put(TokenType.EQUALS, 8);
		ExpressionParser.PRECEDENCE.put(TokenType.NOT_EQUALS, 8);
		ExpressionParser.PRECEDENCE.put(TokenType.LESS, 9);
		ExpressionParser.PRECEDENCE.put(TokenType.LESS_EQUALS, 9);
		ExpressionParser.PRECEDENCE.put(TokenType.GREATER, 9);
		ExpressionParser.PRECEDENCE.put(TokenType.GREATER_EQUALS, 9);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_SHIFT_LEFT, 10);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_SHIFT_SIGNED_RIGHT, 10);
		ExpressionParser.PRECEDENCE.put(TokenType.BIT_SHIFT_UNSIGNED_RIGHT, 10);
		ExpressionParser.PRECEDENCE.put(TokenType.PLUS, 11);
		ExpressionParser.PRECEDENCE.put(TokenType.MINUS, 11);
		ExpressionParser.PRECEDENCE.put(TokenType.MUL, 12);
		ExpressionParser.PRECEDENCE.put(TokenType.DIV, 12);
		ExpressionParser.PRECEDENCE.put(TokenType.MODULO, 12);
	}

	private final RuleParser parser;

	public ExpressionParser(final RuleParser parser) {
		this.parser = parser;
	}

	public Node parseExpression() {
		return this.parseExpression(1);
	}

	private Node parseExpression(final int minPrecedence) {
		Node left = this.parsePrefix();

		while (true) {
			final TokenType operator = this.parser.ctx().peek();
			final Integer precedence = ExpressionParser.PRECEDENCE.get(operator);
			if (precedence == null || precedence < minPrecedence) {
				break;
			}

			this.parser.ctx().consume();
			final boolean assignment = operator.matches(TokenType.ASSIGN);
			final Node right = this.parseExpression(assignment ? precedence : precedence + 1);
			left = assignment ? new AssignmentNode(left, right, operator)
					: new BinaryExpressionNode(left, operator, right);
		}

		return left;
	}

	private Node parsePrefix() {
		final TokenType current = this.parser.ctx().peek();
		if (current == null) {
			throw this.parser.ctx().error("Unexpected end of expression");
		}

		return switch (current) {
		case PLUS, MINUS, NOT, BIT_NOT, PLUS_PLUS, MINUS_MINUS -> {
			final TokenType operator = this.parser.ctx().consume().getType();
			yield this.parsePostfix(new UnaryExpressionNode(this.parsePrefix(), operator, true));
		}
		case PAREN_OPEN -> this.parseParenthesized();
		case CURLY_OPEN -> this.parser.parseBlock(true);
		case IDENT -> this.parsePostfix(this.parseName());
		case BREAK -> this.parseBreakMacroExpression();
		case STRING_LIT -> this.parsePostfix(
				new StringLiteralNode((StringLiteralToken) this.parser.ctx().consume(TokenType.STRING_LIT)));
		case NUM_LIT -> this.parsePostfix(
				new NumericLiteralNode((NumericLiteralToken) this.parser.ctx().consume(TokenType.NUM_LIT)));
		case NEW -> this.parseNewExpression();
		default -> throw new L3Exception(
				"Unexpected token in expression: " + this.parser.ctx().describe(this.parser.ctx().token()));
		};
	}

	private Node parseNewExpression() {
		this.parser.ctx().consume(TokenType.NEW);

		final TypeReferenceNode elementType = this.parser.parseTypeReference();

		this.parser.ctx().consume(TokenType.BRACKET_OPEN);
		final Node length = this.parseExpression();
		this.parser.ctx().consume(TokenType.BRACKET_CLOSE);

		return this.parsePostfix(new NewArrayNode(elementType, length));
	}

	private Node parseParenthesized() {
		this.parser.ctx().consume(TokenType.PAREN_OPEN);
		final Node expression = this.parseExpression();
		this.parser.ctx().consume(TokenType.PAREN_CLOSE);
		return this.parsePostfix(expression);
	}

	private Node parseName() {
		final IdentifierToken identifier = this.parser.ctx().consumeIdentifier();
		List<TypeReferenceNode> genericArguments = List.of();
		if (this.parser.ctx().peek(TokenType.LESS) && this.parser.looksLikeGenericArgumentList(0)) {
			genericArguments = this.parser.parseGenericTypeArguments();
		}
		return new NameNode(identifier.getValue(), genericArguments);
	}

	private Node parseBreakMacroExpression() {
		this.parser.ctx().consume(TokenType.BREAK);
		if (!this.parser.ctx().peek(TokenType.HASH)) {
			throw this.parser.ctx().error("Plain break is a statement, not an expression");
		}
		this.parser.ctx().consume(TokenType.HASH);
		this.parser.ctx().consume(TokenType.PAREN_OPEN);
		final List<Node> args = this.parseArgumentList(TokenType.PAREN_CLOSE);
		this.parser.ctx().consume(TokenType.PAREN_CLOSE);
		return new MacroCallNode("break", args);
	}

	private Node parsePostfix(Node expression) {
		while (true) {
			if (this.parser.ctx().peek(TokenType.PAREN_OPEN)) {
				this.parser.ctx().consume(TokenType.PAREN_OPEN);
				final List<Node> args = this.parseArgumentList(TokenType.PAREN_CLOSE);
				this.parser.ctx().consume(TokenType.PAREN_CLOSE);
				expression = new CallNode(expression, args);
			} else if (this.parser.ctx().peek(TokenType.HASH)) {
				final String macroName = this.extractMacroName(expression);
				this.parser.ctx().consume(TokenType.HASH);
				this.parser.ctx().consume(TokenType.PAREN_OPEN);
				final List<Node> args = this.parseArgumentList(TokenType.PAREN_CLOSE);
				this.parser.ctx().consume(TokenType.PAREN_CLOSE);
				expression = new MacroCallNode(macroName, args);
			} else if (this.parser.ctx().peek(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {
				expression = new UnaryExpressionNode(expression, this.parser.ctx().consume().getType(), false);
			} else {
				return expression;
			}
		}
	}

	private String extractMacroName(final Node expression) {
		if (expression instanceof final NameNode name) {
			return name.getName();
		}
		throw this.parser.ctx().error("Only a simple name can be used before # for a compile-time macro call");
	}

	public List<Node> parseArgumentList(final TokenType closingToken) {
		final List<Node> args = new ArrayList<>();
		if (this.parser.ctx().peek(closingToken)) {
			return args;
		}
		args.add(this.parseExpression());
		while (this.parser.ctx().accept(TokenType.COMMA)) {
			args.add(this.parseExpression());
		}
		return args;
	}
}
