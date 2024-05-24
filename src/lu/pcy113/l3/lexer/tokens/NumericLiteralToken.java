package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ValueType;
import lu.pcy113.l3.utils.BinFormat;
import lu.pcy113.l3.utils.HexFormat;

public class NumericLiteralToken extends LiteralToken<Number> {

	protected String literal;
	protected Number value;
	protected ValueType valueType;

	public NumericLiteralToken(TokenType type, int line, int column, String literal) throws LexerException {
		super(type, line, column);

		literal = literal.trim().replace('_', ' ');

		this.literal = literal;
		if (type.equals(TokenType.DEC_NUM_LIT)) {
			try {
				value = Double.parseDouble(literal);
				valueType = ValueType.DECIMAL;
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		} else if (type.equals(TokenType.HEX_NUM_LIT)) {
			try {
				value = HexFormat.fromHexDigitsToLong(literal);
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		} else if (type.equals(TokenType.BIN_NUM_LIT)) {
			try {
				value = BinFormat.fromBinDigitsToLong(literal);
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		} else if (type.equals(TokenType.CHAR_LIT)) {
			try {
				value = (long) (char) literal.charAt(0);
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		}else if (type.equals(TokenType.NUM_LIT)) {
			try {
				value = Long.parseLong(literal);
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		}
		if (value instanceof Long) {
			if ((long) value <= Byte.MAX_VALUE) {
				valueType = ValueType.INT_8;
			} else if ((long) value <= Short.MAX_VALUE) {
				valueType = ValueType.INT_16;
			} else if ((long) value <= Integer.MAX_VALUE) {
				valueType = ValueType.INT_32;
			} else if ((long) value <= Long.MAX_VALUE) {
				valueType = ValueType.INT_64;
			}
		}
	}

	public String getLiteral() {
		return literal;
	}

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public ValueType getValueType() {
		return valueType;
	}

	@Override
	public String toString() {
		return NumericLiteralToken.class.getName() + "[line=" + line + ", column=" + column + ", type=" + type + ", literal=" + literal + ", value=" + value + "]";
	}

}
