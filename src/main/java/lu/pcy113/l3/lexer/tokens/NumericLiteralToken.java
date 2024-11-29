package lu.pcy113.l3.lexer.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;

public class NumericLiteralToken extends LiteralToken {

	public static enum NumericValueType {
		//@formatter:off
		BOOL_1("bool", 1, TokenType.BOOLEAN),
		FLOAT_64("double", 8, TokenType.DOUBLE),
		FLOAT_32("float", 4, TokenType.FLOAT),
		INT_128("int128", 16, TokenType.INT_128),
		INT_64("int64", 8, TokenType.INT_64),
		INT_32("int32", 4, TokenType.INT_32),
		INT_16("int16", 2, TokenType.INT_16),
		CHAR("char", 1, TokenType.CHAR),
		INT_8("int8", 1, TokenType.INT_8);
		//@formatter:on
		
		private final String name;
		private final int bytes;
		private final TokenType tt;

		private NumericValueType(String name, int bytes, TokenType tt) {
			this.name = name;
			this.bytes = bytes;
			this.tt = tt;
		}

		public String getName() {
			return name;
		}

		public int getBytes() {
			return bytes;
		}
		
		public TokenType getTokenType() {
			return tt;
		}
		
		public static NumericValueType byTokenType(TokenType tt) {
			for (NumericValueType v : NumericValueType.values()) {
				if (v.tt == tt) {
					return v;
				}
			}
			return null;
		}

	}

	protected String literal;
	protected Object value;
	protected NumericValueType valueType;

	public NumericLiteralToken(TokenType type, int line, int column, String literal, NumericValueType valueType, Object value) {
		super(TokenType.NUM_LIT, line, column);
		this.valueType = valueType;
		this.value = value;
		this.literal = literal;
	}

	public static NumericLiteralToken parseNumeric(TokenType tokenType, int line, int column, String literal) {
		// Regex patterns for various numeric formats
		String intPattern = "^(\\d+)([bBsSlLdDfF]{0,2})$";
		String floatPattern = "^(\\d+\\.?\\d+)([dDfF]?)$";
		String boolPattern = "^(true|false)$";

		Matcher matcher;
		if (literal.matches(boolPattern)) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.BOOL_1, Boolean.parseBoolean(literal));
		}

		switch (tokenType) {
		case NUM_LIT:
			matcher = Pattern.compile(intPattern).matcher(literal);
			if (matcher.matches()) {
				String number = matcher.group(1);
				String suffix = matcher.group(2).toLowerCase();
				return parseInteger(tokenType, line, column, literal, number, suffix);
			}
			break;

		case DEC_NUM_LIT:
			matcher = Pattern.compile(floatPattern).matcher(literal);
			if (matcher.matches()) {
				String number = matcher.group(1);
				String suffix = matcher.group(2).toLowerCase();
				if (suffix.equals("f")) {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_32, Float.parseFloat(number));
				} else {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_64, Double.parseDouble(number));
				}
			}
			break;

		case HEX_NUM_LIT:
			if (literal.startsWith("0x")) {
				String number = literal.substring(2);
				long parsedValue = Long.parseUnsignedLong(number, 16);
				return parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;

		case BIN_NUM_LIT:
			if (literal.startsWith("0b")) {
				String number = literal.substring(2);
				long parsedValue = Long.parseUnsignedLong(number, 2);
				return parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;
		}

		throw new IllegalArgumentException("Invalid numeric literal: " + literal + " for type " + tokenType);
	}

	private static NumericLiteralToken parseInteger(TokenType tokenType, int line, int column, String literal, String number, String suffix) {
		switch (suffix) {
		case "b":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_8, Byte.parseByte(number));
		case "s":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_16, Short.parseShort(number));
		case "l":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64, Long.parseLong(number));
		case "ll":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128, new java.math.BigInteger(number));
		case "d":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_64, Double.parseDouble(number));
		case "f":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_32, Float.parseFloat(number));
		default:
			// Default to INT_32 unless it's too large
			long longValue = Long.parseLong(number);
			if (longValue <= Integer.MAX_VALUE) {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_32, (int) longValue);
			} else if (longValue <= Long.MAX_VALUE) {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64, longValue);
			} else {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128, new java.math.BigInteger(number));
			}
		}
	}

	private static NumericLiteralToken parseHexOrBinValue(TokenType tokenType, int line, int column, String literal, long value) {
		if (value <= Byte.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_8, (byte) value);
		} else if (value <= Short.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_16, (short) value);
		} else if (value <= Integer.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_32, (int) value);
		} else if (value <= Long.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64, value);
		} else {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128, new java.math.BigInteger(String.valueOf(value)));
		}
	}

	public String getLiteral() {
		return literal;
	}

	public boolean isDouble() {
		return valueType.equals(NumericValueType.FLOAT_64);
	}

	public boolean isFloat() {
		return valueType.equals(NumericValueType.FLOAT_32);
	}

	public boolean isDecimal() {
		return isDouble() || isFloat();
	}

	public boolean isInteger() {
		return !isDecimal();
	}

	public boolean isBool() {
		return valueType.equals(NumericValueType.BOOL_1);
	}

	public byte byteValue() {
		return (byte) value;
	}

	public short shortValue() {
		return (short) value;
	}

	public int intValue() {
		return (int) value;
	}

	public long longValue() {
		return (long) value;
	}

	public float floatValue() {
		return (float) value;
	}

	public double doubleValue() {
		return (double) value;
	}

	public boolean booleanValue() {
		return (boolean) value;
	}
	
	public boolean isBoolean() {
		return valueType.equals(NumericValueType.BOOL_1);
	}
	
	public Object getValue() {
		return value;
	}
	
	public NumericValueType getValueType() {
		return valueType;
	}

	@Override
	public String toString() {
		return "NumericLiteralToken [literal=" + literal + ", value=" + value + ", valueType=" + valueType + "]";
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", value);
	}

}
