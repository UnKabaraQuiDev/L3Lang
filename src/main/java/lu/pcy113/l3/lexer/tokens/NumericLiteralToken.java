package lu.pcy113.l3.lexer.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;

public class NumericLiteralToken extends LiteralToken {

	public enum NumericValueType {
		//@formatter:off
		BOOL_1("bool", 1, TokenType.BOOLEAN, false),

		FLOAT_64("double", 8, TokenType.DOUBLE, true),
		FLOAT_32("float", 4, TokenType.FLOAT, true),

		INT_128("int128", 16, TokenType.INT_128, false),
		INT_64("int64", 8, TokenType.INT_64, false),
		INT_32("int32", 4, TokenType.INT_32, false),
		INT_16("int16", 2, TokenType.INT_16, false),


		INT_128_S("int128s", 16, TokenType.INT_128_S, true),
		INT_64_S("int64s", 8, TokenType.INT_64_S, true),
		INT_32_S("int32s", 4, TokenType.INT_32_S, true),
		INT_16_S("int16s", 2, TokenType.INT_16_S, true),

		CHAR("char", 1, TokenType.CHAR, false),
		INT_8("int8", 1, TokenType.INT_8, false),

		INT_8_S("int8s", 1, TokenType.INT_8_S, true);
		//@formatter:on

		private final String name;
		private final int bytes;
		private final TokenType tt;
		private final boolean signed;

		NumericValueType(final String name, final int bytes, final TokenType tt, final boolean signed) {
			this.name = name;
			this.bytes = bytes;
			this.tt = tt;
			this.signed = signed;
		}

		public String getName() {
			return this.name;
		}

		public int getBytes() {
			return this.bytes;
		}

		public TokenType getTokenType() {
			return this.tt;
		}

		public boolean isSigned() {
			return this.signed;
		}

		public boolean isUnsigned() {
			return !this.signed;
		}

		public static NumericValueType byTokenType(final TokenType tt) {
			for (final NumericValueType v : NumericValueType.values()) {
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

	public NumericLiteralToken(final TokenType type, final int line, final int column, final String literal, final NumericValueType valueType,
			final Object value) {
		super(TokenType.NUM_LIT, line, column);
		this.valueType = valueType;
		this.value = value;
		this.literal = literal;
	}

	public static NumericLiteralToken parseNumeric(final TokenType tokenType, final int line, final int column, final String literal) {
		// Regex patterns for various numeric formats
		final String intPattern = "^(\\d+)([bBsSlLdDfF]{0,2})$";
		final String floatPattern = "^(\\d+\\.?\\d+)([dDfF]?)$";
		final String boolPattern = "^(true|false)$";

		Matcher matcher;
		if (literal.matches(boolPattern)) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.BOOL_1,
					Boolean.parseBoolean(literal));
		}

		switch (tokenType) {
		case NUM_LIT:
			matcher = Pattern.compile(intPattern).matcher(literal);
			if (matcher.matches()) {
				final String number = matcher.group(1);
				final String suffix = matcher.group(2).toLowerCase();
				return NumericLiteralToken.parseInteger(tokenType, line, column, literal, number, suffix);
			}
			break;

		case DEC_NUM_LIT:
			matcher = Pattern.compile(floatPattern).matcher(literal);
			if (matcher.matches()) {
				final String number = matcher.group(1);
				final String suffix = matcher.group(2).toLowerCase();
				if ("f".equals(suffix)) {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_32,
							Float.parseFloat(number));
				} else {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_64,
							Double.parseDouble(number));
				}
			}
			break;

		case HEX_NUM_LIT:
			if (literal.startsWith("0x")) {
				final String number = literal.substring(2);
				final long parsedValue = Long.parseUnsignedLong(number, 16);
				return NumericLiteralToken.parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;

		case BIN_NUM_LIT:
			if (literal.startsWith("0b")) {
				final String number = literal.substring(2);
				final long parsedValue = Long.parseUnsignedLong(number, 2);
				return NumericLiteralToken.parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;
		}

		throw new IllegalArgumentException("Invalid numeric literal: " + literal + " for type " + tokenType);
	}

	private static NumericLiteralToken parseInteger(final TokenType tokenType, final int line, final int column, final String literal,
			final String number, final String suffix) {
		switch (suffix) {
		case "b":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_8,
					Byte.parseByte(number));
		case "s":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_16,
					Short.parseShort(number));
		case "l":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64,
					Long.parseLong(number));
		case "ll":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128,
					new java.math.BigInteger(number));
		case "d":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_64,
					Double.parseDouble(number));
		case "f":
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_32,
					Float.parseFloat(number));
		default:
			// Default to INT_32 unless it's too large
			final long longValue = Long.parseLong(number);
			if (longValue <= Integer.MAX_VALUE) {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_32,
						(int) longValue);
			} else if (longValue <= Long.MAX_VALUE) {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64, longValue);
			} else {
				return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128,
						new java.math.BigInteger(number));
			}
		}
	}

	private static NumericLiteralToken parseHexOrBinValue(final TokenType tokenType, final int line, final int column, final String literal,
			final long value) {
		if (value <= Byte.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_8, (byte) value);
		} else if (value <= Short.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_16, (short) value);
		} else if (value <= Integer.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_32, (int) value);
		} else if (value <= Long.MAX_VALUE) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_64, value);
		} else {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.INT_128,
					new java.math.BigInteger(String.valueOf(value)));
		}
	}

	public String getLiteral() {
		return this.literal;
	}

	public boolean isDouble() {
		return NumericValueType.FLOAT_64.equals(this.valueType);
	}

	public boolean isFloat() {
		return NumericValueType.FLOAT_32.equals(this.valueType);
	}

	public boolean isDecimal() {
		return this.isDouble() || this.isFloat();
	}

	public boolean isInteger() {
		return !this.isDecimal();
	}

	public boolean isBool() {
		return NumericValueType.BOOL_1.equals(this.valueType);
	}

	public byte byteValue() {
		return (byte) this.value;
	}

	public short shortValue() {
		return (short) this.value;
	}

	public int intValue() {
		return (int) this.value;
	}

	public long longValue() {
		return (long) this.value;
	}

	public float floatValue() {
		return (float) this.value;
	}

	public double doubleValue() {
		return (double) this.value;
	}

	public boolean booleanValue() {
		return (boolean) this.value;
	}

	public boolean isBoolean() {
		return NumericValueType.BOOL_1.equals(this.valueType);
	}

	public Object getValue() {
		return this.value;
	}

	public NumericValueType getValueType() {
		return this.valueType;
	}

	@Override
	public String toString() {
		return "NumericLiteralToken [literal=" + this.literal + ", value=" + this.value + ", valueType="
				+ this.valueType + "]";
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", this.value);
	}

}
