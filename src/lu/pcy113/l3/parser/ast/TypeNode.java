package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public class TypeNode extends Node {

	private boolean generic = true, pointer = false;
	private Token token;

	public TypeNode(boolean generic, Token token) {
		this.generic = generic;
		this.token = token;
	}
	
	public TypeNode(boolean generic, Token token, boolean pointer) {
		this.generic = generic;
		this.token = token;
		this.pointer = pointer;
	}

	public int getSize() {
		return 4;
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
	public boolean isPointer() {
		return pointer;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(generic=" + generic + ", " + token.getType() + ", pointer=" + isPointer() + ")";
	}

}
