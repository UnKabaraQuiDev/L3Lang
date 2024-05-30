package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.ArrayAccessNode;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetRefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.PointerDerefNode;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.expr.UnaryOpNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;

public class L3ExprParser {

	private int index;
	private final List<Token> input;

	private RuntimeNode root;

	public L3ExprParser(L3Lexer lexer) {
		this(lexer.getTokens());
	}

	public L3ExprParser(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty())
			throw new IllegalArgumentException("Tokens cannot be null or empty.");

		this.input = tokens;

		this.root = new RuntimeNode();
	}

	public void parse() throws ParserException {
		while (hasNext()) {

			parseLineExpr(root);

		}
	}

	private void parseLineExpr(RuntimeNode root2) throws ParserException {

		root2.add(parseExpression());

	}

	public ExprNode parseExpression() throws ParserException {
		return parseLogical();
	}

	private ExprNode parseLogical() throws ParserException {
		ExprNode left = parseComparison();

		while (peek(TokenType.OR, TokenType.AND, TokenType.XOR)) {
			TokenType op = consume().getType();
			Node right = parseComparison();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private ExprNode parseComparison() throws ParserException {
		ExprNode left = parseTerm();

		while (peek(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.LESS, TokenType.LESS_EQUALS, TokenType.GREATER, TokenType.GREATER_EQUALS)) {
			TokenType op = consume().getType();
			ExprNode right = parseTerm();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private ExprNode parseTerm() throws ParserException {
		ExprNode left = parseFactor();

		while (peek(TokenType.PLUS, TokenType.MINUS)) {
			TokenType op = consume().getType();
			ExprNode right = parseFactor();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private ExprNode parseFactor() throws ParserException {
		ExprNode left = parsePrimary();

		if (peek(TokenType.ASSIGN)) {
			left = parseLetSet(left);
		}

		while (peek(TokenType.MUL, TokenType.DIV, TokenType.MODULO, TokenType.BIT_XOR, TokenType.BIT_AND, TokenType.BIT_OR)) {
			TokenType op = consume().getType();
			ExprNode right = parsePrimary();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private ExprNode parsePrimary() throws ParserException {
		if (peek(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {

			return new UnaryOpNode(consume().getType(), parseIdent(), true);

		} else if (peek(TokenType.NUM_LIT)) {

			return new NumLitNode(consume());

		} else if (peek(TokenType.IDENT)) {

			return parseIdent();

		} else if (peek(TokenType.PAREN_OPEN)) {

			consume(TokenType.PAREN_OPEN);
			ExprNode expr = parseExpression();
			consume(TokenType.PAREN_CLOSE);
			return expr;

		} else if (peek(TokenType.DOLLAR)) {

			consume(TokenType.DOLLAR);
			return new PointerDerefNode(parseIdent());

		} else if (peek(TokenType.COLON)) {

			consume(TokenType.COLON);
			return new LetRefNode(parseIdent());

		} else {
			throw new RuntimeException("Unexpected token: " + peek().getType());
		}
	}

	private ExprNode parseIdent() throws ParserException {
		IdentifierLitNode ident = new IdentifierLitNode((IdentifierToken) consume());

		// (parsed).not.not.func();

		while (peek(0, TokenType.DOT) && peek(1, TokenType.IDENT)) {
			consume(TokenType.DOT);
			ident.append((IdentifierToken) consume(TokenType.IDENT));
		}

		if (peek(TokenType.BRACKET_OPEN)) {

			consume(TokenType.BRACKET_OPEN);
			ExprNode expr = parseExpression();
			consume(TokenType.BRACKET_CLOSE);

			return new ArrayAccessNode(ident, expr);

		} else if ((peek(TokenType.HASH) && peek(1, TokenType.PAREN_OPEN)) || peek(TokenType.PAREN_OPEN)) {

			return parseFunCall(ident);

		} else {

			FieldAccessNode fieldAccessNode = new FieldAccessNode(ident);

			if (peek(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {
				return new UnaryOpNode(consume().getType(), fieldAccessNode, false);
			} else {
				return fieldAccessNode;
			}

		}
		// throw new ParserException("Unexpected token: "+peek());
	}

	private ExprNode parseFunCall(IdentifierLitNode ident) throws ParserException {
		boolean preset = peek(TokenType.HASH);
		if (preset)
			consume(TokenType.HASH);
		consume(TokenType.PAREN_OPEN);

		FunCallNode call = new FunCallNode(ident, preset);

		int index = 0;
		while (!peek(TokenType.PAREN_CLOSE)) {
			ExprNode expr = parseExpression();
			call.addParam(expr);

			if (peek(TokenType.COMMA))
				consume(TokenType.COMMA);
		}

		consume(TokenType.PAREN_CLOSE);

		if (peek(TokenType.DOT)) {
			consume(TokenType.DOT);
			call.add(parseIdent());
		}

		return call;
	}

	private ExprNode parseLetSet(Node var) throws ParserException {
		TokenType type = consume(TokenType.ASSIGN).getType();

		ExprNode expr = parseExpression();

		switch (type) {
		case PLUS_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.PLUS, (RecursiveArithmeticOp) expr);
			break;
		case MINUS_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.MINUS, (RecursiveArithmeticOp) expr);
			break;
		case MUL_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.MUL, (RecursiveArithmeticOp) expr);
			break;
		case DIV_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.DIV, (RecursiveArithmeticOp) expr);
			break;
		case MODULO_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.MODULO, (RecursiveArithmeticOp) expr);
			break;
		case BIT_AND_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.BIT_AND, (RecursiveArithmeticOp) expr);
			break;
		case BIT_NOT_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.BIT_NOT, (RecursiveArithmeticOp) expr);
			break;
		case BIT_XOR_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.BIT_XOR, (RecursiveArithmeticOp) expr);
			break;
		case BIT_OR_ASSIGN:
			expr = new BinaryOpNode((RecursiveArithmeticOp) var, TokenType.BIT_OR, (RecursiveArithmeticOp) expr);
			break;
		default:
			break;
		}

		return new LetTypeSetNode(var, expr);
	}

	private boolean hasNext() {
		return index < input.size();
	}

	private boolean hasNext(int x) {
		return index + x < input.size();
	}

	private Token consume() throws ParserException {
		return consume(1);
	}

	private Token consume(int i) throws ParserException {
		end();
		Token c = input.get(index);
		index += i;
		return c;
	}

	private Token consume(TokenType t) throws ParserException {
		if (!hasNext())
			throw new ParserException("Expected %s but got end of input.", t);

		if (peek(t)) {
			return consume();
		} else {
			throw new ParserException(peek(), t);
		}
	}

	private Token consume(TokenType... types) throws ParserException {
		Token peek = peek();
		if (Arrays.stream(types).filter(peek.getType()::softEquals).collect(Collectors.counting()) > 0)
			return consume();
		else
			throw new ParserException(peek, types);
	}

	private void end() throws ParserException {
		if (!hasNext())
			throw new ParserException("Unexpected end of input.");
	}

	private Token peek() {
		return peek(0);
	}

	private Token peek(int i) {
		return input.get(index + i);
	}

	private boolean peek(TokenType type) {
		if (!hasNext())
			return false;
		return peek().getType().softEquals(type);
	}

	private boolean peek(int x, TokenType type) {
		if (!hasNext(x))
			return false;
		return peek(x).getType().softEquals(type);
	}

	private boolean peek(TokenType... types) {
		if (!hasNext())
			return false;
		TokenType peek = peek().getType();
		return Arrays.stream(types).map(peek::softEquals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	private boolean peek(int x, TokenType... types) {
		TokenType peek = peek(x).getType();
		return Arrays.stream(types).map(peek::softEquals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	public RuntimeNode getRoot() {
		return root;
	}

}
