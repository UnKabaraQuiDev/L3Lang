package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class VarNumNode extends Node {

	private IdentifierToken ident;

	public VarNumNode(IdentifierToken ident) {
		this.ident = ident;
	}

	public IdentifierToken getIdent() {
		return ident;
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+ident.getIdentifier()+")";
	}

}
