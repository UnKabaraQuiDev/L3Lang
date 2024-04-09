package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.StringLiteralToken;

public class StringLitNode extends Node {

	private StringLiteralToken string;
	
	public StringLitNode(StringLiteralToken stringToken) {
		this.string = stringToken;
	}
	
	public StringLiteralToken getString() {
		return string;
	}

}
