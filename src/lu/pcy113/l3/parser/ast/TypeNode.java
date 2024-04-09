package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public class TypeNode extends Node {

	private boolean generic = true, array = false;
	private Token token;
	private int arraySize;

	public TypeNode(boolean generic, Token token) {
		this.generic = generic;
		this.token = token;
	}
	
	public TypeNode(boolean generic, Token token, boolean array, int arraySize) {
		this.generic = generic;
		this.token = token;
		this.array = array;
		this.arraySize = arraySize;
	}

	public boolean isVoid() {
		return getIdent().getType().equals(TokenType.VOID);
	}
	
	public boolean isGeneric() {
		return generic;
	}

	public Token getIdent() {
		return token;
	}

	public boolean isArray() {
		return array;
	}

	public int getArraySize() {
		return arraySize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + generic + ", " + token.getType() + ", " + isArray() + ", " + getArraySize() + ")";
	}

}
