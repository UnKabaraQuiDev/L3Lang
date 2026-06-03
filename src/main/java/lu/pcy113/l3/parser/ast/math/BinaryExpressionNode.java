package lu.pcy113.l3.parser.ast.math;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class BinaryExpressionNode extends Node {

	private final TokenType operator;
	private final Node left, right;

	public BinaryExpressionNode(final Node left, final TokenType operator, final Node right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public TokenType getOperator() {
		return this.operator;
	}

	public Node getLeft() {
		return this.left;
	}

	public Node getRight() {
		return this.right;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("operator", this.operator).put("left", this.left.toJSONObject()).put("right",
				this.right.toJSONObject());
	}

}
