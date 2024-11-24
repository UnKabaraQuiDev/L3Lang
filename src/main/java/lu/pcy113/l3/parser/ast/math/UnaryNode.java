package lu.pcy113.l3.parser.ast.math;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class UnaryNode extends Node {

	private final Node child;
	private final TokenType operator;
	private final boolean prefix;

	public UnaryNode(Node child, TokenType operator, boolean prefix) {
		this.child = child;
		this.operator = operator;
		this.prefix = prefix;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("child", child.toJSONObject()).put("operator", operator).put("prefix", prefix);
	}

}
