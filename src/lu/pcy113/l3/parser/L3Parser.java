package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.ArrayInitNode;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.ElseDefNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsDefNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.IfDefNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.LocalizingNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.TypeNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
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

	private void parseLineExpr(Node parent) throws ParserException {
		ScopeContainer container = parent.getClosestContainer();

		if (peek(TokenType.LET)) { // var declaration
			LetTypeDefNode ltdn = parseLetTypeDef(parent);
			System.err.println("container = "+container+" for = "+parent);
			if (container.containsDescriptor(ltdn.getIdent().getValue())) {
				throw new ParserException("let " + ltdn.getIdent().getValue() + " already defined: " + ltdn.getIdent().getLine() + ":" + ltdn.getIdent().getColumn());
			}
			parent.add(ltdn);
			container.addDescriptor(ltdn.getIdent().getValue(), new LetScopeDescriptor(ltdn.getIdent(), ltdn));
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(TokenType.PAREN_OPEN))) { // function call
			parent.add(parseFunCall());
			consume(TokenType.SEMICOLON);
		} else if (canParseFun()) {
			FunDefNode ltdn = parseFunDefExpr(parent);
			if (container.containsDescriptor(ltdn.getIdent().getValue())) {
				throw new ParserException("fun " + ltdn.getIdent().getValue() + " already defined: " + ltdn.getIdent().getLine() + ":" + ltdn.getIdent().getColumn());
			}
			container.addDescriptor(ltdn.getIdent().getValue(), new FunScopeDescriptor(ltdn.getIdent(), ltdn));
		} else if (peek(TokenType.RETURN)) {
			ReturnNode rtn = parseReturnExpr();
			parent.add(rtn);
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.COMMENT)) {
			consume(TokenType.COMMENT); // ignore
		} else if (peek(TokenType.IDENT) && (peek(1, TokenType.ASSIGN, TokenType.BRACKET_OPEN)) || (peek(TokenType.BIT_AND) && peek(1, TokenType.IDENT))) {
			LetTypeSetNode set = parseLetTypeSet();
			if (!container.containsDescriptor(set.getLet().getIdent().getValue())) {
				throw new ParserException("let " + set.getLet().getIdent().getValue() + " isn't defined: (" + set.getLet().getIdent().getLine() + ":" + set.getLet().getIdent().getColumn() + ")");
			}
			parent.add(set);
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.SEMICOLON)) {
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.IF)) {
			IfContainerNode ifdef = parseIfContainerExpr();
			parent.add(ifdef);
		} else {
			throw new ParserException("Expression not implemented: (" + parent.getClass().getSimpleName() + ") " + peek() + "->" + peek(1) + "->" + peek(2));
		}
	}

	private IfContainerNode parseIfContainerExpr() throws ParserException {
		if (peek(TokenType.IF) && peek(1, TokenType.PAREN_OPEN)) {
			IfContainerNode container = new IfContainerNode();

			IfDefNode ifDef = parseIfDefExpr();
			container.add(ifDef);
			
			while(peek(TokenType.ELSE)) {
				if(peek(1, TokenType.IF)) {
					consume(TokenType.ELSE);
					IfDefNode ifDef2 = parseIfDefExpr();
					container.add(ifDef2);
				}else {
					ElseDefNode elseDef = parseElseDefExpr();
					container.add(elseDef);
				}
			}
			
			return container;
		}
		throw new ParserException("An error occured when parsing if-statements");
	}

	private ElseDefNode parseElseDefExpr() throws ParserException {
		if (peek(TokenType.ELSE)) {
			Token elseToken = consume(TokenType.ELSE);

			ElseDefNode elseDef = new ElseDefNode(elseToken);

			ScopeBodyNode body = parseIfDefBody(elseDef);

			return elseDef;
		}
		throw new ParserException("An error occured when parsing else-statement");
	}

	private IfDefNode parseIfDefExpr() throws ParserException {
		if (peek(TokenType.IF) && peek(1, TokenType.PAREN_OPEN)) {
			Token ifToken = consume(TokenType.IF);
			consume(TokenType.PAREN_OPEN);

			Node condition = parseExpression();

			consume(TokenType.PAREN_CLOSE);

			IfDefNode ifDef = new IfDefNode(ifToken, condition);

			ScopeBodyNode body = parseIfDefBody(ifDef);

			return ifDef;
		}
		throw new ParserException("An error occured when parsing if-statement");
	}

	private ScopeBodyNode parseIfDefBody(Node fdn) throws ParserException {
		ScopeBodyNode fdb = new ScopeBodyNode();
		fdn.add(fdb);

		if(peek(TokenType.CURLY_OPEN)) {
			consume(TokenType.CURLY_OPEN);
			while (!peek(TokenType.CURLY_CLOSE)) {
				parseLineExpr(fdb);
			}
	
			consume(TokenType.CURLY_CLOSE);
		}else {
			parseLineExpr(fdb);
		}

		return fdb;
	}

	private LetTypeSetNode parseLetTypeSet() throws ParserException {
		if ((peek(TokenType.IDENT) && (peek(1, TokenType.ASSIGN, TokenType.BRACKET_OPEN)))) {
			// generic type
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

			boolean array = peek(TokenType.BRACKET_OPEN);
			Node arrayIndex = null;
			Node val = null;
			if (array) {
				consume(TokenType.BRACKET_OPEN);
				arrayIndex = parseExpression();
				consume(TokenType.BRACKET_CLOSE);

				val = new VarNumNode(ident, arrayIndex);
			} else {
				val = new VarNumNode(ident);
			}

			Token assign = consume(TokenType.ASSIGN);

			Node newValue = parseExpression();
			// TODO array parsing

			LetTypeSetNode typeDefNode = new LetTypeSetNode(val, newValue);

			return typeDefNode;
		} else if (peek(TokenType.BIT_AND) && peek(1, TokenType.IDENT)) {
			consume(TokenType.BIT_AND);
			LetTypeSetNode typeSet = parseLetTypeSet();
			typeSet.getLet().add(new NumLitNode(0));
			return typeSet;
		}
		throw new ParserException("Undefined Var def");
	}

	private ReturnNode parseReturnExpr() throws ParserException {
		consume(TokenType.RETURN);

		TypeNode type = parseType();
		if (peek(TokenType.SEMICOLON))
			return new ReturnNode(type, null);

		Node expr = parseExpression();
		return new ReturnNode(type, expr);
	}

	private boolean canParseFun() {
		return peek(TokenType.FUN) && peek(1, TokenType.TYPE, TokenType.IDENT, TokenType.VOID) && peek(2, TokenType.IDENT) && (peek(3, TokenType.PAREN_OPEN, TokenType.HASH) || peek(2, TokenType.PAREN_OPEN));
	}

	private boolean canParseGenericTypeFun() {
		return peek(TokenType.FUN) && peek(1, TokenType.TYPE, TokenType.VOID) && peek(2, TokenType.IDENT) && (peek(3, TokenType.PAREN_OPEN, TokenType.HASH) || peek(4, TokenType.PAREN_OPEN));
	}

	private FunDefNode parseFunDefExpr(Node container) throws ParserException {
		if (canParseGenericTypeFun()) {
			// generic return type
			Token fun = consume(TokenType.FUN);
			TypeNode returnType = parseType();
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);
			boolean preset = peek(TokenType.HASH);
			if (preset) {
				consume(TokenType.HASH);
			}

			FunDefNode fdn = new FunDefNode(returnType, ident);
			container.add(fdn);

			FunArgsDefNode args = parseFunArgsDef(fdn);

			FunBodyDefNode body = parseFunBodyDef(fdn);

			return fdn;
		} else {
			assert false : "Defined typed not defined yet.";
		}
		throw new ParserException("Undefined Fun def");
	}

	private FunArgsDefNode parseFunArgsDef(FunDefNode fdn) throws ParserException {
		consume(TokenType.PAREN_OPEN);
		FunArgsDefNode argsNode = new FunArgsDefNode();
		fdn.add(argsNode);

		// int index = 0;
		while (!peek(TokenType.PAREN_CLOSE)) {
			FunArgDefNode arg = parseFunArgDef();
			LetTypeDefNode let = arg.getLet();
			argsNode.add(arg);
			fdn.addDescriptor(let.getIdent().getValue(), new LetScopeDescriptor(let.getIdent(), let));
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}

		consume(TokenType.PAREN_CLOSE);

		return argsNode;
	}

	private FunBodyDefNode parseFunBodyDef(FunDefNode fdn) throws ParserException {
		FunBodyDefNode fdb = new FunBodyDefNode();
		fdn.add(fdb);

		consume(TokenType.CURLY_OPEN);
		while (!peek(TokenType.CURLY_CLOSE)) {
			parseLineExpr(fdb);
		}

		if (!fdb.getChildren().isEmpty() && !(fdb.getChildren().getLast() instanceof ReturnNode)) {
			if (fdn.getReturnType().isVoid()) {
				fdb.add(new ReturnNode(fdn.getReturnType(), null));
			} else {
				throw new ParserException("Missing final return statement: " + peek());
			}
		}

		consume(TokenType.CURLY_CLOSE);

		return fdb;
	}

	private FunCallNode parseFunCall() throws ParserException {
		if (peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(1, TokenType.PAREN_OPEN))) {
			Token ident = consume();
			boolean preset = peek(TokenType.HASH);
			if (preset) {
				consume(TokenType.HASH);
			}
			Token parenOpen = consume(TokenType.PAREN_OPEN);

			FunCallNode fcn = new FunCallNode((IdentifierToken) ident, preset);
			parseFunArgsVal(fcn);

			Token parenClosed = consume(TokenType.PAREN_CLOSE);

			return fcn;
		} else {
			throw new ParserException("Error: not a complete function call");
		}
	}

	private FunArgsValNode parseFunArgsVal(FunCallNode fcn) throws ParserException {
		GlobalLogger.log();
		FunArgsValNode fargs = new FunArgsValNode();
		fcn.add(fargs);
		int index = 0;
		while (!peek(TokenType.PAREN_CLOSE)) {
			fargs.add(parseFunArgVal(index++));
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}
		return fargs;
	}

	private Node parseFunArgVal(int index) throws ParserException {
		Node exprNode = parseExpression();
		return new FunArgValNode(index, exprNode);
	}

	private boolean canParseExpr() {
		return (peek(TokenType.MINUS) && peek(1, TokenType.NUM_LIT)) || peek(TokenType.NUM_LIT, TokenType.PAREN_OPEN, TokenType.IDENT);
	}

	private FunArgDefNode parseFunArgDef() throws ParserException {
		if ((peek(TokenType.TYPE) && peek(1, TokenType.IDENT)) || (peek(TokenType.TYPE) && peek(1, TokenType.COLON) && peek(2, TokenType.IDENT))) {
			// generic type
			TypeNode typeNode = parseType();
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

			LetTypeDefNode typeDefNode = new LetTypeDefNode(typeNode, (IdentifierToken) ident, false, true); // TODO:
																												// Array

			return new FunArgDefNode(typeDefNode);
		} else if (peek(TokenType.IDENT)) {
			throw new ParserException("Defined typed not defined yet.");
		}
		throw new ParserException("Undefined Var def");
	}

	private TypeNode parseType() throws ParserException {
		if (peek(TokenType.IDENT)) {
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);
			boolean pointer = peek(TokenType.COLON);
			if (pointer)
				consume(TokenType.COLON);
			return new TypeNode(false, ident, pointer);

		} else if (peek(TokenType.TYPE)) {
			Token type = consume(TokenType.TYPE);
			boolean pointer = peek(TokenType.COLON);
			if (pointer)
				consume(TokenType.COLON);
			return new TypeNode(true, type, pointer);

		} else if (peek(TokenType.VOID)) {
			return new TypeNode(true, consume(TokenType.VOID));

		} else {
			throw new ParserException("Unsupported type: " + peek());

		}
	}

	private LetTypeDefNode parseLetTypeDef(Node parent) throws ParserException {
		ScopeContainer container = parent.getClosestContainer();

		if ((peek(TokenType.LET) && (peek(1, TokenType.STATIC, TokenType.TYPE)) || peek(1, TokenType.TYPE))) { // generic
																												// type
			Token let = consume(TokenType.LET);
			boolean iStatic = peek(TokenType.STATIC);
			if (iStatic) {
				consume(TokenType.STATIC);
			}
			TypeNode type = parseType();

			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

			// int nonStaticLetIndex = getLetIndex(container);

			LetTypeDefNode typeDefNode = new LetTypeDefNode(type, ident, iStatic, false);

			if (!peek(TokenType.ASSIGN))
				return typeDefNode;

			Token assign = consume(TokenType.ASSIGN);

			if (type.isPointer() && peek(TokenType.CURLY_OPEN)) {
				if (peek(TokenType.CURLY_OPEN)) {
					consume(TokenType.CURLY_OPEN);
					parseArrayArgs().forEach(typeDefNode::add);
					consume(TokenType.CURLY_CLOSE);
				}
				// typeDefNode.setLetIndex(typeDefNode.getLetIndex() /*+
				// typeDefNode.getChildren().size()*/);
			} else {
				typeDefNode.add(parseExpression());
				if (typeDefNode.getExpr() instanceof ArrayInitNode) {
					// TODO -1 really needed ? v
					// typeDefNode.setLetIndex(typeDefNode.getLetIndex() - 1 + ((ArrayInitNode)
					// typeDefNode.getExpr()).getArraySize());
				} else if (typeDefNode.getExpr() instanceof StringLitNode) {
					// typeDefNode.setLetIndex(typeDefNode.getLetIndex() - 1 + ((StringLitNode)
					// typeDefNode.getExpr()).getArraySize());
				}
			}

			return typeDefNode;
		} else if (peek(TokenType.LET) && peek(TokenType.IDENT)) {
			throw new ParserException("Defined typed not defined yet.");
		}
		throw new ParserException("Undefined Var def");
	}

	private int getLetIndex(ScopeContainer container) {
		return (int) (long) container.getDescriptors().values().stream().map((ScopeDescriptor i) -> {
			if (i instanceof LetScopeDescriptor && ((LetScopeDescriptor) i).getNode() instanceof LetTypeDefNode) {
				LetScopeDescriptor letDesc = (LetScopeDescriptor) i;
				LetTypeDefNode letNode = (LetTypeDefNode) letDesc.getNode();
				return letNode.isiStatic() ? 0 : 1;
			}
			return 0;
		}).reduce(0, (a, b) -> a + b);
	}

	private List<Node> parseArrayArgs() throws ParserException { // TODO
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
			return parseExpression();
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
		if (canParseArrayInit()) {
			return parseArrayInit();
		}

		if (peek(TokenType.COLON)) { // create pointer to var
			consume(TokenType.COLON);
			Node varNode = parseExpression();
			if (varNode instanceof VarNumNode) {
				return new LocalizingNode((VarNumNode) varNode);
			} else {
				throw new ParserException("Can only localize vars");
			}
		} else if (peek(TokenType.BIT_AND)) { // value from pointer
			consume(TokenType.BIT_AND);
			Node identParent = parseTerm();
			Node identNode = identParent;
			if (identNode instanceof BinaryOpNode) {
				identNode = ((BinaryOpNode) identParent).getLeft();
			}
			if (identNode instanceof VarNumNode) {
				identNode.add(new NumLitNode(0L)); // set as pointer
				return identParent;
			} else {
				throw new ParserException("Cannot treat lit/... as pointer");
			}
		}

		if (peek(TokenType.STRING)) {
			return new StringLitNode((StringLiteralToken) consume(TokenType.STRING));
		}

		Node left = parseTerm();

		while (peek(TokenType.PLUS, TokenType.MINUS)) {
			TokenType operator = consume(TokenType.PLUS, TokenType.MINUS).getType();
			Node right = parseTerm();
			left = new BinaryOpNode(left, operator, right);
		}

		return left;
	}

	private ArrayInitNode parseArrayInit() throws ParserException {
		consume(TokenType.NEW);
		TypeNode type = parseType();
		consume(TokenType.BRACKET_OPEN);
		int arraySize = (int) (long) ((NumericLiteralToken) consume(TokenType.NUM_LIT)).getValue();
		consume(TokenType.BRACKET_CLOSE);
		return new ArrayInitNode(type, arraySize);
	}

	private boolean canParseArrayInit() {
		return peek(TokenType.NEW) && peek(1, TokenType.TYPE, TokenType.IDENT) && peek(2, TokenType.BRACKET_OPEN) && peek(3, TokenType.NUM_LIT) && peek(4, TokenType.BRACKET_CLOSE);
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
		} else if (peek(TokenType.BIT_AND) && peek(1, TokenType.IDENT)) {
			consume(TokenType.BIT_AND);
			Node identNode = parseTerm();
			System.out.println(identNode.getClass().getName());
			if (identNode instanceof VarNumNode) {
				identNode.add(new NumLitNode(0L)); // set as pointer
			} else {
				throw new ParserException("Cannot treat lit/... as pointer");
			}
			return identNode;
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
		boolean array = peek(1, TokenType.BRACKET_OPEN);
		Node arrayIndex = null;
		Node val = null;
		if (array) {
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

			consume(TokenType.BRACKET_OPEN);
			arrayIndex = parseExpression();
			consume(TokenType.BRACKET_CLOSE);

			val = new VarNumNode(ident, arrayIndex);
		} else {
			val = new VarNumNode((IdentifierToken) consume(TokenType.IDENT));
		}
		if (negative) {
			return new BinaryOpNode(new NumLitNode(0), TokenType.MINUS, val);
		} else {
			return val;
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
