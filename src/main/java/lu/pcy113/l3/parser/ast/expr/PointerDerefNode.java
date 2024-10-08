package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.FieldAccessNode;

public class PointerDerefNode extends ExprNode implements RecursiveArithmeticOp {

	public PointerDerefNode(FieldAccessNode node) {
		add(node);
	}

	public FieldAccessNode getExpr() {
		return (FieldAccessNode) children.get(0);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return getExpr().isDouble();
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return getExpr().isFloat();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return getExpr().isInteger();
	}

}
