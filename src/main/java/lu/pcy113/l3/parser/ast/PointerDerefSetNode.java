package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public class PointerDerefSetNode extends ExprNode implements RecursiveArithmeticOp {

	public PointerDerefSetNode(PointerDerefNode let, Node expr) {
		add(let);
		add(expr);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return getPointer().getPointerExpr().isDouble();
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return getPointer().getPointerExpr().isFloat();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return getPointer().getPointerExpr().isInteger();
	}

	public PointerDerefNode getPointer() {
		return (PointerDerefNode) children.get(0);
	}

	public ExprNode getExpr() {
		return (ExprNode) children.get(1);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getPointer() + ")";
	}

}
