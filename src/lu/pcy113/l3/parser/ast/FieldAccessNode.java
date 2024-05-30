package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class FieldAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public FieldAccessNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isDecimal();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isInteger();
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public Node getOffset() {
		return children.get(0);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.toString()  + ")";
	}

}
