package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class FieldAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public FieldAccessNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isFloat();
	}

	@Override
	public boolean isInt() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isInt();
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
