package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class StructScopeDescriptor extends ScopeDescriptor {

	private StructDefNode node;

	public StructScopeDescriptor(IdentifierToken ident, StructDefNode node) {
		super(ident);
		this.node = node;
	}

	public StructDefNode getNode() {
		return node;
	}

}
