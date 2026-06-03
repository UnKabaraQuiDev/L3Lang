package lu.pcy113.l3.parser.ast.expr;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ExpressionStatementNode extends Node {

	private final Node expression;

	public ExpressionStatementNode(final Node expression) {
		this.expression = expression;
	}

	public Node getExpression() {
		return this.expression;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expression", this.expression.toJSONObject());
	}
}
