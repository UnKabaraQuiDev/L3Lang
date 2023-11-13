package lu.pcy113.l3.lexer;

import java.util.HexFormat;

import lu.pcy113.l3.parser.ValueType;
import lu.pcy113.l3.utils.BinFormat;

public class NumericLiteralToken extends LiteralToken<Number> {
	
	protected String literal;
	protected Number value;
	protected ValueType valueType;
	
	public NumericLiteralToken(TokenType type, int line, int column, String literal) throws LexerException {
		super(type, line, column);
		
		literal = literal.trim();
		this.literal = literal;
		if(type.equals(TokenType.DEC_NUM_LIT)) {
			try {
				value = Double.parseDouble(literal);
			} catch(NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: "+e.getMessage(), line, column, literal);
			}
		} else if(type.equals(TokenType.HEX_NUM_LIT)) {
			try {
				value = HexFormat.fromHexDigitsToLong(literal.substring(2));
			} catch(NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: "+e.getMessage(), line, column, literal);
			}
		} else if(type.equals(TokenType.BIN_NUM_LIT)) {
			try {
				value = BinFormat.fromBinDigitsToLong(literal.substring(2));
			} catch(NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: "+e.getMessage(), line, column, literal);
			}
		} else if(type.equals(TokenType.NUM_LIT)) {
			try {
				value = Long.parseLong(literal);
			} catch(NumberFormatException e) {
				throw new LexerException(e, "Invalid number format: "+e.getMessage(), line, column, literal);
			}
		}
		if(value instanceof Double) {
			valueType = ValueType.DECIMAL;
		}else {
			if((long) value <= Byte.MAX_VALUE) {
				valueType = ValueType.BYTE;
			}else if((long) value <= Short.MAX_VALUE) {
				valueType = ValueType.SHORT;
			}else if((long) value <= Integer.MAX_VALUE) {
				valueType = ValueType.INTEGER;
			}else if((long) value <= Long.MAX_VALUE) {
				valueType = ValueType.LONG;
			}
		}
	}
	
	public String getLiteral() {return literal;}
	@Override
	public Number getValue() {return value;}
	@Override
	public ValueType getValueType() {return valueType;}
	
	@Override
	public String toString() {
		return NumericLiteralToken.class.getName()+"[line="+line+", column="+column+", type="+type+", literal="+literal+", value="+value+"]";
	}
	
}
