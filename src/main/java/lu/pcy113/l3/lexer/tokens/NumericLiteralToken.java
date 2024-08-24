package lu.pcy113.l3.lexer.tokens;

import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ValueType;
import lu.pcy113.l3.utils.BinFormat;
import lu.pcy113.l3.utils.HexFormat;
import lu.pcy113.pclib.PCUtils;

public class NumericLiteralToken extends LiteralToken<Number> {

	protected String literal;
	protected Number value;
	protected ValueType valueType;

	public NumericLiteralToken(TokenType type, int line, int column, String literal) throws LexerException {
		super(type, line, column);

		boolean float_ = literal.endsWith("f");

		literal = PCUtils.replaceLast(literal.trim().replace("_", ""), "f", "");

		this.literal = literal;
		if (float_) {
			try {
				value = Float.parseFloat(literal);
				valueType = ValueType.FLOAT;
			} catch (NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: " + e.getMessage(), line, column, literal);
			}
		} else if (type.equals(TokenType.DEC_NUM_LIT)) {
			try {
				value = Double.parseDouble(literal);
				valueType = ValueType.DOUBLE;
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
		} else if (type.equals(TokenType.NUM_LIT)) {
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

	public boolean isDouble() {
		return valueType.equals(ValueType.DOUBLE);
	}

	public boolean isFloat() {
		return valueType.equals(ValueType.FLOAT);
	}

	public boolean isInteger() {
		return !isDouble() && !isFloat();
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
