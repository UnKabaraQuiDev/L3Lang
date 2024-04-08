package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;

public class FunArgVarValueNode extends Node {
	
	private Token value;
	
	public FunArgVarValueNode(Token value) {
		this.value = value;
	}
	
	public Token getValue() {
		return value;
	}

}
