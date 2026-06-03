package lu.pcy113.l3.lexer.tokens;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;
import lu.pcy113.l3.lexer.TokenType;

public class Token implements JSONConvertible {

	protected int line, column;
	protected TokenType type;

	public Token(final TokenType _t, final int _l, final int _c) {
		this.type = _t;
		this.line = _l + 1;
		this.column = _c + 1;
	}

	public int getColumn() {
		return this.column;
	}

	public int getLine() {
		return this.line;
	}

	public TokenType getType() {
		return this.type;
	}

	public String getPosition() {
		return this.line + ":" + this.column;
	}

	@Override
	public String toString() {
		return "Token [line=" + this.line + ", column=" + this.column + ", type=" + this.type + "]";
	}

	public String toString(final int i) {
		return "'" + this.type.name() + "' at " + this.getPosition();
	}

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject().put("position", new JSONObject().put("line", this.line).put("column", this.column))
				.put("type", this.type.name());
	}

}
