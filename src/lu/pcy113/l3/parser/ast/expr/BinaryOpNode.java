package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.Node;

public class BinaryOpNode extends ExprNode implements RecursiveArithmeticOp {

	private TokenType operator;

	public BinaryOpNode(RecursiveArithmeticOp left, TokenType operator, RecursiveArithmeticOp right) {
		add((ExprNode) left);
		add((ExprNode) right);
		this.operator = operator;
	}

	@Override
	public boolean isDecimal() throws L3Exception {
		return ((RecursiveArithmeticOp) getLeft()).isDecimal() || ((RecursiveArithmeticOp) getRight()).isDecimal();
	}
	
	@Override
	public boolean isInteger() throws L3Exception {
		return !isDecimal();
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
