package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class UserTypeNode extends TypeNode {

	private IdentifierLitNode ident;

	public UserTypeNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public IdentifierLitNode getIdentifier() {
		return ident;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ")";
	}

}
