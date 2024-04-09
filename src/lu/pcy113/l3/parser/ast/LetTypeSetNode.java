package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetTypeSetNode extends Node {

	private IdentifierToken ident;
	
	public LetTypeSetNode(IdentifierToken ident) {
		this.ident = ident;
	}
	
	public Node getExpr() {
		return children.get(0);
	}
	
	public IdentifierToken getIdent() {
		return ident;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + ident.getIdentifier() + ")";
	}

}
