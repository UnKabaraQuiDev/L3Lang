package lu.pcy113.l3.lexer.tokens;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;

public class StringLiteralToken extends LiteralToken {

	protected String value;

	public StringLiteralToken(TokenType type, int line, int column, String value) {
		super(type, line, column);
		this.value = value;
	}

	public String getEscapedValue() {
		return getValue().replace("\\", "\\\\");
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "StringLiteralToken [value=" + value + "]";
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", getEscapedValue());
	}

}
