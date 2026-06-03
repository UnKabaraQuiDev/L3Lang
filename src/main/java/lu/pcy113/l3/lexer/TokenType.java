package lu.pcy113.l3.lexer;

public enum TokenType {

	LET("let"), FUN("fun"),

	RETURN("return"),

	IMPORT("import"), PACKAGE("package"), AS("as"),

	CLASS("class"), INTERFACE("interface"), STRUCT("struct"), UNION("union"),

	TYPE(), USER_TYPE(TYPE), PRIMITIVE_TYPE(TYPE),

	INT(PRIMITIVE_TYPE, "int"),

	INT_1(INT, "int1"), INT_8(INT, "int8"), INT_16(INT, "int16"), INT_32(INT, "int32"), INT_64(INT, "int64"),
	INT_128(INT, "int128"),

	INT_8_S(INT, "int8s"), INT_16_S(INT, "int16s"), INT_32_S(INT, "int32s"), INT_64_S(INT, "int64s"),
	INT_128_S(INT, "int128s"),

	BYTE(INT_8, "byte"), CHAR(BYTE, "char"), SHORT(INT_16, "short"), LONG(INT_64, "long"),

	FLOAT(PRIMITIVE_TYPE, "float"),

	FLOAT_32(FLOAT, "float32"), /* FLOAT_128(FLOAT, "float128"), */

	DOUBLE(PRIMITIVE_TYPE, "double"),

	BOOLEAN(PRIMITIVE_TYPE, "bool"),

	TRUE("true"), FALSE("false"),

	VOID(PRIMITIVE_TYPE, "void"), NEW("new"),

	NUM_LIT(), DEC_NUM_LIT(NUM_LIT), HEX_NUM_LIT(NUM_LIT), BIN_NUM_LIT(NUM_LIT), CHAR_LIT(NUM_LIT),

	IDENT(),

	COMMA(','), DOT('.'), COLON(':'), SEMICOLON(';'), DOLLAR('$'),

	ARROW("->"),

	PAREN_OPEN('('), PAREN_CLOSE(')'), BRACKET_OPEN('['), BRACKET_CLOSE(']'), CURLY_OPEN('{'), CURLY_CLOSE('}'),

	STRING_LIT(),

	COMMENT("//"),

	IF("if"), ELSE("else"),

	STATIC("static"),

	SWITCH("switch"), CASE("case"), DEFAULT("default"),

	FOR("for"), WHILE("while"), BREAK("break"),

	DO("do"), FINALLY("finally"),

	GOTO("goto"), YIELD("yield"),

	MATH_OP(),

	ASSIGN(MATH_OP), STRICT_ASSIGN(ASSIGN, '='),

	BIT_OR(MATH_OP, '|'), BIT_AND(MATH_OP, '&'), BIT_XOR(MATH_OP, '^'), BIT_NOT(MATH_OP, '~'),

	BIT_OR_ASSIGN(ASSIGN, "|="), BIT_AND_ASSIGN(ASSIGN, "&="), BIT_XOR_ASSIGN(ASSIGN, "^="),
	BIT_NOT_ASSIGN(ASSIGN, "~="),

	OR(MATH_OP, "||"), AND(MATH_OP, "&&"), NOT(MATH_OP, '!'), XOR(MATH_OP, "^^"),

	BIT_SHIFT(MATH_OP),

	BIT_SHIFT_LEFT(BIT_SHIFT, "<<"), BIT_SHIFT_SIGNED_RIGHT(BIT_SHIFT, ">>"),
	BIT_SHIFT_UNSIGNED_RIGHT(BIT_SHIFT, ">>>"),

	HASH('#'),

	PLUS(MATH_OP, '+'), MINUS(MATH_OP, '-'), MUL(MATH_OP, '*'), DIV(MATH_OP, '/'), MODULO(MATH_OP, '%'),

	PLUS_PLUS(MATH_OP, "++"), MINUS_MINUS(MATH_OP, "--"),

	PLUS_ASSIGN(ASSIGN, "+="), MINUS_ASSIGN(ASSIGN, "-="), MUL_ASSIGN(ASSIGN, "*="), DIV_ASSIGN(ASSIGN, "/="),
	MODULO_ASSIGN(ASSIGN, "%="),

	COMPARAISON(MATH_OP),

	EQUALS(COMPARAISON, "=="), NOT_EQUALS(COMPARAISON, "!="),

	LESS(COMPARAISON, '<'), LESS_EQUALS(COMPARAISON, "<="),

	GREATER(COMPARAISON, '>'), GREATER_EQUALS(COMPARAISON, ">=");

	private TokenType parent;
	private boolean fixed = false;
	private boolean string = false;
	private String stringValue;
	private char charValue;

	TokenType() {
		this.fixed = false;
	}

	TokenType(final TokenType parent) {
		this.fixed = false;
		this.parent = parent;
	}

	TokenType(final char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
	}

	TokenType(final TokenType parent, final char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
		this.parent = parent;
	}

	TokenType(final String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
	}

	TokenType(final TokenType parent, final String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
		this.parent = parent;
	}

	public boolean matches(final TokenType type) {
		return this.equals(type) || (this.parent != null ? this.parent.matches(type) : false);
	}

	public boolean isFixed() {
		return this.fixed;
	}

	public boolean isString() {
		return this.string;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public char getCharValue() {
		return this.charValue;
	}

	public Object getValue() {
		return !this.fixed ? this.name() : this.string ? this.stringValue : this.charValue;
	}

	@Override
	public String toString() {
		if (this.fixed && this.string) {
			return TokenType.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + ", stringValue=" + this.stringValue + "]";
		} else if (this.fixed && !this.string) {
			return TokenType.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + ", charValue=" + this.charValue + "]";
		} else {
			return TokenType.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + "]";
		}
	}

	public String toShortString() {
		return TokenType.class.getSimpleName() + "[" + this.name() + "]";
	}

}
