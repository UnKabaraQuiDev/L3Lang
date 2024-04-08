package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;

public class LetScopeDescriptor extends ScopeDescriptor {

	private LetTypeDefNode node;

	public LetScopeDescriptor(IdentifierToken ident, LetTypeDefNode node) {
		super(ident);
		this.node = node;
	}

	public LetTypeDefNode getNode() {
		return node;
	}

}
