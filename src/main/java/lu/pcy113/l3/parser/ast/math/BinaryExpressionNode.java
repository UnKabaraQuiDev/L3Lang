package lu.pcy113.l3.parser.ast.math;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class BinaryExpressionNode extends Node {

	private TokenType operator;
	private Node left, right;

	public BinaryExpressionNode(Node left, TokenType operator, Node right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public TokenType getOperator() {
		return operator;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("operator", operator).put("left", left.toJSONObject()).put("right", right.toJSONObject());
	}

}
