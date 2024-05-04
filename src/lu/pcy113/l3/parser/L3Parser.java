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
import lu.pcy113.l3.parser.ast.ComparisonOpNode;
import lu.pcy113.l3.parser.ast.ConArgValNode;
import lu.pcy113.l3.parser.ast.ConArgsValNode;
import lu.pcy113.l3.parser.ast.DelocalizingNode;
import lu.pcy113.l3.parser.ast.ElseDefNode;
import lu.pcy113.l3.parser.ast.FinallyDefNode;
import lu.pcy113.l3.parser.ast.ForDefNode;
import lu.pcy113.l3.parser.ast.FunArgDefNode;
import lu.pcy113.l3.parser.ast.FunArgValNode;
import lu.pcy113.l3.parser.ast.FunArgsDefNode;
import lu.pcy113.l3.parser.ast.FunArgsValNode;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.IfDefNode;
import lu.pcy113.l3.parser.ast.ImportDefNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.LetTypeSetNode;
import lu.pcy113.l3.parser.ast.LocalizingNode;
import lu.pcy113.l3.parser.ast.LogicalOpNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.ObjectInitNode;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.StringLitNode;
import lu.pcy113.l3.parser.ast.TypeNode;
import lu.pcy113.l3.parser.ast.VarNumNode;
import lu.pcy113.l3.parser.ast.WhileDefNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ImportScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.ScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.StructDefNode;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
import lu.pcy113.l3.utils.FileUtils;

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

	private void parsePackageDefExpr(FileNode root) throws ParserException {
		if (peek(TokenType.PACKAGE) && peek(1, TokenType.STRING)) {

			PackageDefNode packageDef = new PackageDefNode(consume(TokenType.PACKAGE), ((StringLiteralToken) consume(TokenType.STRING)).getValue());

			if (!root.getSource().replaceAll("^" + packageDef.getValue() + "\\.", "").equals(FileUtils.getExtension(root.getSource()))) {
				throw new ParserException("Package declaration not matching with file path: " + root.getSource() + " (" + root.getSource().replaceAll("^" + packageDef.getValue() + "\\.", "") + ") & " + packageDef.getValue());
			}

			root.add(packageDef);

			consume(TokenType.SEMICOLON);
		} else {
			throw new ParserException("Missing package declaration");
		}
	}

	private Node parseFunctionalExpr(Node parent) throws ParserException {
		ScopeContainer container = parent.getClosestContainer();

		if (peek(TokenType.STRUCT) && peek(1, TokenType.IDENT)) {
			StructDefNode ltdn = parseStructDef(parent);

			return ltdn;
		} else if (peek(TokenType.LET)) { // var declaration
			LetTypeDefNode ltdn = parseLetTypeDef(parent);

			return ltdn;
		} else if ((peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(TokenType.PAREN_OPEN))
				|| (peek(TokenType.IDENT) && peek(1, TokenType.DOT) && peek(2, TokenType.IDENT) && (peek(3, TokenType.HASH, TokenType.PAREN_OPEN) || peek(4, TokenType.PAREN_OPEN))))) { // function
																																															// call
			FunCallNode funCall = parseFunCall();
			parent.add(funCall);

			return funCall;
		} else if (canParseFun()) {

			FunDefNode ltdn = parseFunDefExpr(parent);
			if (container.containsFunDescriptor(ltdn)) {
				throw new ParserException("Fun " + ltdn.getIdent().getValue() + " already defined: " + ltdn.getIdent().getLine() + ":" + ltdn.getIdent().getColumn());
			}
			container.addDescriptor(ltdn.getIdent().getValue(), new FunScopeDescriptor(ltdn.getIdent(), ltdn));

			return ltdn;
		} else if (peek(TokenType.RETURN)) {
			ReturnNode rtn = parseReturnExpr();
			parent.add(rtn);

			return rtn;
		} else if (peek(TokenType.COMMENT)) {
			consume(TokenType.COMMENT); // ignore
		} else if (canParseLetTypeSet()) {

			LetTypeSetNode set = parseLetTypeSet();
			if (!container.containsDescriptor(set.getLetIdent().getValue())) {
				throw new ParserException("let " + set.getLetIdent().getValue() + " isn't defined: " + set.getLetIdent().getPosition());
			}
			parent.add(set);

			return set;
		} else if (peek(TokenType.IF)) {
			IfContainerNode ifdef = parseIfContainerExpr(parent);

			return ifdef;
		} else if (peek(TokenType.WHILE)) {
			WhileDefNode whileDef = parseWhileDefNode(parent);

			return whileDef;
		} else if (peek(TokenType.FOR)) {
			ForDefNode forDef = parseForDefNode(parent);

			return forDef;
		} else if (peek(TokenType.IMPORT)) {
			ImportDefNode importDef = parseImportDefNode(parent);

			container.addDescriptor(importDef.getIdentValue(), new ImportScopeDescriptor(importDef));

			return importDef;
		} else {
			throw new ParserException("Expression not implemented: (" + parent.getClass().getSimpleName() + ") " + peek() + "->" + peek(1) + "->" + peek(2));
		}

		return null;
	}

	private boolean canParseLetTypeSet() {
		return (peek(TokenType.IDENT) && (peek(1, TokenType.ASSIGN, TokenType.BRACKET_OPEN)) || (peek(TokenType.BIT_AND) && peek(1, TokenType.IDENT))) || (peek(TokenType.IDENT) && peek(1, TokenType.DOT) && peek(2, TokenType.IDENT));
	}

	private StructDefNode parseStructDef(Node parent) throws ParserException {
		consume(TokenType.STRUCT);

		IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

		consume(TokenType.CURLY_OPEN);

		StructDefNode struct = new StructDefNode(ident);

		while (peek(TokenType.LET)) {
			parseLetTypeDef(struct);
			consume(TokenType.SEMICOLON);
		}

		consume(TokenType.CURLY_CLOSE);

		parent.add(struct);
		parent.getClosestContainer().addDescriptor(ident.getValue(), new StructScopeDescriptor(ident, struct));

		return struct;
	}

	private ImportDefNode parseImportDefNode(Node parent) throws ParserException {
		consume(TokenType.IMPORT);

		StringLiteralToken strLit = (StringLiteralToken) consume(TokenType.STRING);

		IdentifierToken ident = null;

		if (peek(TokenType.AS)) {
			consume(TokenType.AS);
			ident = (IdentifierToken) consume(TokenType.IDENT);
		}

		consume(TokenType.SEMICOLON);

		return new ImportDefNode(strLit, ident);
	}

	private Node parseLineExpr(Node parent) throws ParserException {
		Node node = parseFunctionalExpr(parent);
		if (node instanceof LetTypeDefNode || node instanceof FunCallNode || node instanceof ReturnNode || node instanceof LetTypeSetNode || node instanceof FunCallNode) {
			consume(TokenType.SEMICOLON);
		}
		return node;
	}

	private ForDefNode parseForDefNode(Node parent) throws ParserException {
		if (peek(TokenType.FOR) && peek(1, TokenType.PAREN_OPEN)) {
			Token forToken = consume(TokenType.FOR);
			consume(TokenType.PAREN_OPEN);

			ForDefNode forDef = new ForDefNode(forToken);

			if (!peek(TokenType.SEMICOLON)) {
				Node letDef = parseLetTypeDef(forDef);
				forDef.setLetTypeDef(true);
			}

			consume(TokenType.SEMICOLON);

			if (!peek(TokenType.SEMICOLON)) {
				Node condition = parseExpression();
				forDef.add(condition);
				forDef.setCondition(true);
			}

			consume(TokenType.SEMICOLON);

			if (!peek(TokenType.PAREN_CLOSE)) {
				Node shortBody = parseFunctionalExpr(forDef);
				forDef.setShortBody(true);
			}

			consume(TokenType.PAREN_CLOSE);

			parent.add(forDef);

			ScopeBodyNode body = parseScopeDefBody(forDef);

			return forDef;
		}
		throw new ParserException("An error occured when parsing for-statement");
	}

	private WhileDefNode parseWhileDefNode(Node parent) throws ParserException {
		if (peek(TokenType.WHILE) && peek(1, TokenType.PAREN_OPEN)) {
			Token whileToken = consume(TokenType.WHILE);
			consume(TokenType.PAREN_OPEN);

			Node condition = parseExpression();

			consume(TokenType.PAREN_CLOSE);

			WhileDefNode whileDef = new WhileDefNode(whileToken, condition);

			parent.add(whileDef);

			ScopeBodyNode body = parseScopeDefBody(whileDef);

			if (peek(TokenType.ELSE)) {
				ElseDefNode elseDef = parseElseDefExpr();
				whileDef.add(elseDef);
			}

			if (peek(TokenType.FINALLY)) {
				FinallyDefNode finallyDef = parseFinallyDefExpr();
				whileDef.add(finallyDef);
			}

			if (peek(TokenType.ELSE, TokenType.FINALLY)) {
				throw new ParserException("Unexpected token: " + peek().toString(0) + ", cannot continue while-statement after final else/finally.");
			}

			return whileDef;
		}
		throw new ParserException("An error occured when parsing while-statement");
	}

	private IfContainerNode parseIfContainerExpr(Node parent) throws ParserException {
		if (peek(TokenType.IF) && peek(1, TokenType.PAREN_OPEN)) {
			IfContainerNode container = new IfContainerNode();

			parent.add(container);

			IfDefNode ifDef = parseIfDefExpr();
			container.add(ifDef);

			while (peek(TokenType.ELSE)) {
				if (peek(1, TokenType.IF)) {
					consume(TokenType.ELSE);
					IfDefNode ifDef2 = parseIfDefExpr();
					container.add(ifDef2);
				} else {
					ElseDefNode elseDef = parseElseDefExpr();
					container.add(elseDef);
					break;
				}
			}

			if (peek(TokenType.FINALLY)) {
				FinallyDefNode finallyDef = parseFinallyDefExpr();
				container.add(finallyDef);
			}

			if (peek(TokenType.ELSE, TokenType.FINALLY)) {
				throw new ParserException("Unexpected token: " + peek().toString(0) + ", cannot continue if-statement after final else/finally.");
			}

			return container;
		}
		throw new ParserException("An error occured when parsing if-statements");
	}

	private FinallyDefNode parseFinallyDefExpr() throws ParserException {
		if (peek(TokenType.FINALLY)) {
			Token elseToken = consume(TokenType.FINALLY);

			FinallyDefNode finallyDef = new FinallyDefNode(elseToken);

			ScopeBodyNode body = parseScopeDefBody(finallyDef);

			return finallyDef;
		}
		throw new ParserException("An error occured when parsing finally-statement");
	}

	private ElseDefNode parseElseDefExpr() throws ParserException {
		if (peek(TokenType.ELSE)) {
			Token elseToken = consume(TokenType.ELSE);

			ElseDefNode elseDef = new ElseDefNode(elseToken);

			ScopeBodyNode body = parseScopeDefBody(elseDef);

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

			ScopeBodyNode body = parseScopeDefBody(ifDef);

			return ifDef;
		}
		throw new ParserException("An error occured when parsing if-statement");
	}

	private ScopeBodyNode parseScopeDefBody(Node fdn) throws ParserException {
		ScopeBodyNode fdb = new ScopeBodyNode();
		fdn.add(fdb);

		if (peek(TokenType.CURLY_OPEN)) {
			consume(TokenType.CURLY_OPEN);
			while (!peek(TokenType.CURLY_CLOSE)) {
				parseLineExpr(fdb);
			}

			consume(TokenType.CURLY_CLOSE);
		} else {
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

		} else if (peek(TokenType.IDENT) && peek(1, TokenType.DOT) && peek(2, TokenType.IDENT) && peek(3, TokenType.ASSIGN)) {
			// struct type

			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);
			consume(TokenType.DOT);
			IdentifierToken ident2 = (IdentifierToken) consume(TokenType.IDENT);

			boolean array = peek(TokenType.BRACKET_OPEN);
			Node arrayIndex = null;
			Node val = null;
			if (array) {
				consume(TokenType.BRACKET_OPEN);
				arrayIndex = parseExpression();
				consume(TokenType.BRACKET_CLOSE);

				val = new VarNumNode(ident, ident2, arrayIndex);
			} else {
				val = new VarNumNode(ident, ident2);
			}

			Token assign = consume(TokenType.ASSIGN);

			Node newValue = parseExpression();
			// TODO array parsing

			LetTypeSetNode typeDefNode = new LetTypeSetNode(val, newValue);

			return typeDefNode;

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

		if ((fdb.getChildren().isEmpty() || !(fdb.getChildren().getLast() instanceof ReturnNode)) && !fdn.getReturnType().isVoid()) {
			throw new ParserException("Missing final return statement: " + peek());
		} else if (fdn.getReturnType().isVoid()) {
			fdb.add(new ReturnNode(fdn.getReturnType(), null));
		}

		consume(TokenType.CURLY_CLOSE);

		return fdb;
	}

	private FunCallNode parseFunCall() throws ParserException {
		if (peek(TokenType.IDENT) && peek(1, TokenType.DOT) && peek(2, TokenType.IDENT) && peek(3, TokenType.PAREN_OPEN)) {
			// extern method

			IdentifierToken source = (IdentifierToken) consume(TokenType.IDENT);
			consume(TokenType.DOT);
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

			Token parenOpen = consume(TokenType.PAREN_OPEN);

			FunCallNode fcn = new FunCallNode(source, ident);
			parseFunArgsVal(fcn);

			Token parenClosed = consume(TokenType.PAREN_CLOSE);

			return fcn;
		} else if (peek(TokenType.IDENT) && (peek(1, TokenType.PAREN_OPEN, TokenType.HASH) || peek(1, TokenType.PAREN_OPEN))) {
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

			LetTypeDefNode typeDefNode = new LetTypeDefNode(typeNode, (IdentifierToken) ident, false, true);
			// TODO: Array

			return new FunArgDefNode(typeDefNode);
		} else if (peek(TokenType.IDENT)) {
			throw new ParserException("Defined typed not defined yet.");
		}
		throw new ParserException("Undefined Var def");
	}

	private TypeNode parseType() throws ParserException {
		if (peek(TokenType.IDENT)) {
			IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);
			TypeNode node = new TypeNode(false, ident);
			while (peek(TokenType.COLON)) {
				node = new TypeNode(node);
				consume(TokenType.COLON);
			}
			return node;
		} else if (peek(TokenType.TYPE)) {
			Token type = consume(TokenType.TYPE);
			TypeNode node = new TypeNode(true, type.getType());
			while (peek(TokenType.COLON)) {
				node = new TypeNode(node);
				consume(TokenType.COLON);
			}
			return node;
		} else if (peek(TokenType.VOID)) {
			return new TypeNode(true, consume(TokenType.VOID));
		} else {
			throw new ParserException("Unsupported type: " + peek());
		}
	}

	private LetTypeDefNode parseLetTypeDef(Node parent) throws ParserException {
		ScopeContainer container = parent.getClosestContainer();

		Token let = consume(TokenType.LET);
		boolean iStatic = peek(TokenType.STATIC);
		if (iStatic) {
			consume(TokenType.STATIC);
		}
		TypeNode type = parseType();

		IdentifierToken ident = (IdentifierToken) consume(TokenType.IDENT);

		LetTypeDefNode typeDefNode = new LetTypeDefNode(type, ident, iStatic, false);

		if (container.containsDescriptor(ident.getValue())) {
			throw new ParserException("let " + ident.getValue() + " already defined: " + ident.getPosition());
		}
		parent.add(typeDefNode);
		container.addDescriptor(ident.getValue(), new LetScopeDescriptor(ident, typeDefNode));

		if (!peek(TokenType.ASSIGN)) {
			return typeDefNode;
		}

		Token assign = consume(TokenType.ASSIGN);

		/*if (peek(TokenType.NEW)) {
			
			
			
		} *//*else if (type.isPointer() && peek(TokenType.CURLY_OPEN)) {

			if (peek(TokenType.CURLY_OPEN)) {
				consume(TokenType.CURLY_OPEN);
				parseArrayArgs().forEach(typeDefNode::add);
				consume(TokenType.CURLY_CLOSE);
			}

		} else*/ /*{

			typeDefNode.add(parseExpression());
		}*/
		
		typeDefNode.add(parseExpression());

		return typeDefNode;
	}

	private ConArgsValNode parseConArgsVal(ObjectInitNode fcn) throws ParserException {
		ConArgsValNode fargs = new ConArgsValNode();
		fcn.add(fargs);
		int index = 0;
		while (!peek(TokenType.PAREN_CLOSE)) {
			fargs.add(parseConArgVal(index++));
			if (peek(TokenType.COMMA)) {
				consume();
			}
		}
		return fargs;
	}

	private Node parseConArgVal(int index) throws ParserException {
		Node exprNode = parseExpression();
		return new ConArgValNode(index, exprNode);
	}

	private int getLetIndex(ScopeContainer container) {
		return (int) (long) container.getDescriptors().values().stream().flatMap(List::stream).map((ScopeDescriptor i) -> {
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

		while (peek(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.LESS, TokenType.LESS_EQUALS, TokenType.GREATER, TokenType.GREATER_EQUALS)) {
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
			left = new BinaryOpNode(left, op, right);
		}

		return left;
	}

	private Node parseFactor() throws ParserException {
		Node left = parsePrimary();

		if (peek(TokenType.ASSIGN)) {
			left = parseLetTypeSet(left);
		}

		while (peek(TokenType.MUL, TokenType.DIV, TokenType.MODULO, TokenType.BIT_XOR, TokenType.BIT_AND, TokenType.BIT_OR)) {
			TokenType op = consume().getType();
			Node right = parsePrimary();
			left = new BinaryOpNode(left, op, right);
		}

		return left;
	}

	private Node parsePrimary() throws ParserException {
		if (peek(TokenType.NUM_LIT, TokenType.TRUE, TokenType.FALSE)) {

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

			Node expr = null;

			if (peek(TokenType.PAREN_OPEN)) {
				expr = parseExpression();
			} else if (peek(TokenType.IDENT)) {
				expr = parseIdent();
			} else {
				throw new RuntimeException("Unexpected token: " + peek());
			}

			return new DelocalizingNode(expr);

		} else if (peek(TokenType.COLON)) {

			consume(TokenType.COLON);
			return new LocalizingNode(parseIdent());

		} else if (peek(TokenType.STRING)) {

			return new StringLitNode((StringLiteralToken) consume(TokenType.STRING));

		} else if(peek(TokenType.NEW)) {
			
			consume(TokenType.NEW);

			TypeNode newType = parseType();

			if (peek(TokenType.BRACKET_OPEN)) { // array init

				consume(TokenType.BRACKET_OPEN);

				ArrayInitNode arr = new ArrayInitNode(newType);

				if (peek(TokenType.NUM_LIT)) {
					int newTypeArrayLength = (int) (long) ((NumericLiteralToken) consume(TokenType.NUM_LIT)).getValue();
					arr.setArraySize(newTypeArrayLength);
				}

				consume(TokenType.BRACKET_CLOSE);

				if (peek(TokenType.CURLY_OPEN)) {
					consume(TokenType.CURLY_OPEN);

					parseArrayArgs().forEach(arr::add);

					consume(TokenType.CURLY_CLOSE);
				}

				return arr;

			} else if (peek(TokenType.PAREN_OPEN)) { // constructor

				consume(TokenType.PAREN_OPEN);

				ObjectInitNode objInit = new ObjectInitNode(newType);
				parseConArgsVal(objInit);

				consume(TokenType.PAREN_CLOSE);
				
				return objInit;

			}
			
		}
		
		throw new RuntimeException("Unexpected token: " + peek());
	}

	private Node parseIdent() throws ParserException {
		IdentifierToken varIdent = (IdentifierToken) consume(TokenType.IDENT);
		Node var = null;
		if (peek(TokenType.BRACKET_OPEN)) {

			consume(TokenType.BRACKET_OPEN);
			Node expr = parseExpression();
			consume(TokenType.BRACKET_CLOSE);
			var = new VarNumNode(varIdent, expr);

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

		} else if (peek(TokenType.DOT) && peek(1, TokenType.IDENT) && !peek(2, TokenType.PAREN_OPEN)) {

			consume(TokenType.DOT);
			var = new VarNumNode(varIdent, (IdentifierToken) consume(TokenType.IDENT));

		} else {

			var = new VarNumNode(varIdent);

		}
		return var;
	}

	private Node parseLetTypeSet(Node var) throws ParserException {
		consume(TokenType.ASSIGN);
		Node expr = parseExpression();
		return new LetTypeSetNode(var, expr);
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

	public FileNode getRoot() {
		return root;
	}

}
