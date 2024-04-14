package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetTypeDefNode extends Node {

	private IdentifierToken ident;
	private boolean iStatic, arg;
	private int letIndex;

	public LetTypeDefNode(int letIndex, TypeNode type, IdentifierToken ident, boolean iStatic, boolean arg) {
		add(type);
		this.letIndex = letIndex;
		this.ident = ident;
		this.iStatic = iStatic;
		this.arg = arg;
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public boolean hasExpr() {
		return children.size() >= 2;
	}

	public Node getExpr() {
		return children.get(1);
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public boolean isiStatic() {
		return iStatic;
	}

	public int getLetIndex() {
		return letIndex;
	}
	
	public void setLetIndex(int letIndex) {
		this.letIndex = letIndex;
	}

	public boolean isArg() {
		return arg;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + getType().toString() + ", " + ident.getIdentifier() + ", index=" + letIndex + ")";
	}


}
