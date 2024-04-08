package lu.pcy113.l3.lexer;

public enum TokenType {

	LET("let"), FUN("fun"),

	TYPE(), TYPE_1(TYPE, "t1"), TYPE_8(TYPE, "t8"), TYPE_16(TYPE, "t16"), TYPE_32(TYPE, "t32"), TYPE_64(TYPE, "t64"),

	TYPE_8_S(TYPE, "t8s"), TYPE_16_S(TYPE, "t16s"), TYPE_32_S(TYPE, "t32s"), TYPE_64_S(TYPE, "t64s"),

	VOID("void"), NEW("new"),

	NUM_LIT(), DEC_NUM_LIT(NUM_LIT), HEX_NUM_LIT(NUM_LIT), BIN_NUM_LIT(NUM_LIT),

	IDENT(),

	COMMA(','), DOT('.'), SEMICOLON(';'),

	ARROW("->"),

	PAREN_OPEN('('), PAREN_CLOSE(')'), BRACKET_OPEN('['), BRACKET_CLOSE(']'), CURLY_OPEN('{'), CURLY_CLOSE('}'),

	STRING(),

	COMMENT("//"),

	TRUE("true"), FALSE("false"),

	IF("if"), ELSE("else"),
	
	STATIC("static"),
	
	SWITCH("switch"), CASE("case"), DEFAULT("default"),

	FOR("for"), WHILE("while"), BREAK("break"),

	RETURN("return"), DO("do"), FINALLY("finally"),

	BIT_OR('|'), BIT_AND('&'), BIT_XOR('^'), BIT_NOT('~'),

	OR("||"), AND("&&"), NOT('!'),
	
	HASH('#'),

	PLUS('+'), MINUS('-'), MUL('*'), DIV('/'), MODULO('%'),

	ASSIGN('='),

	EQUALS("=="), NOT_EQUALS("!="),

	LOWER('<'), LOWER_EQUALS("<="),

	GREATER('>'), GREATER_EQUALS(">=");

	private TokenType parent;
	private boolean fixed = false;
	private boolean string = false;
	private String stringValue;
	private char charValue;

	private TokenType() {
		this.fixed = false;
	}

	private TokenType(TokenType parent) {
		this.fixed = false;
		this.parent = parent;
	}

	private TokenType(char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
	}

	private TokenType(TokenType parent, char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
		this.parent = parent;
	}

	private TokenType(String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
	}

	private TokenType(TokenType parent, String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
		this.parent = parent;
	}

	public boolean softEquals(TokenType type) {
		return this.equals(type) || (parent != null ? parent.softEquals(type) : false);
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
		if (fixed && string) {
			return TokenType.class.getName() + "[" + name() + ", fixed=" + fixed + ", string=" + string + ", stringValue=" + stringValue + "]";
		} else if (fixed && !string) {
			return TokenType.class.getName() + "[" + name() + ", fixed=" + fixed + ", string=" + string + ", charValue=" + charValue + "]";
		} else {
			return TokenType.class.getName() + "[" + name() + ", fixed=" + fixed + ", string=" + string + "]";
		}
	}

}
