package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class StructScopeDescriptor extends ScopeDescriptor {

	private StructDefNode node;

	public StructScopeDescriptor(IdentifierLitNode ident, StructDefNode node) {
		super(ident);
		this.node = node;
	}

	public StructDefNode getNode() {
		return node;
	}

	public void setNode(StructDefNode node) {
		this.node = node;
	}
	
}