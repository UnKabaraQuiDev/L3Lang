package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.TokenType;

public class Token {
	
	protected int line, column;
	protected TokenType type;
	
	public Token(TokenType _t, int _l, int _c) {
		this.type = _t;
		this.line = _l;
		this.column = _c;
	}
	
	public int getColumn() {return column;}
	public int getLine() {return line;}
	public TokenType getType() {return type;}
	
	@Override
	public String toString() {
		return Token.class.getName()+"[line="+line+", column="+column+", type="+type+"]";
	}
	
}
