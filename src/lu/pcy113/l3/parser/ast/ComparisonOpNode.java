package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;

public class ComparisonOpNode extends Node {

	private TokenType operator;

	public ComparisonOpNode(Node left, TokenType operator, Node right) {
		add(left);
		add(right);
		this.operator = operator;
	}

	public Node getLeft() {
		return children.get(0);
	}

	public Node getRight() {
		return children.get(1);
	}

	public TokenType getOperator() {
		return operator;
	}

	public void setLeft(Node left) {
		children.set(0, left);
	}

	public void setRight(Node right) {
		children.set(1, right);
	}

	public void setOperator(TokenType operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + operator.getValue() + ")";
	}

}
