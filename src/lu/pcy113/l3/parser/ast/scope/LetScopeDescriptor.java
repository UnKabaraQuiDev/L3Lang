package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetScopeDescriptor extends ScopeDescriptor {

	public LetScopeDescriptor(IdentifierToken ident) {
		super(ident);
	}

}
