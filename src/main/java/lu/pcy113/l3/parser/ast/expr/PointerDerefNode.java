package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;

public class PointerDerefNode extends ExprNode implements RecursiveArithmeticOp {

	public PointerDerefNode(ExprNode node) {
		add(node);
	}

	public ExprNode getPointerExpr() {
		return (ExprNode) children.get(0);
	}

	public boolean hasExpr() {
		return children.size() > 1;
	}

	public ExprNode getExpr() {
		return (ExprNode) children.get(1);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return getPointerExpr().isDouble();
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return getPointerExpr().isFloat();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return getPointerExpr().isInteger();
	}

}
