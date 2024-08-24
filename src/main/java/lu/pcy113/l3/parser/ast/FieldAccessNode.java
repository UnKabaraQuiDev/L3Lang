package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FieldAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public FieldAccessNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public boolean isPrimitive() throws CompilerException {
		return getClosestContainer().getLetDefDescriptor(this).getNode().getType() instanceof PrimitiveTypeNode;
	}

	public boolean isNumber() throws CompilerException {
		final TypeNode type = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		return isPrimitive() && (type instanceof PrimitiveTypeNode || type instanceof PointerTypeNode);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return isNumber() && ((PrimitiveTypeNode) getClosestContainer().getLetDefDescriptor(this).getNode().getType()).isDouble();
	}

	public boolean isFloat() throws CompilerException {
		return isNumber() && ((PrimitiveTypeNode) getClosestContainer().getLetDefDescriptor(this).getNode().getType()).isFloat();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return isNumber() && ((PrimitiveTypeNode) getClosestContainer().getLetDefDescriptor(this).getNode().getType()).isInteger();
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public Node getOffset() {
		return children.get(0);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.toString() + ")";
	}

}
