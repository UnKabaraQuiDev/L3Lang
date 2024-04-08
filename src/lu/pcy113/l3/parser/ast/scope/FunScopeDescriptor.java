package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class FunScopeDescriptor extends ScopeDescriptor {

	public FunScopeDescriptor(IdentifierToken ident) {
		super(ident);
	}

}
