package lu.pcy113.l3.lexer;

public class NumericLiteralToken extends Token {
	
	protected String literal;
	protected Object value;
	
	public NumericLiteralToken(TokenType type, int line, int column, String literal) {
		super(type, line, column);
		
		literal = literal.trim();
		this.literal = literal;
		if(type.equals(TokenType.DEC_NUM_LIT))
			value = Double.parseDouble(literal);
		else
			value = Long.parseLong(literal);
	}
	
	public String getLiteral() {return literal;}
	public Object getValue() {return value;}
	
	@Override
	public String toString() {
		return NumericLiteralToken.class.getName()+"[line="+line+", column="+column+", type="+type+", literal="+literal+", value="+value+"]";
	}
	
}
