package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public class TypeNode extends Node {

	private boolean generic = true, pointer = false;
	private Token token;
	private TokenType type;

	public TypeNode(boolean generic, Token token) {
		this.generic = generic;
		this.token = token;
	}

	public TypeNode(boolean generic, Token token, boolean pointer) {
		this.generic = generic;
		this.token = token;
		this.pointer = pointer;
	}

	public TypeNode(boolean generic, TokenType numLit) {
		this.generic = generic;
		this.type = numLit;
	}

	public TypeNode(boolean generic, TokenType numLit, boolean pointer) {
		this.generic = generic;
		this.type = numLit;
		this.pointer = pointer;
	}

	public int getSize() {
		return 4;
	}

	public boolean isVoid() {
		return getType().equals(TokenType.VOID);
	}

	public TokenType getType() {
		if(type == null && token != null) {
			return token.getType();
		}
		return type;
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
	public boolean equals(Object obj) {
		if(!(obj instanceof TypeNode))
			return false;
		
		TypeNode tn = (TypeNode) obj;
		return tn.generic == generic &&
				tn.pointer == pointer &&
				tn.isVoid() == isVoid() &&
				tn.getType().equals(getType());
	}
	
	@Override
	public String toString() {
		return super.toString() + "(generic=" + generic + ", " + getType().name() + ", pointer=" + isPointer() + ")";
	}

}
