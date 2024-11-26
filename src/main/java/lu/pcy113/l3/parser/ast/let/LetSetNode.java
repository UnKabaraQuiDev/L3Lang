package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class LetSetNode extends Node {

	private final Node parent;
	private final Node expr;
	private final TokenType assignType;

	public LetSetNode(Node parent, Node expr, TokenType assignType) {
		this.parent = parent;
		this.expr = expr;
		this.assignType = assignType;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expr", expr.toJSONObject()).put("assignType", assignType.name()).put("parent", parent.toJSONObject());
	}

}
