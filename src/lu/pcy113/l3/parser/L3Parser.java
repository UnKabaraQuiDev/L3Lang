package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgNumLitValueNode;
import lu.pcy113.l3.parser.ast.FunArgVarValueNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.RuntimeNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.pclib.GlobalLogger;

public class L3Parser {

	private int index;
	private final List<Token> input;

	private RuntimeNode root;

	public L3Parser(L3Lexer lexer) {
		this(lexer.getTokens());
	}

	public L3Parser(List<Token> tokens) {
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

	private void parseLineExpr(Node container) throws ParserException {
		if (peek(TokenType.LET)) { // var declaration
			container.add(parseVarDefExpr());
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.IDENT) && peek(1, TokenType.PAREN_OPEN, TokenType.HASH)) { // function call
			container.add(parseFunCall());
			consume(TokenType.SEMICOLON);
		} else {
			throw new ParserException("Expression not implemented: " + peek() + "->" + peek(1) + "->" + peek(2));
		}
	}

	private Node parseFunCall() throws ParserException {
		if (peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(1, TokenType.PAREN_OPEN))) {
			Token ident = consume();
			boolean preset = peek(TokenType.HASH);
			if (preset) {
				consume(TokenType.HASH);
			}
			Token parenOpen = consume(TokenType.PAREN_OPEN);

			FunCallNode fcn = new FunCallNode((IdentifierToken) ident, preset);
			parseFunArgs().forEach(fcn::add);

			Token parenClosed = consume(TokenType.PAREN_CLOSE);

			return fcn;
		} else {
			throw new ParserException("Error: not a complete function call");
		}
	}

	private List<Node> parseFunArgs() throws ParserException {
		List<Node> ln = new LinkedList<Node>();
		while (!peek(TokenType.PAREN_CLOSE)) {
			ln.add(parseFunArgsValue());
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}
		return ln;
	}

	private Node parseFunArgsValue() throws ParserException {
		if (peek(TokenType.IDENT) && peek(1, TokenType.PAREN_OPEN)) {
			return parseFunCall();
		} else if (peek(TokenType.IDENT)) {
			return new FunArgVarValueNode(consume(TokenType.IDENT));
		} else if ((peek(TokenType.MINUS) && peek(1, TokenType.NUM_LIT)) || peek(TokenType.NUM_LIT) || peek(TokenType.PAREN_OPEN)) {
			return new FunArgNumLitValueNode(parseExpression());
		}
		throw new ParserException("We don't know what happened, but there was some kind of error");
	}

	private Node parseVarDefExpr() throws ParserException {
		if (peek(TokenType.LET) && (peek(1, TokenType.STATIC, TokenType.TYPE) || peek(1, TokenType.TYPE))) {
			// generic type
			Token let = consume(TokenType.LET);
			boolean iStatic = peek(TokenType.STATIC);
			if (iStatic) {
				consume(TokenType.STATIC);
			}
			Token type = consume(TokenType.TYPE);
			Token ident = consume(TokenType.IDENT);
			Token assign = consume(TokenType.ASSIGN);

			Node expr = parseExpression();
			expr = simplifyExpression((BinaryOpNode) expr);

			return new LetTypeDefNode(type, (IdentifierToken) ident, expr, iStatic);
		} else if (peek(TokenType.LET) && peek(TokenType.IDENT)) {
			assert false : "Defined typed not defined yet.";
		}
		throw new ParserException("Undefined Var def");
	}

	private Node simplifyExpression(Node node) {
		if (node instanceof BinaryOpNode) {
			BinaryOpNode binaryOpNode = (BinaryOpNode) node;
			Node left = simplifyExpression(binaryOpNode.getLeft());
			Node right = simplifyExpression(binaryOpNode.getRight());
			if (left instanceof NumLitNode && right instanceof NumLitNode) {
				System.err.println("types: "+((NumLitNode) left).getValue()+" and "+((NumLitNode) right).getValue());
				long result = performOperation((long) ((NumLitNode) left).getValue(), binaryOpNode.getOperator(), (long) ((NumLitNode) right).getValue());
				return new NumLitNode(result);
			} else {
				binaryOpNode.setLeft(left);
				binaryOpNode.setRight(right);
				return binaryOpNode;
			}
		} else {
			// If it's not a BinaryOpNode, continue traversing its children recursively
			return node;
		}
	}

	private long performOperation(long left, TokenType operator, long right) {
		switch (operator) {
		case PLUS:
			return left + right;
		case MINUS:
			return left - right;
		case MUL:
			return left * right;
		case DIV:
			if (right != 0) {
				return left / right;
			} else {
				throw new ArithmeticException("Division by zero");
			}
		default:
			throw new IllegalArgumentException("Unsupported operator: " + operator);
		}
	}

	private Node parseExpression() throws ParserException {
		Node left = parseTerm();

		while (peek(TokenType.PLUS) || peek(TokenType.MINUS)) {
			TokenType operator = consume(TokenType.PLUS, TokenType.MINUS).getType();
			Node right = parseTerm();
			left = new BinaryOpNode(left, operator, right);
		}

		return left;
	}

	private Node parseTerm() throws ParserException {
		if ((peek(TokenType.MINUS) && peek(1, TokenType.NUM_LIT)) || peek(TokenType.NUM_LIT) || peek(TokenType.PAREN_OPEN)) {
			Node left = parseFactor();

			while (peek(TokenType.MUL) || peek(TokenType.DIV)) {
				TokenType operator = consume(TokenType.MUL, TokenType.DIV).getType();
				Node right = parseFactor();
				left = new BinaryOpNode(left, operator, right);
			}

			return left;
		} else if (peek(TokenType.IDENT) && peek(TokenType.PAREN_OPEN)) {
			return parseFunCall();
		} else if (peek(TokenType.IDENT)) {
			return parseVar();
		}
		return new NumLitNode(0L); // return 0 to make the value negative
	}

	private Node parseFactor() throws ParserException {
		if ((peek(TokenType.MINUS) && peek(1, TokenType.NUM_LIT)) || peek(TokenType.NUM_LIT)) {
			return parseNumLit();
		} else if (peek(TokenType.PAREN_OPEN)) {
			consume(TokenType.PAREN_OPEN);
			Node expr = parseExpression();
			consume(TokenType.PAREN_CLOSE);
			return expr;
		} else if ((peek(TokenType.IDENT) && peek(1, TokenType.PAREN_OPEN)) || (peek(TokenType.MINUS) && peek(1, TokenType.IDENT) && peek(2, TokenType.PAREN_OPEN))) {
			return parseFunCall();
		} else if (peek(TokenType.IDENT) || (peek(TokenType.MINUS) && peek(1, TokenType.IDENT))) {
			return parseVar();
		} else {
			throw new ParserException("Expected expression");
		}
	}

	private Node parseNumLit() throws ParserException {
		boolean negative = parseSign();
		NumericLiteralToken nlt = (NumericLiteralToken) consume(TokenType.NUM_LIT);
		if(ValueType.DECIMAL.equals(nlt.getValueType())) {
			return new NumLitNode(Double.valueOf(nlt.getValue().doubleValue()*(negative ? -1 : 1)));
		}else {
			return new NumLitNode(Long.valueOf(nlt.getValue().longValue()*(negative ? -1 : 1)));
		}
	}

	private boolean parseSign() throws ParserException {
		boolean negative = false;
		while(peek(TokenType.MINUS)) {
			consume(TokenType.MINUS);
			negative = !negative;
		}
		return negative;
	}

	private Node parseVar() throws ParserException {
		boolean negative = parseSign();
		if(negative) {
			return new BinaryOpNode(new NumLitNode(0), TokenType.MINUS, new VarNumNode((IdentifierToken) consume(TokenType.IDENT)));
		}else {
			return new VarNumNode((IdentifierToken) consume(TokenType.IDENT));
		}
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
		GlobalLogger.log();
		GlobalLogger.log("We are at: " + peek());

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
		if (Arrays.stream(types).filter(peek.getType()::equals).collect(Collectors.counting()) > 0)
			return consume();
		else
			throw new ParserException(peek, types);
	}

	private void end() throws ParserException {
		if (!hasNext())
			throw new ParserException("Unexpected end of input.");
	}

	private Token peek() {
		// GlobalLogger.log();
		// GlobalLogger.log("trying peek: "+peek(0));
		return peek(0);
	}

	private Token peek(int i) {
		return input.get(index + i);
	}

	private boolean peek(TokenType type) {
		return peek().getType().softEquals(type);
	}

	private boolean peek(int x, TokenType type) {
		return peek(x).getType().softEquals(type);
	}

	private boolean peek(TokenType... types) {
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
