package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.ArrayTypeNode;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FieldAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public FieldAccessNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public boolean isPrimitive() throws CompilerException {
		TypeNode letType = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		return letType instanceof PrimitiveTypeNode /*|| (letType instanceof ArrayTypeNode && ((ArrayTypeNode) letType).getLastSubType() instanceof PrimitiveTypeNode)*/;
	}

	public boolean isNumber() throws CompilerException {
		final TypeNode type = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		return isPrimitive() && (type instanceof PrimitiveTypeNode || type instanceof PointerTypeNode || type instanceof ArrayTypeNode);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return isNumber() && getPrimitiveType().isDouble();
	}

	public boolean isPointer() throws CompilerException {
		TypeNode letType = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		System.err.println("type: " + letType);
		return letType instanceof PointerTypeNode || letType instanceof ArrayTypeNode;
	}

	private PrimitiveTypeNode getPrimitiveType() throws CompilerException {
		TypeNode typeNode = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		if (typeNode instanceof PrimitiveTypeNode) {
			return (PrimitiveTypeNode) typeNode;
		} /*else if (typeNode instanceof ArrayTypeNode && ((ArrayTypeNode) typeNode).getLastSubType() instanceof PrimitiveTypeNode) {
			return (PrimitiveTypeNode) ((ArrayTypeNode) typeNode).getLastSubType();
		}*/
		throw new CompilerException("Type: " + typeNode + " is not primitive.");
	}

	public boolean isFloat() throws CompilerException {
		return isNumber() && getPrimitiveType().isFloat();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return isNumber() && getPrimitiveType().isInteger();
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
