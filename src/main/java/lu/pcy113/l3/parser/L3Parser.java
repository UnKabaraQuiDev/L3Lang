package lu.pcy113.l3.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.ident.PackageNode;
import lu.pcy113.l3.parser.ast.let.ArgDefNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.let.LetSetNode;
import lu.pcy113.l3.parser.ast.let.MembersAccess;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.math.UnaryExpressionNode;
import lu.pcy113.l3.parser.ast.pointer.PointerDerefNode;
import lu.pcy113.l3.parser.ast.pointer.PointerMemberAccess;
import lu.pcy113.l3.parser.ast.pointer.PointerRefNode;
import lu.pcy113.l3.parser.ast.type.CastNode;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.pclib.PCUtils;

public class L3Parser {

	private final String path;
	private final LexerIterator iterator;

	private FileNode file;

	public L3Parser(LexerIterator iterator, String path) {
		this.iterator = iterator;
		this.path = path;
	}

	public void parse() {
		parseFileNode();
	}

	private void parseFileNode() {
		file = new FileNode(this.path, PCUtils.getFileName(path));

		if (iterator.peek(TokenType.PACKAGE)) {
			final PackageNode packageNode = parsePackage();
			iterator.consume(TokenType.SEMICOLON);
			file.setPackageNode(packageNode);
		}

		while (iterator.hasNext()) {
			file.addChild(parseFileLineExpression());
		}
	}

	private PackageNode parsePackage() {
		iterator.consume(TokenType.PACKAGE);
		return new PackageNode(parseLongIdentifier(TokenType.DOT));
	}

	private Node parseFileLineExpression() {
		if (iterator.peek(TokenType.LET)) {
			final LetDefNode letDef = parseStaticLetDef();
			iterator.consume(TokenType.SEMICOLON);

			file.getSymbols().registerLet(letDef);

			return letDef;

		} else if (iterator.peek(TokenType.FUN)) {
			final FunDefNode funDef = parseFunDef(file);

			file.getSymbols().registerFun(funDef);

			return funDef;

		} else if (iterator.peek(TokenType.IMPORT)) {
			final ImportNode importNode = parseImport();
			iterator.consume(TokenType.SEMICOLON);

			file.getSymbols().registerImport(importNode);

			return importNode;

		} else {
			throw new L3Exception("Unexpected token: " + iterator.peek());
		}
	}

	private Node parseFunLineExpression(FunDefNode funDef) {
		if (iterator.peek(TokenType.LET)) {
			final LetDefNode letDef = parseLetDef();
			iterator.consume(TokenType.SEMICOLON);

			funDef.getSymbols().checkDependencies(letDef.getValue());
			funDef.getSymbols().registerLet(letDef);

			return letDef;

		} else if (iterator.peek(TokenType.RETURN)) {
			final ReturnNode returnNode = parseReturn();
			iterator.consume(TokenType.SEMICOLON);

			if (returnNode.hasExpression()) {
				funDef.getSymbols().checkDependencies(returnNode.getExpression());
			}

			return returnNode;

		} else {
			final Node chained = parseChainedExpression();
			iterator.consume(TokenType.SEMICOLON);
			return chained;
		}
	}

	private ImportNode parseImport() {
		iterator.consume(TokenType.IMPORT);

		final List<IdentifierNode> idents = parseLongIdentifier(TokenType.DOT);

		if (iterator.peek(TokenType.SEMICOLON)) {
			return new ImportNode(idents);
		}

		iterator.consume(TokenType.AS);

		return new ImportNode(idents, parseSimpleIdentifier());
	}

	private List<IdentifierNode> parseLongIdentifier(TokenType separator) {
		final List<IdentifierNode> list = new ArrayList<>();

		list.add(parseSimpleIdentifier());

		while (iterator.peek(separator)) {
			iterator.consume(separator);
			list.add(parseSimpleIdentifier());
		}

		return list;
	}

	private ReturnNode parseReturn() {
		iterator.consume(TokenType.RETURN);

		if (iterator.peek(TokenType.SEMICOLON)) {
			return new ReturnNode();
		}

		return new ReturnNode(parseExpression());
	}

	private Node parseExpression() {
		return parseChainedExpression();
	}

	private FunDefNode parseFunDef(ListNode list) {
		iterator.consume(TokenType.FUN);
		final TypeNode type = parseType();
		final IdentifierNode identifier = parseSimpleIdentifier();

		iterator.consume(TokenType.PAREN_OPEN);
		final List<ArgDefNode> args = parseFunArgsDef();
		iterator.consume(TokenType.PAREN_CLOSE);

		final FunDefNode funDef = new FunDefNode(list, type, identifier, args);
		funDef.getArgs().forEach(v -> funDef.getSymbols().registerLet(v));

		iterator.consume(TokenType.CURLY_OPEN);
		funDef.setChildren(parseBlock(funDef));
		iterator.consume(TokenType.CURLY_CLOSE);

		return funDef;
	}

	private List<Node> parseBlock(FunDefNode funDef) {
		List<Node> list = new ArrayList<>();
		while (!iterator.peek(TokenType.CURLY_CLOSE)) {
			list.add(parseFunLineExpression(funDef));
		}
		return list;
	}

	private List<ArgDefNode> parseFunArgsDef() {
		List<ArgDefNode> list = new ArrayList<>();

		if (iterator.peek(TokenType.PAREN_CLOSE)) {
			return list;
		}

		list.add(parseArgDef());

		while (iterator.peek(TokenType.COMMA)) {
			iterator.consume(TokenType.COMMA);
			list.add(parseArgDef());
		}

		return list;
	}

	private ArgDefNode parseArgDef() {
		iterator.consume(TokenType.LET);
		return new ArgDefNode(parseType(), parseSimpleIdentifier());
	}

	private Node parseChainedExpression() {
		Node expr = parseFirstBinaryExpression();

		while (iterator.peek(TokenType.DOT, TokenType.PAREN_OPEN, TokenType.ARROW, TokenType.DOLLAR, TokenType.HASH)) {
			iterator.consume();
			// chained long identifier
			switch (iterator.peek(-1)) {
			case DOT:
				expr = new MembersAccess(expr, parseSimpleIdentifier());
				break;
			case ARROW:
				expr = new PointerMemberAccess(expr, parseSimpleIdentifier());
				break;
			case HASH:
				iterator.consume(TokenType.PAREN_OPEN);
				expr = new FunCallNode(expr, parseFunArgs(), true);
				iterator.consume(TokenType.PAREN_CLOSE);
				break;
			case PAREN_OPEN:
				expr = new FunCallNode(expr, parseFunArgs(), false);
				iterator.consume(TokenType.PAREN_CLOSE);
				break;
			}
		}

		return expr;
	}

	private LetDefNode parseLetDef() {
		iterator.consume(TokenType.LET);
		final TypeNode type = parseType();
		final IdentifierNode identifier = parseSimpleIdentifier();
		if (iterator.peek(TokenType.STRICT_ASSIGN)) {
			iterator.consume(TokenType.STRICT_ASSIGN);
			final Node value = parseExpression();

			return new LetDefNode(type, identifier, value);
		}

		return new LetDefNode(type, identifier);
	}

	private LetDefNode parseStaticLetDef() {
		iterator.consume(TokenType.LET);
		iterator.consume(TokenType.STATIC);

		final TypeNode type = parseType();
		final IdentifierNode identifier = parseSimpleIdentifier();

		if (iterator.peek(TokenType.STRICT_ASSIGN)) {
			iterator.consume(TokenType.STRICT_ASSIGN);
			final Node value = parseExpression();

			return new LetDefNode(type, identifier, value, true);
		}

		return new LetDefNode(type, identifier, true);
	}

	private TypeNode parseType() {
		TypeNode type = null;

		if (iterator.peek(TokenType.PRIMITIVE_TYPE)) {
			type = new PrimitiveTypeNode(iterator.consume(TokenType.PRIMITIVE_TYPE));
		}

		while (iterator.peek(TokenType.COLON)) {
			iterator.consume(TokenType.COLON);
			type = new PointerTypeNode(type);
		}

		return type;
	}

	private Node parseBinaryExpression(Supplier<Node> leftProvider, TokenType... opTypes) {
		Node left = leftProvider.get();

		while (iterator.peek(opTypes)) {
			final TokenType op = iterator.consume(opTypes).getType();
			Node right = parseMultiplicativeExpression();

			left = new BinaryExpressionNode(left, op, right);
		}

		return left;
	}

	private Node parseFirstBinaryExpression() {
		return parseLogicalXORExpression();
	}

	private Node parseLogicalXORExpression() {
		return parseBinaryExpression(this::parseLogicalORExpression, TokenType.BIT_XOR);
	}

	private Node parseLogicalORExpression() {
		return parseBinaryExpression(this::parseLogicalANDExpression, TokenType.OR);
	}

	private Node parseLogicalANDExpression() {
		return parseBinaryExpression(this::parseBitORExpression, TokenType.AND);
	}

	private Node parseBitORExpression() {
		return parseBinaryExpression(this::parseBitXORExpression, TokenType.BIT_OR);
	}

	private Node parseBitXORExpression() {
		return parseBinaryExpression(this::parseBitANDExpression, TokenType.BIT_XOR);
	}

	private Node parseBitANDExpression() {
		return parseBinaryExpression(this::parseComparisonExpression, TokenType.BIT_AND);
	}

	private Node parseComparisonExpression() {
		return parseBinaryExpression(this::parseShiftExpression, TokenType.COMPARAISON);
	}

	private Node parseShiftExpression() {
		return parseBinaryExpression(this::parseAdditiveExpression, TokenType.BIT_SHIFT);
	}

	private Node parseAdditiveExpression() {
		return parseBinaryExpression(this::parseMultiplicativeExpression, TokenType.PLUS, TokenType.MINUS);
	}

	private Node parseMultiplicativeExpression() {
		return parseBinaryExpression(this::parsePrimary, TokenType.MUL, TokenType.DIV, TokenType.MODULO);
	}

	private IdentifierNode parseSimpleIdentifier() {
		return new IdentifierNode(((IdentifierToken) iterator.consume(TokenType.IDENT)).getValue());
	}

	private Node parseIdentifier() {
		Function<Node, Node> unaryHandler = (e) -> e;

		if (iterator.peek(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS, TokenType.BIT_NOT)) {
			final TokenType type = iterator.consume().getType();
			unaryHandler = (e) -> new UnaryExpressionNode(e, type, true);
		}

		Node expr = parseSimpleIdentifier();

		while (iterator.peek(TokenType.DOT, TokenType.PAREN_OPEN, TokenType.ARROW, TokenType.HASH)) {
			iterator.consume();
			// chained long identifier
			switch (iterator.peek(-1)) {
			case DOT:
				expr = new MembersAccess(expr, parseSimpleIdentifier());
				break;
			case ARROW:
				expr = new PointerMemberAccess(expr, parseSimpleIdentifier());
				break;
			case HASH:
				iterator.consume(TokenType.PAREN_OPEN);
				expr = new FunCallNode(expr, parseFunArgs(), true);
				iterator.consume(TokenType.PAREN_CLOSE);
				break;
			case PAREN_OPEN:
				expr = new FunCallNode(expr, parseFunArgs(), false);
				iterator.consume(TokenType.PAREN_CLOSE);
				break;
			}
		}

		if (iterator.peek(TokenType.ASSIGN)) {
			final TokenType assignType = iterator.consume(TokenType.ASSIGN).getType();
			expr = new LetSetNode(expr, parseExpression(), assignType);
			return expr;
		} else if (iterator.peek(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {
			expr = new UnaryExpressionNode(expr, iterator.consume(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS).getType(), false);
		}

		expr = unaryHandler.apply(expr);

		return expr;
	}

	private List<Node> parseFunArgs() {
		List<Node> list = new ArrayList<>();

		if (iterator.peek(TokenType.PAREN_CLOSE)) {
			return list;
		}

		list.add(parseExpression());

		while (iterator.peek(TokenType.COMMA)) {
			iterator.consume(TokenType.COMMA);
			list.add(parseExpression());
		}

		return list;
	}

	private Node parseParenthesizedExpression() {
		iterator.consume(TokenType.PAREN_OPEN);
		
		if(isType()) {
			final TypeNode castType = parseType();
			iterator.consume(TokenType.PAREN_CLOSE);
			
			final Node expression = parseExpression();
			
			return new CastNode(castType, expression);
		}
		
		final Node expression = parseExpression();
		iterator.consume(TokenType.PAREN_CLOSE);
		
		return expression;
	}

	private boolean isType() {
		int i = 0;
		while(iterator.peek(i, TokenType.IDENT, TokenType.DOT, TokenType.TYPE)) {
			i++;
		}
		
		return !iterator.peek(i, TokenType.MATH_OP);
	}

	private Node parsePointerDeref() {
		iterator.consume(TokenType.DOLLAR);
		if (iterator.peek(TokenType.PAREN_OPEN)) {
			return new PointerDerefNode(parsePrimary());
		} else {
			return new PointerDerefNode(parseSimpleIdentifier());
		}
	}

	private Node parsePrimary() {
		switch (iterator.peek()) {
		case PAREN_OPEN:
			return parseParenthesizedExpression();
		case DOLLAR:
			return parsePointerDeref();
		case COLON:
			return parsePointerRef();
		case PLUS_PLUS:
		case MINUS_MINUS:
		case NOT:
		case IDENT:
			return parseIdentifier();
		case STRING_LIT:
			return new StringLiteralNode((StringLiteralToken) iterator.consume());
		case NUM_LIT:
			return new NumericLiteralNode((NumericLiteralToken) iterator.consume());
		}

		throw new L3Exception("Unexpected token: " + iterator.peek());
	}

	private Node parseCast() {
		iterator.consume(TokenType.PAREN_OPEN);
		final TypeNode castType = parseType();
		iterator.consume(TokenType.PAREN_CLOSE);
		
		Node value = parsePrimary();
		
		return new CastNode(castType, value);
	}

	private Node parsePointerRef() {
		iterator.consume(TokenType.COLON);
		return new PointerRefNode(parseSimpleIdentifier());
	}

	private void notImplemented() {
		throw new L3Exception("Not implemented: " + iterator.consume());
	}

	public FileNode getFile() {
		return file;
	}

}
