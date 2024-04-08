package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class ScopeDescriptor {
	
	private IdentifierToken ident;

	public ScopeDescriptor(IdentifierToken ident) {
		this.ident = ident;
	}

	public IdentifierToken getIdent() {
		return ident;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"("+ident.getIdentifier()+" "+ident.getLine()+":"+ident.getColumn()+")";
	}
	
}
