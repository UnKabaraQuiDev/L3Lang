package lu.pcy113.l3.parser.ast.math;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class UnaryExpressionNode extends Node {

	private final Node child;
	private final TokenType operator;
	private final boolean prefix;

	public UnaryExpressionNode(final Node child, final TokenType operator, final boolean prefix) {
		this.child = child;
		this.operator = operator;
		this.prefix = prefix;
	}

	public Node getChild() {
		return this.child;
	}

	public TokenType getOperator() {
		return this.operator;
	}

	public boolean isPrefix() {
		return this.prefix;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("child", this.child.toJSONObject()).put("operator", this.operator).put("prefix",
				this.prefix);
	}

}
