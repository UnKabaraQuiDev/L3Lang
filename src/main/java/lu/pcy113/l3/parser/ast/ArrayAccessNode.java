package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public class ArrayAccessNode extends ExprNode implements RecursiveArithmeticOp {


	public ArrayAccessNode(ExprNode expr, ExprNode offset) {
		add(expr);
		add(offset);
	}

	public Node getOffset() {
		return children.get(1);
	}

	public ExprNode getExpr() {
		return (ExprNode) children.get(0);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

}
