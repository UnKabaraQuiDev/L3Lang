package lu.pcy113.l3.lexer;

public enum TokenType {
	
	VAR_1("var1"),
	VAR_8("var8"),
	VAR_16("var16"),
	VAR_32("var32"),
	VAR_64("var64"),
	
	VAR_8_S("var8s"),
	VAR_16_S("var16s"),
	VAR_32_S("var32s"),
	VAR_64_S("var64s"),
	
	VOID("void"),
	
	NUM_LIT(),
	DEC_NUM_LIT(),
	HEX_NUM_LIT(),
	BIN_NUM_LIT(),
	
	IDENT(),
	
	COMMA(','),
	SEMICOLON(';'),
	
	PAREN_OPEN('('),
	PAREN_CLOSE(')'),
	BRACKET_OPEN('['),
	BRACKET_CLOSE(']'),
	CURLY_OPEN('{'),
	CURLY_CLOSE('}'),
	
	//SINGLE_QUOTE('\''),
	//DOUBLE_QUOTE('\"'),
	
	STRING(),
	
	COMMENT("//"),
	
	TRUE("true"),
	FALSE("false"),
	
	IF("if"),
	ELSE("else"),
	
	SWITCH("switch"),
	CASE("case"),
	DEFAULT("default"),
	
	FOR("for"),
	WHILE("while"),
	BREAK("break"),
	
	RETURN("return"),
	DO("do"),
	FINALLY("finally"),
	
	BIT_OR('|'),
	BIT_AND('&'),
	BIT_XOR('^'),
	BIT_NOT('~'),
	
	OR("||"),
	AND("&&"),
	NOT('!'),
	
	PLUS('+'),
	MINUS('-'),
	MUL('*'),
	DIV('/'),
	MODULO('%'),
	
	ASSIGN('='),
	
	EQUALS("=="),
	NOT_EQUALS("!="),
	
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
		return !fixed ? name() : (string ? stringValue : charValue);
	}
	
	@Override
	public String toString() {
		if(fixed && string) {
			return TokenType.class.getName()+"["+name()+", fixed="+fixed+", string="+string+", stringValue="+stringValue+"]";
		}else if(fixed && !string) {
			return TokenType.class.getName()+"["+name()+", fixed="+fixed+", string="+string+", charValue="+charValue+"]";
		}else {
			return TokenType.class.getName()+"["+name()+", fixed="+fixed+", string="+string+"]";
		}
	}
	
}
