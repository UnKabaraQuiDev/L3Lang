package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;

public class PackageDefNode extends Node {

	private Token token;
	private String value;

	public PackageDefNode(Token token, String value) {
		this.token = token;
		this.value = value;
	}

	public Token getToken() {
		return token;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString()+"('"+value+"' "+token.getPosition()+")";
	}

}
