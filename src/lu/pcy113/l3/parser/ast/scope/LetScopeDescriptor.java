package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.LetDefNode;

public class LetScopeDescriptor extends ScopeDescriptor {

	private LetDefNode node;

	public LetScopeDescriptor(IdentifierToken ident, LetDefNode node) {
		super(ident);
		this.node = node;
	}

	public LetDefNode getNode() {
		return node;
	}
	
}
