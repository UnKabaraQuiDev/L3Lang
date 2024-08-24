package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class ArrayAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public ArrayAccessNode(IdentifierLitNode ident, ExprNode offset) {
		add(offset);
		this.ident = ident;
	}

	public Node getOffset() {
		return children.get(0);
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.toString() + ", offset=" + getOffset() + ")";
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
