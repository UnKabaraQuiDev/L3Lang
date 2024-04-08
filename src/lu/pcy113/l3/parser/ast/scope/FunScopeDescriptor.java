package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class FunScopeDescriptor extends ScopeDescriptor {

	private FunDefNode node;

	public FunScopeDescriptor(IdentifierToken ident, FunDefNode node) {
		super(ident);
		this.node = node;
	}

	public FunDefNode getNode() {
		return node;
	}

}
