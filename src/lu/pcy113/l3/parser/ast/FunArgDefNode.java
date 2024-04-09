package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class FunArgDefNode extends Node {

	private int argIndex;
	private IdentifierToken ident;

	public FunArgDefNode(int argIndex, TypeNode type, IdentifierToken ident) {
		add(type);
		this.ident = ident;
		this.argIndex = argIndex;
	}

	public int getArgIndex() {
		return argIndex;
	}
	
	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return super.toString()+"("+argIndex+", "+ident+")";
	}
}
