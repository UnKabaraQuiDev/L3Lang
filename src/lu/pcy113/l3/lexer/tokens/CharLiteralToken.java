package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ValueType;

@Deprecated
public class CharLiteralToken extends LiteralToken<Character> {

	protected Character value;

	public CharLiteralToken(TokenType type, int line, int column, Character value) {
		super(type, line, column);
		this.value = value;
	}

	@Override
	public Character getValue() {
		return value;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.CHAR;
	}

	@Override
	public String toString() {
		return StringLiteralToken.class.getName() + "[line=" + line + ", column=" + column + ", type=" + type + ", value=" + value + "]";
	}

}
