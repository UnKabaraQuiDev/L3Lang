package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.ComparisonOpNode;
import lu.pcy113.l3.parser.ast.PointerDerefNode;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.LogicalOpNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.LetRefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
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

	public Node parseExpression() throws ParserException {
		return parseLogical();
	}

	private Node parseLogical() throws ParserException {
		Node left = parseComparison();

		while (peek(TokenType.OR, TokenType.AND, TokenType.XOR)) {
			TokenType op = consume().getType();
			Node right = parseComparison();
			left = new LogicalOpNode(left, op, right);
		}

		return left;
	}

	private Node parseComparison() throws ParserException {
		Node left = parseTerm();

		while (peek(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.LESS, TokenType.LESS_EQUALS, TokenType.GREATER,
				TokenType.GREATER_EQUALS)) {
			TokenType op = consume().getType();
			Node right = parseTerm();
			left = new ComparisonOpNode(left, op, right);
		}

		return left;
	}

	private Node parseTerm() throws ParserException {
		Node left = parseFactor();

		while (peek(TokenType.PLUS, TokenType.MINUS)) {
			TokenType op = consume().getType();
			Node right = parseFactor();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private Node parseFactor() throws ParserException {
		Node left = parsePrimary();

		if (peek(TokenType.ASSIGN)) {
			left = parseLetTypeSet(left);
		}

		while (peek(TokenType.MUL, TokenType.DIV, TokenType.MODULO, TokenType.BIT_XOR, TokenType.BIT_AND,
				TokenType.BIT_OR)) {
			TokenType op = consume().getType();
			Node right = parsePrimary();
			left = new BinaryOpNode((RecursiveArithmeticOp) left, op, (RecursiveArithmeticOp) right);
		}

		return left;
	}

	private Node parsePrimary() throws ParserException {
		if (peek(TokenType.NUM_LIT)) {
			return new NumLitNode(consume());
		} else if (peek(TokenType.IDENT)) {
			return parseIdent();
		} else if (peek(TokenType.PAREN_OPEN)) {
			consume(TokenType.PAREN_OPEN);
			Node expr = parseExpression();
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

	private Node parseIdent() throws ParserException {
		IdentifierToken varIdent = (IdentifierToken) consume();
		Node var = null;
		if (peek(TokenType.BRACKET_OPEN)) {
			consume(TokenType.BRACKET_OPEN);
			Node expr = parseExpression();
			consume(TokenType.BRACKET_CLOSE);
			var = new FieldAccessNode(varIdent, expr);
		} else if ((peek(TokenType.HASH) && peek(1, TokenType.PAREN_OPEN)) || peek(TokenType.PAREN_OPEN)) {
			boolean preset = peek(TokenType.HASH);
			if (preset)
				consume(TokenType.HASH);
			consume(TokenType.PAREN_OPEN);
			FunArgsValNode args = new FunArgsValNode();
			int index = 0;
			while (!peek(TokenType.PAREN_CLOSE)) {
				args.add(new FunArgValNode(index++, parseExpression()));
				if (peek(TokenType.COMMA))
					consume(TokenType.COMMA);
			}
			consume(TokenType.PAREN_CLOSE);
			FunCallNode call = new FunCallNode(varIdent, preset);
			call.add(args);
			return call;
		} else {
			var = new FieldAccessNode(varIdent);
		}
		return var;
	}

	private Node parseLetTypeSet(Node var) throws ParserException {
		consume(TokenType.ASSIGN);
		Node expr = parseExpression();
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
