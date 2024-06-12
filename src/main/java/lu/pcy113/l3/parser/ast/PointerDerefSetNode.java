package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class PointerDerefSetNode extends ExprNode implements RecursiveArithmeticOp {

	public PointerDerefSetNode(PointerDerefNode let, Node expr) {
		add(let);
		add(expr);
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		TypeNode type = getClosestContainer().getLetDefDescriptor(this.getPointer().getExpr()).getNode().getType();
		return type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) type).isDecimal();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		TypeNode type = getClosestContainer().getLetDefDescriptor(this.getPointer().getExpr()).getNode().getType();
		return type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) type).isInteger();
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
