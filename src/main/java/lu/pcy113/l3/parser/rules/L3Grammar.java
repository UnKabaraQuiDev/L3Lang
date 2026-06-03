package lu.pcy113.l3.parser.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lu.pcy113.l3.lexer.TokenType;

public final class L3Grammar {

	private final List<ParseRule> topLevelRules = new ArrayList<>();
	private final List<ParseRule> statementRules = new ArrayList<>();

	public L3Grammar() {
		this.topLevelRules.add(new SimpleParseRule("import", (ctx, parser) -> ctx.peek(TokenType.IMPORT),
				(ctx, parser) -> parser.parseImport()));
		this.topLevelRules.add(new SimpleParseRule("preprocessor-directive", (ctx, parser) -> ctx.peek(TokenType.HASH),
				(ctx, parser) -> parser.parsePreprocessorDirective()));
		this.topLevelRules.add(new SimpleParseRule("method", (ctx, parser) -> parser.looksLikeMethodHeader(0),
				(ctx, parser) -> parser.parseMethodDefinition()));
		this.topLevelRules.add(
				new SimpleParseRule("variable-declaration", (ctx, parser) -> parser.looksLikeVariableDeclaration(0),
						(ctx, parser) -> parser.parseVariableDeclaration(true)));

		this.statementRules.add(new SimpleParseRule("block", (ctx, parser) -> ctx.peek(TokenType.CURLY_OPEN),
				(ctx, parser) -> parser.parseBlock(false)));
		this.statementRules.add(
				new SimpleParseRule("if", (ctx, parser) -> ctx.peek(TokenType.IF), (ctx, parser) -> parser.parseIf()));
		this.statementRules.add(new SimpleParseRule("while", (ctx, parser) -> ctx.peek(TokenType.WHILE),
				(ctx, parser) -> parser.parseWhile()));
		this.statementRules.add(new SimpleParseRule("for", (ctx, parser) -> ctx.peek(TokenType.FOR),
				(ctx, parser) -> parser.parseFor()));
		this.statementRules.add(new SimpleParseRule("return", (ctx, parser) -> ctx.peek(TokenType.RETURN),
				(ctx, parser) -> parser.parseReturn()));
		this.statementRules.add(
				new SimpleParseRule("yield", (ctx, parser) -> ctx.peek(TokenType.YIELD) || ctx.peekIdentifier("yield"),
						(ctx, parser) -> parser.parseYield()));
		this.statementRules.add(
				new SimpleParseRule("break", (ctx, parser) -> ctx.peek(TokenType.BREAK) || ctx.peekIdentifier("break"),
						(ctx, parser) -> parser.parseBreakOrBreakpoint()));
		this.statementRules.add(new SimpleParseRule("preprocessor-directive", (ctx, parser) -> ctx.peek(TokenType.HASH),
				(ctx, parser) -> parser.parsePreprocessorDirective()));
		this.statementRules.add(
				new SimpleParseRule("variable-declaration", (ctx, parser) -> parser.looksLikeVariableDeclaration(0),
						(ctx, parser) -> parser.parseVariableDeclaration(true)));
		this.statementRules.add(new SimpleParseRule("expression-statement", (ctx, parser) -> true,
				(ctx, parser) -> parser.parseExpressionStatement(true)));
	}

	public List<ParseRule> topLevelRules() {
		return Collections.unmodifiableList(this.topLevelRules);
	}

	public List<ParseRule> statementRules() {
		return Collections.unmodifiableList(this.statementRules);
	}
}
