package lu.pcy113.l3.lexer;

public enum TokenType {
	
	LET("let"),
	
	NUM_LIT(),
	DEC_NUM_LIT(),
	IDENT(),
	
	SEMICOLON(';'),
	
	PLUS('+'),
	MINUS('-'),
	MUL('*'),
	DIV('/'),
	
	ASSIGN('='),
	
	EQUALS("=="),
	
	LOWER('<'),
	LOWER_EQUALS("<="),
	
	GREATER('>'),
	GREATER_EQUALS(">=");
	
	private boolean fixed = false;
	private boolean string = false;
	private String stringValue;
	private char charValue;
	
	private TokenType() {
		this.fixed = false;
	}
	private TokenType(char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
	}
	private TokenType(String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
	}
	
	public boolean isFixed() {
		return fixed;
	}
	public boolean isString() {
		return string;
	}
	public String getStringValue() {
		return stringValue;
	}
	public char getCharValue() {
		return charValue;
	}
	public Object getValue() {
		return !fixed ? null : (string ? stringValue : charValue);
	}
	
	@Override
	public String toString() {
		if(fixed && string) {
			return TokenType.class.getName()+"[name="+name()+", fixed="+fixed+", string="+string+", stringValue="+stringValue+"]";
		}else if(fixed && !string) {
			return TokenType.class.getName()+"[name="+name()+", fixed="+fixed+", string="+string+", charValue="+charValue+"]";
		}else {
			return TokenType.class.getName()+"["+name()+", fixed="+fixed+", string="+string+"]";
		}
	}
	
}
