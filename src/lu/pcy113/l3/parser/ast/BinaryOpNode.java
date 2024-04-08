package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;

public class BinaryOpNode extends Node {

	private Node left, right;
	private TokenType operator;

	public BinaryOpNode(Node left, TokenType operator, Node right) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

	public TokenType getOperator() {
		return operator;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public void setOperator(TokenType operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + left + operator.getValue() + right + ")";
	}

}
