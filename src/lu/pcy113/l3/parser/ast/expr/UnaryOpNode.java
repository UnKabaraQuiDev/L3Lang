package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.Node;

public class UnaryOpNode extends ExprNode implements RecursiveArithmeticOp {

	private TokenType operator;
	private boolean prefix = false;

	public UnaryOpNode(TokenType operator, ExprNode node) {
		add(node);
		this.operator = operator;
	}

	public UnaryOpNode(TokenType operator, ExprNode node, boolean prefix) {
		add(node);
		this.operator = operator;
		this.prefix = prefix;
	}

	@Override
	public boolean isFloat() throws L3Exception {
		return ((RecursiveArithmeticOp) getExpr()).isFloat();
	}

	@Override
	public boolean isInt() throws L3Exception {
		return !isFloat();
	}

	public boolean isPrefix() {
		return prefix;
	}

	public boolean isPostfix() {
		return !prefix;
	}

	public Node getExpr() {
		return children.get(0);
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
		return super.toString() + "(" + operator.getValue() + ", " + (prefix ? "pre" : "post") + ")";
	}

}
