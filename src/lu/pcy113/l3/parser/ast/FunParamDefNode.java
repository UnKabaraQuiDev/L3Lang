package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunParamDefNode extends Node {

	private IdentifierLitNode ident;

	public FunParamDefNode(TypeNode type, IdentifierLitNode ident) {
		add(type);
		this.ident = ident;
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

	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ", " + getType().toString() + ")";
	}

}
