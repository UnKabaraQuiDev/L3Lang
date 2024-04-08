package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class LetTypeDefNode extends Node {

	private Token type;
	private IdentifierToken ident;
	private boolean iStatic, iArray;
	private int arraySize;

	public LetTypeDefNode(Token type, IdentifierToken ident, boolean iStatic, boolean iArray, int arraySize) {
		this.type = type;
		this.ident = ident;
		this.iStatic = iStatic;
		this.iArray = iArray;
		this.arraySize = arraySize;
	}

	public Token getType() {
		return type;
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
		return super.toString() + "(" + type.getType().getValue() + " " + ident.getIdentifier() + ")";
	}

}
