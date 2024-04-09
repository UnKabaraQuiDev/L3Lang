package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class VarNumNode extends Node {

	private IdentifierToken ident;

	public VarNumNode(IdentifierToken ident) {
		this.ident = ident;
	}
	public VarNumNode(IdentifierToken ident, Node offset) {
		add(offset);
		this.ident = ident;
	}

	public boolean hasOffset() {
		return children.size() == 1;
	}
	
	public Node getOffset() {
		return children.get(0);
	}
	
	public IdentifierToken getIdent() {
		return ident;
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+ident.getIdentifier()+", "+hasOffset()+")";
	}

}
