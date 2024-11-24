package lu.pcy113.l3.parser.ast.math;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class UnaryExpressionNode extends Node {

	private final Node child;
	private final TokenType operator;
	private final boolean prefix;

	public UnaryExpressionNode(Node child, TokenType operator, boolean prefix) {
		this.child = child;
		this.operator = operator;
		this.prefix = prefix;
	}

	public Node getChild() {
		return child;
	}

	public TokenType getOperator() {
		return operator;
	}

	public boolean isPrefix() {
		return prefix;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("child", child.toJSONObject()).put("operator", operator).put("prefix", prefix);
	}

}
