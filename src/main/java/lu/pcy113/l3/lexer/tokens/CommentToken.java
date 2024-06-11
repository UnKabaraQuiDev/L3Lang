package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.TokenType;

public class CommentToken extends Token {
	
	private String value;
	
	public CommentToken(TokenType _t, int _l, int _c, String value) {
		super(_t, _l, _c);
		this.value = value;
	}
	
	public String getValue() {return value;}
	
	@Override
	public String toString() {
		return CommentToken.class.getName()+"[line="+line+", column="+column+", type="+type+", value="+value+"]";
	}

}
