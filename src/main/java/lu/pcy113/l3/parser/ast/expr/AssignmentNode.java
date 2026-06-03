package lu.pcy113.l3.parser.ast.expr;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class AssignmentNode extends Node {

	private final Node target;
	private final Node value;
	private final TokenType operator;

	public AssignmentNode(final Node target, final Node value, final TokenType operator) {
		this.target = target;
		this.value = value;
		this.operator = operator;
	}

	public Node getTarget() {
		return this.target;
	}

	public Node getValue() {
		return this.value;
	}

	public TokenType getOperator() {
		return this.operator;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("target", this.target.toJSONObject()).put("value", this.value.toJSONObject())
				.put("operator", this.operator.name());
	}
}
