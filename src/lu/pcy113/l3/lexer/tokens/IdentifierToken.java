package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.TokenType;

public class IdentifierToken extends Token {
	
	protected String identifier;
	
	public IdentifierToken(TokenType type, int line, int column, String strValue) {
		super(type, line, column);
		this.identifier = strValue;
	}
	
	public String getIdentifier() {return identifier;}
	
	@Override
	public String toString() {
		return IdentifierToken.class.getName()+"[line="+line+", column="+column+", type="+type+", identifier="+identifier+"]";
	}
	
}
