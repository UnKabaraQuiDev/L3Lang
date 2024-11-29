package lu.pcy113.l3.parser.ast.fun.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ReturnNode extends Node {

	private Node expression;
	
	public ReturnNode(Node expression) {
		this.expression = expression;
	}
	
	public ReturnNode() {
	}

	public Node getExpression() {
		return expression;
	}
	
	public boolean hasExpression() {
		return expression != null;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expression", expression == null ? null : expression.toJSONObject());
	}

}
