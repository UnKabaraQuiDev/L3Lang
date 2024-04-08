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
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.FunDefArgsNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
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

	private void parseLineExpr(ScopeContainerNode container) throws ParserException {
		if (peek(TokenType.LET)) { // var declaration
			LetTypeDefNode ltdn = parseVarDefExpr();
			if (container.containsDescriptor(ltdn.getIdent().getIdentifier())) {
				throw new ParserException("let " + ltdn.getIdent().getIdentifier() + " already defined: " + ltdn.getIdent().getLine() + ":" + ltdn.getIdent().getColumn());
			}
			container.addDescriptor(ltdn.getIdent().getIdentifier(), new LetScopeDescriptor(ltdn.getIdent(), ltdn));
			container.add(ltdn);
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(TokenType.PAREN_OPEN))) { // function call
			container.add(parseFunCall());
			consume(TokenType.SEMICOLON);
		} else if (canParseFun()) {
			FunDefNode ltdn = parseFunDefExpr();
			if (container.containsDescriptor(ltdn.getIdent().getIdentifier())) {
				throw new ParserException("fun " + ltdn.getIdent().getIdentifier() + " already defined: " + ltdn.getIdent().getLine() + ":" + ltdn.getIdent().getColumn());
			}
			container.addDescriptor(ltdn.getIdent().getIdentifier(), new FunScopeDescriptor(ltdn.getIdent()));
			container.add(ltdn);
		} else {
			throw new ParserException("Expression not implemented: " + peek() + "->" + peek(1) + "->" + peek(2));
		}
	}

	private boolean canParseFun() {
		return peek(TokenType.FUN) && peek(1, TokenType.TYPE, TokenType.IDENT, TokenType.VOID) && peek(2, TokenType.IDENT) && (peek(3, TokenType.PAREN_OPEN, TokenType.HASH) || peek(2, TokenType.PAREN_OPEN));
	}

	private boolean canParseGenericTypeFun() {
		return peek(TokenType.FUN) && peek(1, TokenType.TYPE, TokenType.VOID) && peek(2, TokenType.IDENT) && (peek(3, TokenType.PAREN_OPEN, TokenType.HASH) || peek(2, TokenType.PAREN_OPEN));
	}

	private FunDefNode parseFunDefExpr() throws ParserException {
		if (canParseGenericTypeFun()) {
			// generic return type
			Token fun = consume(TokenType.FUN);
			Token returnType = consume(TokenType.TYPE, TokenType.VOID);
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);
			boolean preset = peek(TokenType.HASH);
			if (preset) {
				consume(TokenType.HASH);
			}

			FunDefNode fdn = new FunDefNode(returnType, ident);

			consume(TokenType.PAREN_OPEN);
			FunDefArgsNode argsNode = new FunDefArgsNode();
			List<Node> nodes = parseFunArgsDef();
			nodes.forEach(argsNode::add);
			nodes.forEach(node -> {
				if (node instanceof LetTypeDefNode) {
					fdn.addDescriptor(((LetTypeDefNode) node).getIdent().getIdentifier(), new LetScopeDescriptor(((LetTypeDefNode) node).getIdent(), (LetTypeDefNode) node));
				}
			});
			consume(TokenType.PAREN_CLOSE);

			consume(TokenType.CURLY_OPEN);
			while (!peek(TokenType.CURLY_CLOSE)) {
				parseLineExpr(fdn);
			}
			consume(TokenType.CURLY_CLOSE);

			return fdn;
		} else {
			assert false : "Defined typed not defined yet.";
		}
		throw new ParserException("Undefined Fun def");
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
			parseFunArgsValues().forEach(fcn::add);

			Token parenClosed = consume(TokenType.PAREN_CLOSE);

			return fcn;
		} else {
			throw new ParserException("Error: not a complete function call");
		}
	}

	private List<Node> parseFunArgsDef() throws ParserException {
		List<Node> ln = new LinkedList<Node>();
		while (!peek(TokenType.PAREN_CLOSE)) {
			ln.add(parseVarDefExpr());
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}
		return ln;
	}

	private List<Node> parseFunArgsValues() throws ParserException {
		GlobalLogger.log();
		System.err.println(peek());
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
		} else {
			Node expr = parseExpression();
			return new FunArgNumLitValueNode(expr);
		}
	}

	private boolean canParseExpr() {
		return (peek(TokenType.MINUS) && peek(1, TokenType.NUM_LIT)) || peek(TokenType.NUM_LIT) || peek(TokenType.PAREN_OPEN) || peek(TokenType.IDENT);
	}

	private LetTypeDefNode parseVarDefExpr() throws ParserException {
		System.err.println(peek() + " -> " + peek(1));
		if ((peek(TokenType.LET) && (peek(1, TokenType.STATIC, TokenType.TYPE)) || peek(1, TokenType.TYPE))) {
			// generic type
			Token let = consume(TokenType.LET);
			boolean iStatic = peek(TokenType.STATIC);
			if (iStatic) {
				consume(TokenType.STATIC);
			}
			Token type = consume(TokenType.TYPE);
			Token ident = consume(TokenType.IDENT);

			boolean iArray = peek(TokenType.BRACKET_OPEN) && peek(1, TokenType.NUM_LIT) && peek(2, TokenType.BRACKET_CLOSE);
			int arraySize = 0;
			if (iArray) {
				consume(TokenType.BRACKET_OPEN);

				arraySize = (int) ((NumericLiteralToken) consume(TokenType.NUM_LIT)).getValue();

				consume(TokenType.BRACKET_CLOSE);
			}

			LetTypeDefNode typeDefNode = new LetTypeDefNode(type, (IdentifierToken) ident, iStatic, iArray, arraySize);

			if (!peek(TokenType.ASSIGN))
				return typeDefNode;

			Token assign = consume(TokenType.ASSIGN);

			if (!iArray) {
				typeDefNode.add(parseExpression());
			} else {
				if (peek(TokenType.CURLY_OPEN)) {
					consume(TokenType.CURLY_OPEN);
					parseArrayArgs().forEach(typeDefNode::add);
					consume(TokenType.CURLY_CLOSE);
				}
			}

			return typeDefNode;
		} else if (peek(TokenType.LET) && peek(TokenType.IDENT)) {
			assert false : "Defined typed not defined yet.";
		}
		throw new ParserException("Undefined Var def");
	}

	private List<Node> parseArrayArgs() throws ParserException {
		List<Node> ln = new LinkedList<Node>();
		while (!peek(TokenType.CURLY_CLOSE)) {
			ln.add(parseArrayArgsValue());
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}
		return ln;
	}

	private Node parseArrayArgsValue() throws ParserException {
		if (peek(TokenType.IDENT) && peek(1, TokenType.PAREN_OPEN)) {
			return parseFunCall();
		} else {
			return new NumLitNode(parseExpression());
		}
	}

	private Node simplifyExpression(Node node) {
		if (node instanceof BinaryOpNode) {
			BinaryOpNode binaryOpNode = (BinaryOpNode) node;
			Node left = simplifyExpression(binaryOpNode.getLeft());
			Node right = simplifyExpression(binaryOpNode.getRight());
			if (left instanceof NumLitNode && right instanceof NumLitNode) {
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
		case MODULO:
			return left % right;
		default:
			throw new IllegalArgumentException("Unsupported operator: " + operator);
		}
	}

	private Node parseExpression() throws ParserException {
		Node left = parseTerm();

		while (peek(TokenType.PLUS, TokenType.MINUS)) {
			TokenType operator = consume(TokenType.PLUS, TokenType.MINUS).getType();
			Node right = parseTerm();
			left = new BinaryOpNode(left, operator, right);
		}

		return left;
	}

	private Node parseTerm() throws ParserException {
		if (canParseExpr()) {
			Node left = parseFactor();

			while (peek(TokenType.MUL, TokenType.DIV, TokenType.MODULO)) {
				TokenType operator = consume(TokenType.MUL, TokenType.DIV, TokenType.MODULO).getType();
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
		if (ValueType.DECIMAL.equals(nlt.getValueType())) {
			return new NumLitNode(Double.valueOf(nlt.getValue().doubleValue() * (negative ? -1 : 1)));
		} else {
			return new NumLitNode(Long.valueOf(nlt.getValue().longValue() * (negative ? -1 : 1)));
		}
	}

	private boolean parseSign() throws ParserException {
		boolean negative = false;
		while (peek(TokenType.MINUS)) {
			consume(TokenType.MINUS);
			negative = !negative;
		}
		return negative;
	}

	private Node parseVar() throws ParserException {
		boolean negative = parseSign();
		if (negative) {
			return new BinaryOpNode(new NumLitNode(0), TokenType.MINUS, new VarNumNode((IdentifierToken) consume(TokenType.IDENT)));
		} else {
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
