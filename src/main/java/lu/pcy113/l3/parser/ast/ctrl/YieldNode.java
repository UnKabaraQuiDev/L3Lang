package lu.pcy113.l3.parser.ast.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class YieldNode extends Node {

	private final Node expression;

	public YieldNode(final Node expression) {
		this.expression = expression;
	}

	public Node getExpression() {
		return this.expression;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expression", this.expression == null ? null : this.expression.toJSONObject());
	}
}
