package lu.pcy113.l3.parser.ast.pointer;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class PointerDerefNode extends Node {

	private Node expression;

	public PointerDerefNode(Node expression) {
		this.expression = expression;
	}

	public Node getExpression() {
		return expression;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expression", expression.toJSONObject());
	}

}
