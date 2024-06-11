package lu.pcy113.l3.parser.ast.lit;

import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public abstract class NumLitNode<T> extends ExprNode implements RecursiveArithmeticOp {

	public abstract T getValue();
	
}
