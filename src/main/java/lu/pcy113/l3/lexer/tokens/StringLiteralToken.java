package lu.pcy113.l3.lexer.tokens;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;

public class StringLiteralToken extends LiteralToken {

	protected String value;

	public StringLiteralToken(final TokenType type, final int line, final int column, final String value) {
		super(type, line, column);
		this.value = value;
	}

	public String getEscapedValue() {
		return this.getValue().replace("\\", "\\\\");
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "StringLiteralToken [value=" + this.value + "]";
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", this.getEscapedValue());
	}

}
