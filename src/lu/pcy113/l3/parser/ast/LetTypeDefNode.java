package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetTypeDefNode extends Node {

	private IdentifierToken ident;
	private boolean iStatic, iArray;
	private int arraySize;

	public LetTypeDefNode(TypeNode type, IdentifierToken ident, boolean iStatic, boolean iArray, int arraySize) {
		add(type);
		this.ident = ident;
		this.iStatic = iStatic;
		this.iArray = iArray;
		this.arraySize = arraySize;
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
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

	public boolean isiArray() {
		return iArray;
	}

	public int getArraySize() {
		return arraySize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getType().getIdent().getType() + " " + ident.getIdentifier() + ")";
	}

}
