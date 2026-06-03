package lu.pcy113.l3.parser.ast.fun.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ReturnNode extends Node {

	private Node expression;

	public ReturnNode(final Node expression) {
		this.expression = expression;
	}

	public ReturnNode() {
	}

	public Node getExpression() {
		return this.expression;
	}

	public boolean hasExpression() {
		return this.expression != null;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expression", this.expression == null ? null : this.expression.toJSONObject());
	}

}
