package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;

public class TypeNode extends Node {

	private boolean generic = true;
	private Token token;

	public TypeNode(boolean generic, Token token) {
		this.generic = generic;
		this.token = token;
	}

	public boolean isGeneric() {
		return generic;
	}

	public Token getIdent() {
		return token;
	}

}
