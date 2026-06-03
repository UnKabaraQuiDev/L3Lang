package lu.pcy113.l3.parser.rules;

import lu.pcy113.l3.parser.ast.abstr.Node;

public interface ParseRule {

	String name();

	boolean matches(ParserContext ctx, RuleParser parser);

	Node parse(ParserContext ctx, RuleParser parser);
}
