package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class FunScopeDescriptor extends ScopeDescriptor {

	private FunDefNode node;

	public FunScopeDescriptor(IdentifierLitNode ident, FunDefNode node) {
		super(ident);
		this.node = node;
	}

	public FunDefNode getNode() {
		return node;
	}

}
