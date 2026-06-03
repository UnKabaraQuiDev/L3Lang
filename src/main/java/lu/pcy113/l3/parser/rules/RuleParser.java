package lu.pcy113.l3.parser.rules;

import java.util.ArrayList;
import java.util.List;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.ctrl.BreakNode;
import lu.pcy113.l3.parser.ast.ctrl.ForNode;
import lu.pcy113.l3.parser.ast.ctrl.IfNode;
import lu.pcy113.l3.parser.ast.ctrl.WhileNode;
import lu.pcy113.l3.parser.ast.ctrl.YieldNode;
import lu.pcy113.l3.parser.ast.expr.ExpressionStatementNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.ident.PackageNode;
import lu.pcy113.l3.parser.ast.let.VariableDeclarationNode;
import lu.pcy113.l3.parser.ast.macro.MacroCallNode;
import lu.pcy113.l3.parser.ast.macro.PreprocessorDirectiveNode;
import lu.pcy113.l3.parser.ast.method.MethodDefNode;
import lu.pcy113.l3.parser.ast.method.ParameterNode;
import lu.pcy113.l3.parser.ast.scope.BlockNode;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public final class RuleParser {

	private final ParserContext ctx;
	private final L3Grammar grammar;
	private final ExpressionParser expressionParser;

	public RuleParser(final ParserContext ctx, final L3Grammar grammar) {
		this.ctx = ctx;
		this.grammar = grammar;
		this.expressionParser = new ExpressionParser(this);
	}

	public ParserContext ctx() {
		return this.ctx;
	}

	public FileNode parseFile(final String path, final String name) {
		final FileNode file = new FileNode(path, name);

		if (this.ctx.peek(TokenType.PACKAGE)) {
			file.setPackageNode(this.parsePackage());
			this.ctx.consume(TokenType.SEMICOLON);
		}

		while (this.ctx.hasNext()) {
			final Node node = this.parseByRules(this.grammar.topLevelRules(), "top-level declaration");
			file.addChild(node);
		}

		return file;
	}

	private Node parseByRules(final List<ParseRule> rules, final String description) {
		for (final ParseRule rule : rules) {
			if (rule.matches(this.ctx, this)) {
				return rule.parse(this.ctx, this);
			}
		}
		throw this.ctx.error("No rule matched for " + description);
	}

	public PackageNode parsePackage() {
		this.ctx.consume(TokenType.PACKAGE);
		final List<IdentifierNode> parts = new ArrayList<>();
		if (this.ctx.peek(TokenType.STRING_LIT)) {
			// Keep package strings usable with the old PackageNode shape.
			final String raw = ((lu.pcy113.l3.lexer.tokens.StringLiteralToken) this.ctx.consume(TokenType.STRING_LIT))
					.getValue();
			parts.add(new IdentifierNode(raw));
			return new PackageNode(parts);
		}
		parts.add(new IdentifierNode(this.ctx.consumeIdentifier().getValue()));
		while (this.ctx.accept(TokenType.DOT)) {
			parts.add(new IdentifierNode(this.ctx.consumeIdentifier().getValue()));
		}
		return new PackageNode(parts);
	}

	public ImportNode parseImport() {
		this.ctx.consume(TokenType.IMPORT);
		final List<IdentifierNode> parts = new ArrayList<>();
		parts.add(new IdentifierNode(this.ctx.consumeIdentifier().getValue()));
		while (this.ctx.accept(TokenType.DOT)) {
			parts.add(new IdentifierNode(this.ctx.consumeIdentifier().getValue()));
		}
		ImportNode node;
		if (this.ctx.accept(TokenType.AS)) {
			node = new ImportNode(parts, new IdentifierNode(this.ctx.consumeIdentifier().getValue()));
		} else {
			node = new ImportNode(parts);
		}
		this.ctx.consume(TokenType.SEMICOLON);
		return node;
	}

	public PreprocessorDirectiveNode parsePreprocessorDirective() {
		this.ctx.consume(TokenType.HASH);
		final String name = this.ctx.peek(TokenType.IDENT) ? this.ctx.consumeIdentifier().getValue() : "";
		final List<Token> rest = this.ctx.consumeRemainingOnLine();
		return new PreprocessorDirectiveNode(name, rest);
	}

	public MethodDefNode parseMethodDefinition() {
		if (this.ctx.accept(TokenType.FUN)) {
			// Old spelling remains accepted, but the produced AST is the new method node.
		}
		final TypeReferenceNode returnType = this.parseTypeReference();
		final String name = this.ctx.consumeIdentifier().getValue();
		final List<String> genericParameters = this.parseGenericParameterNames();
		this.ctx.consume(TokenType.PAREN_OPEN);
		final List<ParameterNode> parameters = this.parseParameters();
		this.ctx.consume(TokenType.PAREN_CLOSE);
		final BlockNode body = this.parseBlock(false);
		return new MethodDefNode(returnType, name, genericParameters, parameters, body);
	}

	public VariableDeclarationNode parseVariableDeclaration(final boolean requireSemicolon) {
		boolean staticDeclaration = false;
		if (this.ctx.accept(TokenType.LET)) {
			staticDeclaration = this.ctx.accept(TokenType.STATIC);
		}
		final TypeReferenceNode type = this.parseTypeReference();
		final String name = this.ctx.consumeIdentifier().getValue();
		Node initializer = null;
		if (this.ctx.accept(TokenType.STRICT_ASSIGN)) {
			initializer = this.parseExpression();
		}
		if (requireSemicolon) {
			this.ctx.consume(TokenType.SEMICOLON);
		}
		return new VariableDeclarationNode(type, name, initializer, staticDeclaration);
	}

	public BlockNode parseBlock(final boolean computed) {
		this.ctx.consume(TokenType.CURLY_OPEN);
		final BlockNode block = new BlockNode(computed);
		while (!this.ctx.peek(TokenType.CURLY_CLOSE)) {
			if (!this.ctx.hasNext()) {
				throw this.ctx.error("Unterminated block");
			}
			block.add(this.parseStatement());
		}
		this.ctx.consume(TokenType.CURLY_CLOSE);
		return block;
	}

	public Node parseStatement() {
		return this.parseByRules(this.grammar.statementRules(), "statement");
	}

	public Node parseStatementBody() {
		if (this.ctx.peek(TokenType.CURLY_OPEN)) {
			return this.parseBlock(false);
		}
		return this.parseStatement();
	}

	public IfNode parseIf() {
		this.ctx.consume(TokenType.IF);
		this.ctx.consume(TokenType.PAREN_OPEN);
		final Node condition = this.parseExpression();
		this.ctx.consume(TokenType.PAREN_CLOSE);
		final Node thenBranch = this.parseStatementBody();
		Node elseBranch = null;
		if (this.ctx.accept(TokenType.ELSE)) {
			elseBranch = this.parseStatementBody();
		}
		return new IfNode(condition, thenBranch, elseBranch);
	}

	public WhileNode parseWhile() {
		this.ctx.consume(TokenType.WHILE);
		this.ctx.consume(TokenType.PAREN_OPEN);
		final Node condition = this.parseExpression();
		this.ctx.consume(TokenType.PAREN_CLOSE);
		final Node body = this.parseStatementBody();
		Node elseBranch = null;
		if (this.ctx.accept(TokenType.ELSE)) {
			elseBranch = this.parseStatementBody();
		}
		return new WhileNode(condition, body, elseBranch);
	}

	public ForNode parseFor() {
		this.ctx.consume(TokenType.FOR);
		this.ctx.consume(TokenType.PAREN_OPEN);
		Node initializer = null;
		if (!this.ctx.peek(TokenType.SEMICOLON)) {
			initializer = this.looksLikeVariableDeclaration(0) ? this.parseVariableDeclaration(false)
					: this.parseExpression();
		}
		this.ctx.consume(TokenType.SEMICOLON);

		Node condition = null;
		if (!this.ctx.peek(TokenType.SEMICOLON)) {
			condition = this.parseExpression();
		}
		this.ctx.consume(TokenType.SEMICOLON);

		Node update = null;
		if (!this.ctx.peek(TokenType.PAREN_CLOSE)) {
			update = this.parseExpression();
		}
		this.ctx.consume(TokenType.PAREN_CLOSE);
		return new ForNode(initializer, condition, update, this.parseStatementBody());
	}

	public ReturnNode parseReturn() {
		this.ctx.consume(TokenType.RETURN);
		Node expression = null;
		if (!this.ctx.peek(TokenType.SEMICOLON)) {
			expression = this.parseExpression();
		}
		this.ctx.consume(TokenType.SEMICOLON);
		return expression == null ? new ReturnNode() : new ReturnNode(expression);
	}

	public YieldNode parseYield() {
		if (!this.ctx.accept(TokenType.YIELD) && !this.ctx.acceptIdentifier("yield")) {
			throw this.ctx.expected(TokenType.YIELD);
		}
		Node expression = null;
		if (!this.ctx.peek(TokenType.SEMICOLON)) {
			expression = this.parseExpression();
		}
		this.ctx.consume(TokenType.SEMICOLON);
		return new YieldNode(expression);
	}

	public Node parseBreakOrBreakpoint() {
		if (!this.ctx.accept(TokenType.BREAK) && !this.ctx.acceptIdentifier("break")) {
			throw this.ctx.expected(TokenType.BREAK);
		}
		if (this.ctx.accept(TokenType.HASH)) {
			this.ctx.consume(TokenType.PAREN_OPEN);
			final List<Node> args = this.expressionParser.parseArgumentList(TokenType.PAREN_CLOSE);
			this.ctx.consume(TokenType.PAREN_CLOSE);
			this.ctx.consume(TokenType.SEMICOLON);
			return new MacroCallNode("break", args);
		}
		this.ctx.consume(TokenType.SEMICOLON);
		return new BreakNode();
	}

	public ExpressionStatementNode parseExpressionStatement(final boolean requireSemicolon) {
		final Node expression = this.parseExpression();
		if (requireSemicolon) {
			this.ctx.consume(TokenType.SEMICOLON);
		}
		return new ExpressionStatementNode(expression);
	}

	public Node parseExpression() {
		return this.expressionParser.parseExpression();
	}

	public List<ParameterNode> parseParameters() {
		final List<ParameterNode> parameters = new ArrayList<>();
		if (this.ctx.peek(TokenType.PAREN_CLOSE)) {
			return parameters;
		}
		parameters.add(this.parseParameter());
		while (this.ctx.accept(TokenType.COMMA)) {
			parameters.add(this.parseParameter());
		}
		return parameters;
	}

	public ParameterNode parseParameter() {
		this.ctx.accept(TokenType.LET);
		final TypeReferenceNode type = this.parseTypeReference();
		final String name = this.ctx.consumeIdentifier().getValue();
		return new ParameterNode(type, name);
	}

	public TypeReferenceNode parseTypeReference() {
		String name;
		final TokenType type = this.ctx.peek();
		if (type == null) {
			throw this.ctx.error("Expected type");
		}
		if (type.matches(TokenType.PRIMITIVE_TYPE) || type == TokenType.VOID) {
			name = this.tokenTypeName(this.ctx.consume().getType());
		} else if (type == TokenType.IDENT) {
			name = this.ctx.consumeIdentifier().getValue();
		} else {
			throw this.ctx.error("Expected type");
		}

		List<TypeReferenceNode> genericArguments = List.of();
		if (this.ctx.peek(TokenType.LESS) && this.looksLikeGenericArgumentList(0)) {
			genericArguments = this.parseGenericTypeArguments();
		}

		int pointerDepth = 0;
		while (this.ctx.accept(TokenType.COLON)) {
			pointerDepth++;
		}

		TypeReferenceNode node = new TypeReferenceNode(name, genericArguments, pointerDepth, false);
		if (this.ctx.peek(TokenType.DOT) && this.ctx.peek(1, TokenType.DOT) && this.ctx.peek(2, TokenType.DOT)) {
			this.ctx.consume(TokenType.DOT);
			this.ctx.consume(TokenType.DOT);
			this.ctx.consume(TokenType.DOT);
			node = node.withVarArg(true);
		}
		return node;
	}

	public List<TypeReferenceNode> parseGenericTypeArguments() {
		final List<TypeReferenceNode> args = new ArrayList<>();
		this.ctx.consume(TokenType.LESS);
		if (this.ctx.peek(TokenType.GREATER)) {
			this.ctx.consume(TokenType.GREATER);
			return args;
		}
		args.add(this.parseTypeReference());
		while (this.ctx.accept(TokenType.COMMA)) {
			args.add(this.parseTypeReference());
		}
		this.ctx.consume(TokenType.GREATER);
		return args;
	}

	public List<String> parseGenericParameterNames() {
		final List<String> args = new ArrayList<>();
		if (!this.ctx.peek(TokenType.LESS) || !this.looksLikeGenericArgumentList(0)) {
			return args;
		}
		this.ctx.consume(TokenType.LESS);
		if (this.ctx.peek(TokenType.GREATER)) {
			this.ctx.consume(TokenType.GREATER);
			return args;
		}
		args.add(this.parseGenericParameterName());
		while (this.ctx.accept(TokenType.COMMA)) {
			args.add(this.parseGenericParameterName());
		}
		this.ctx.consume(TokenType.GREATER);
		return args;
	}

	private String parseGenericParameterName() {
		String name = this.ctx.consumeIdentifier().getValue();
		if (this.ctx.peek(TokenType.DOT) && this.ctx.peek(1, TokenType.DOT) && this.ctx.peek(2, TokenType.DOT)) {
			this.ctx.consume(TokenType.DOT);
			this.ctx.consume(TokenType.DOT);
			this.ctx.consume(TokenType.DOT);
			name += "...";
		}
		return name;
	}

	public boolean looksLikeGenericArgumentList(final int offset) {
		if (!this.ctx.peek(offset, TokenType.LESS)) {
			return false;
		}
		int depth = 0;
		for (int i = offset; this.ctx.peek(i) != null; i++) {
			final TokenType type = this.ctx.peek(i);
			if (type != null) {
				switch (type) {
				case LESS:
					depth++;
					break;
				case GREATER:
					depth--;
					if (depth == 0) {
						final TokenType next = this.ctx.peek(i + 1);
						return next == TokenType.PAREN_OPEN || next == TokenType.IDENT || next == TokenType.COLON
								|| next == TokenType.COMMA || next == TokenType.GREATER || next == TokenType.DOT;
					}
					break;
				case SEMICOLON:
				case CURLY_OPEN:
				case CURLY_CLOSE:
				case PAREN_CLOSE:
					return false;
				default:
					break;
				}
			}
		}
		return false;
	}

	public boolean looksLikeMethodHeader(final int offset) {
		int i = offset;
		if (this.ctx.peek(i, TokenType.FUN)) {
			i++;
		}
		if (!this.isTypeStart(i)) {
			return false;
		}
		i = this.skipType(i);
		if (!this.ctx.peek(i, TokenType.IDENT)) {
			return false;
		}
		i++;
		i = this.skipGenericNames(i);
		return this.ctx.peek(i, TokenType.PAREN_OPEN);
	}

	public boolean looksLikeVariableDeclaration(final int offset) {
		int i = offset;
		if (this.ctx.peek(i, TokenType.LET)) {
			i++;
			if (this.ctx.peek(i, TokenType.STATIC)) {
				i++;
			}
		}
		if (!this.isTypeStart(i)) {
			return false;
		}
		i = this.skipType(i);
		return this.ctx.peek(i, TokenType.IDENT) && !this.ctx.peek(i + 1, TokenType.PAREN_OPEN);
	}

	private boolean isTypeStart(final int offset) {
		final TokenType type = this.ctx.peek(offset);
		return type != null
				&& (type.matches(TokenType.PRIMITIVE_TYPE) || type == TokenType.VOID || type == TokenType.IDENT);
	}

	private int skipType(final int offset) {
		int i = offset + 1;
		if (this.ctx.peek(i, TokenType.LESS)) {
			i = this.skipAngleList(i);
		}
		while (this.ctx.peek(i, TokenType.COLON)) {
			i++;
		}
		if (this.ctx.peek(i, TokenType.DOT) && this.ctx.peek(i + 1, TokenType.DOT)
				&& this.ctx.peek(i + 2, TokenType.DOT)) {
			i += 3;
		}
		return i;
	}

	private int skipGenericNames(final int offset) {
		if (!this.ctx.peek(offset, TokenType.LESS)) {
			return offset;
		}
		return this.skipAngleList(offset);
	}

	private int skipAngleList(final int offset) {
		int depth = 0;
		for (int i = offset; this.ctx.peek(i) != null; i++) {
			if (this.ctx.peek(i, TokenType.LESS)) {
				depth++;
			} else if (this.ctx.peek(i, TokenType.GREATER)) {
				depth--;
				if (depth == 0) {
					return i + 1;
				}
			}
		}
		throw this.ctx.error("Unterminated generic argument list");
	}

	private String tokenTypeName(final TokenType tokenType) {
		if (tokenType != null) {
			switch (tokenType) {
			case BOOLEAN:
				return "bool";
			case VOID:
				return "void";
			case INT:
				return "int";
			case BYTE:
				return "byte";
			case CHAR:
				return "char";
			case SHORT:
				return "short";
			case LONG:
				return "long";
			case FLOAT:
				return "float";
			case DOUBLE:
				return "double";
			default:
				break;
			}
		}
		return tokenType.name().toLowerCase().replace("int_", "int").replace("_s", "_s");
	}
}
