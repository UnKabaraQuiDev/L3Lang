package lu.pcy113.l3.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.ArrayAccessNode;
import lu.pcy113.l3.parser.ast.ArrayAllocNode;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.FunCallParamsNode;
import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.FunDefParamsNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetRefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.PointerDerefNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.expr.UnaryOpNode;
import lu.pcy113.l3.parser.ast.lit.DecimalNumLitNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.lit.IntegerNumLitNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.l3.parser.ast.type.VoidTypeNode;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.l3.utils.L3Utils;
import lu.pcy113.pclib.GlobalLogger;

public class L3Parser {

	private int index;
	private final List<Token> input;

	private FileNode root;

	public L3Parser(String source, L3Lexer lexer) throws ParserException {
		input = lexer.getTokens();

		if (input == null || input.isEmpty())
			throw new ParserException("Tokens cannot be null or empty.");

		this.root = new FileNode(source.substring(0, source.lastIndexOf(".l3")).replace('/', '.'));
	}

	public void parse() throws ParserException {
		if (hasNext()) {
			parsePackageDefExpr(root);
		}

		while (hasNext()) {

			parseLineExpr(root);

		}
	}

	private void parseLineExpr(ScopeContainerNode parent) throws ParserException {
		if (peek(TokenType.LET)) {
			parseStaticLetDef(parent);
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.FUN)) {
			parseFunDef(parent);
		} else {
			implement(peek().getType());
		}
	}

	private void parseFunLineExpr(FunDefNode fun, FunBodyDefNode body, ScopeContainerNode parent) throws ParserException {
		if (peek(TokenType.LET)) {
			parseLetDef(parent);
			consume(TokenType.SEMICOLON);
		} else if (peek(TokenType.RETURN)) {
			parseReturn(fun, parent);
		} else {
			implement(peek().getType());
		}
	}

	private void parseReturn(FunDefNode fun, ScopeContainerNode parent) throws ParserException {
		consume(TokenType.RETURN);

		if (fun.getReturnType() instanceof VoidTypeNode) {
			parent.add(new ReturnNode());
			consume(TokenType.SEMICOLON);
		} else {
			parent.add(new ReturnNode(parseExpression()));
			consume(TokenType.SEMICOLON);
		}
	}

	private FunDefNode parseFunDef(ScopeContainerNode parent) throws ParserException {
		consume(TokenType.FUN);

		TypeNode type = parseType();

		IdentifierLitNode ident = parseSimpleIdentLit();

		consume(TokenType.PAREN_OPEN);

		FunDefNode funDef = new FunDefNode(type, ident, parseFunParamsDef());

		consume(TokenType.PAREN_CLOSE);

		if (peek(TokenType.SEMICOLON)) {
			consume(TokenType.SEMICOLON);
		} else {
			consume(TokenType.CURLY_OPEN);

			funDef.add(parseFunBody(funDef));

			consume(TokenType.CURLY_CLOSE);
		}

		parent.add(funDef);
		if (parent.localContainsDescriptor(ident.getFirst().getValue()) && parent.getLocalDescriptor(ident.getFirst().getValue()) instanceof FunScopeDescriptor) {
			throw new ParserException("FunDef '" + ident.getFirst().getValue() + "' already declared in this scope (" + ident.getFirst().getPosition() + ").");
		}
		parent.addDescriptor(ident.asString(), new FunScopeDescriptor(ident, funDef));

		return funDef;
	}

	private FunBodyDefNode parseFunBody(FunDefNode fun) throws ParserException {
		FunBodyDefNode body = new FunBodyDefNode();

		while (!peek(TokenType.CURLY_CLOSE)) {

			parseFunLineExpr(fun, body, body);

			if (!hasNext()) {
				throw new ParserException("Unterminated function: " + peek(-1).getPosition());
			}
		}

		return body;
	}

	private FunDefParamsNode parseFunParamsDef() throws ParserException {
		FunDefParamsNode params = new FunDefParamsNode();

		while (!peek(TokenType.PAREN_CLOSE)) {
			params.add(parseFunParamDef());

			if (peek(TokenType.COMMA))
				consume(TokenType.COMMA);
		}

		return params;
	}

	private FunDefParamNode parseFunParamDef() {
		return null;
	}

	private LetDefNode parseStaticLetDef(ScopeContainerNode parent) throws ParserException {

		consume(TokenType.LET);

		if (!peek(TokenType.STATIC)) {
			throw new ParserException("Global LetDef (declared in file-scope) have to be static.");
		}
		
		consume(TokenType.STATIC);

		TypeNode type = parseType();

		IdentifierLitNode ident = parseSimpleIdentLit();

		LetDefNode letDef = new LetDefNode(type, ident, true);

		parent.add(letDef);
		if (parent.localContainsDescriptor(ident.getFirst().getValue()) && parent.getLocalDescriptor(ident.getFirst().getValue()) instanceof LetScopeDescriptor) {
			throw new ParserException("LetDef '" + ident.getFirst().getValue() + "' already declared in this scope (" + ident.getFirst().getPosition() + ").");
		}
		parent.addDescriptor(ident.asString(), new LetScopeDescriptor(ident, letDef));

		if (peek(TokenType.STRICT_ASSIGN)) {
			consume(TokenType.STRICT_ASSIGN);
			letDef.add(parseExpression());
		}

		return letDef;

	}

	private LetDefNode parseLetDef(ScopeContainerNode parent) throws ParserException {

		consume(TokenType.LET);

		if (peek(TokenType.STATIC)) {
			throw new ParserException("Only global (declared in file-scope) can be static.");
		}

		TypeNode type = parseType();

		IdentifierLitNode ident = parseSimpleIdentLit();

		LetDefNode letDef = new LetDefNode(type, ident, false);

		parent.add(letDef);
		if (parent.localContainsDescriptor(ident.getFirst().getValue()) && parent.getLocalDescriptor(ident.getFirst().getValue()) instanceof LetScopeDescriptor) {
			throw new ParserException("LetDef '" + ident.getFirst().getValue() + "' already declared in this scope (" + ident.getFirst().getPosition() + ").");
		}
		parent.addDescriptor(ident.asString(), new LetScopeDescriptor(ident, letDef));

		if (peek(TokenType.STRICT_ASSIGN)) {
			consume(TokenType.STRICT_ASSIGN);
			letDef.add(parseExpression());
		}

		return letDef;

	}

	private TypeNode parseType() throws ParserException {
		TypeNode node = null;

		if (peek(TokenType.IDENT)) {
			IdentifierLitNode ident = parseIdentLit();

			node = new UserTypeNode(ident);

		} else if (peek(TokenType.PRIMITIVE_TYPE)) {

			Token type = consume(TokenType.PRIMITIVE_TYPE);
			node = new PrimitiveTypeNode(type.getType());

		} else if (peek(TokenType.VOID)) {

			return new VoidTypeNode();

		} else {

			throw new ParserException("Unsupported type: " + peek());

		}

		while (peek(TokenType.COLON)) {
			node = new PointerTypeNode(node);
			consume(TokenType.COLON);
		}

		return node;
	}

	private void parsePackageDefExpr(FileNode root) throws ParserException {
		if (peek(TokenType.PACKAGE)) {

			PackageDefNode packageDef = new PackageDefNode(consume(TokenType.PACKAGE), parseIdentLit());

			if (!L3Utils.getPackageName(root.getSource()).equals(packageDef.getValue().asString())) {
				throw new ParserException("Package declaration not matching with file path: " + root.getSource() + " (" + FileUtils.removeExtension(root.getSource()) + ") & " + packageDef.getValue().asString());
			}

			root.add(packageDef);

			consume(TokenType.SEMICOLON);
		} else {
			if (!L3Utils.getPackageName(root.getSource()).isEmpty()) {
				throw new ParserException("Package declaration not matching with file path: " + root.getSource() + " (" + FileUtils.removeExtension(root.getSource()) + ") & <empty>");
			}

			warning(root.getSource(), "Usage of empty package is strongly discouraged.");
		}
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

			return parseNumLit();

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

		} else if (peek(TokenType.NEW)) {

			consume(TokenType.NEW);

			TypeNode type = parseType();

			if (peek(TokenType.BRACKET_OPEN)) {
				consume(TokenType.BRACKET_OPEN);

				// array constructor
				ArrayAllocNode node = new ArrayAllocNode(type, parseNumLit());
				consume(TokenType.BRACKET_CLOSE);

				return node;
			}

			consume(TokenType.PAREN_OPEN);

			// regular type constructor
			UserTypeAllocNode node = new UserTypeAllocNode(type);

			parseFunArgs().forEach(node::add);

			consume(TokenType.PAREN_CLOSE);

			return node;

		} else {
			throw new RuntimeException("Unexpected token: " + peek().getType());
		}
	}

	private NumLitNode parseNumLit() throws ParserException {
		NumericLiteralToken numLit = (NumericLiteralToken) consume(TokenType.NUM_LIT);
		if (numLit.isDecimal()) {
			return new DecimalNumLitNode((double) numLit.getValue());
		} else if (numLit.isInteger()) {
			return new IntegerNumLitNode((long) numLit.getValue());
		} else {
			throw new ParserException("Invalid number format.");
		}
	}

	private ExprNode parseIdent() throws ParserException {
		IdentifierLitNode ident = parseIdentLit();

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

	private IdentifierLitNode parseIdentLit() throws ParserException {
		IdentifierLitNode ident = new IdentifierLitNode((IdentifierToken) consume());

		while (peek(0, TokenType.DOT) && peek(1, TokenType.IDENT)) {
			consume(TokenType.DOT);
			ident.append((IdentifierToken) consume(TokenType.IDENT));
		}
		return ident;
	}

	private IdentifierLitNode parseSimpleIdentLit() throws ParserException {
		return new IdentifierLitNode((IdentifierToken) consume(TokenType.IDENT));
	}

	private ExprNode parseFunCall(IdentifierLitNode ident) throws ParserException {
		boolean preset = peek(TokenType.HASH);
		if (preset)
			consume(TokenType.HASH);
		consume(TokenType.PAREN_OPEN);

		FunCallParamsNode params = new FunCallParamsNode();

		parseFunArgs().forEach(params::add);

		FunCallNode call = new FunCallNode(ident, params, preset);

		consume(TokenType.PAREN_CLOSE);

		if (peek(TokenType.DOT)) {
			consume(TokenType.DOT);
			call.add(parseIdent());
		}

		return call;
	}

	private List<ExprNode> parseFunArgs() throws ParserException {
		List<ExprNode> nodes = new ArrayList<ExprNode>();

		while (!peek(TokenType.PAREN_CLOSE)) {
			ExprNode expr = parseExpression();
			nodes.add(expr);

			if (peek(TokenType.COMMA))
				consume(TokenType.COMMA);
		}

		return nodes;
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

		return new LetSetNode(var, expr);
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
			throw new ParserException("Expected %s but got end of input at: " + peek(-1).getPosition() + "->" + peek().getPosition(), t);

		if (peek(t)) {
			return consume();
		} else {
			throw new ParserException(peek(), t);
		}
	}

	private Token consume(TokenType... types) throws ParserException {
		Token peek = peek();
		if (Arrays.stream(types).filter(peek.getType()::matches).collect(Collectors.counting()) > 0)
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
		return peek().getType().matches(type);
	}

	private boolean peek(int x, TokenType type) {
		if (!hasNext(x))
			return false;
		return peek(x).getType().matches(type);
	}

	private boolean peek(TokenType... types) {
		if (!hasNext())
			return false;
		TokenType peek = peek().getType();
		return Arrays.stream(types).map(peek::matches).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	private boolean peek(int x, TokenType... types) {
		TokenType peek = peek(x).getType();
		return Arrays.stream(types).map(peek::matches).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	public FileNode getRoot() {
		return root;
	}

	private void implement() throws ParserException {
		throw new ParserException("not implemented; ");
	}

	private void implement(Object obj) throws ParserException {
		throw new ParserException("not implemented: " + obj.getClass().getName());
	}

	private void implement(Token obj) throws ParserException {
		throw new ParserException("not implemented: " + obj);
	}

	private void implement(TokenType obj) throws ParserException {
		throw new ParserException("not implemented: " + obj);
	}

	public void warning(String pos, String str) {
		GlobalLogger.warning("[" + pos + ") " + str);
	}

}
