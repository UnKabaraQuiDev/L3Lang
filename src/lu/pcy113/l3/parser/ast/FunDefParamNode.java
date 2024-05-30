package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunDefParamNode extends Node {

	private IdentifierLitNode ident;

	public FunDefParamNode(TypeNode type, IdentifierLitNode ident) {
		add(type);
		this.ident = ident;
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ", " + getType().toString() + ")";
	}

}
