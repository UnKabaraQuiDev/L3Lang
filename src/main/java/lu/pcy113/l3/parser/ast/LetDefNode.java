package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class LetDefNode extends Node {

	private IdentifierLitNode ident;
	private boolean iStatic, allocated = false;

	public LetDefNode(TypeNode type, IdentifierLitNode ident, boolean iStatic) {
		add(type);
		this.ident = ident;
		this.iStatic = iStatic;
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public boolean hasExpr() {
		return children.size() >= 2;
	}

	public Node getExpr() {
		return children.get(1);
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public boolean isiStatic() {
		return iStatic;
	}

	public boolean isAllocated() {
		return allocated;
	}

	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ", " + getType().toString() + ")";
	}

}
