package lu.pcy113.l3.lexer;

public enum TokenType {

	LET("let"), FUN("fun"),

	TYPE(), INT(TYPE, "int"),

	INT_1(INT, "int1"), INT_8(INT, "int8"), INT_16(INT, "int16"), INT_32(TYPE, "int32"), INT_64(TYPE, "int64"),

	INT_8_S(INT, "int8s"), INT_16_S(INT, "int16s"), INT_32_S(INT, "int32s"), INT_64_S(INT, "int64s"),

	VOID("void"), NEW("new"),

	NUM_LIT(), DEC_NUM_LIT(NUM_LIT), HEX_NUM_LIT(NUM_LIT), BIN_NUM_LIT(NUM_LIT),

	IDENT(),

	COMMA(','), DOT('.'), COLON(':'), SEMICOLON(';'), DOLLAR('$'),

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

	OR("||"), AND("&&"), NOT('!'), XOR("^^"),

	HASH('#'),

	PLUS('+'), MINUS('-'), MUL('*'), DIV('/'), MODULO('%'),

	ASSIGN('='),

	EQUALS("=="), NOT_EQUALS("!="),

	LESS('<'), LESS_EQUALS("<="),

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
