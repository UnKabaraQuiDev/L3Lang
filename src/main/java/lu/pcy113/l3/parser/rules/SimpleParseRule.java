package lu.pcy113.l3.parser.rules;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import lu.pcy113.l3.parser.ast.abstr.Node;

public final class SimpleParseRule implements ParseRule {

	private final String name;
	private final BiPredicate<ParserContext, RuleParser> matcher;
	private final BiFunction<ParserContext, RuleParser, Node> parser;

	public SimpleParseRule(final String name, final BiPredicate<ParserContext, RuleParser> matcher,
			final BiFunction<ParserContext, RuleParser, Node> parser) {
		this.name = name;
		this.matcher = matcher;
		this.parser = parser;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public boolean matches(final ParserContext ctx, final RuleParser parser) {
		return this.matcher.test(ctx, parser);
	}

	@Override
	public Node parse(final ParserContext ctx, final RuleParser parser) {
		return this.parser.apply(ctx, parser);
	}
}
