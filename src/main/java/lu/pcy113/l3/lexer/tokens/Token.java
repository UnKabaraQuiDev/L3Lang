package lu.pcy113.l3.lexer.tokens;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;
import lu.pcy113.l3.lexer.TokenType;

public class Token implements JSONConvertible {

	protected int line, column;
	protected TokenType type;

	public Token(TokenType _t, int _l, int _c) {
		this.type = _t;
		this.line = _l + 1;
		this.column = _c + 1;
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

	public TokenType getType() {
		return type;
	}

	public String getPosition() {
		return (line) + ":" + (column);
	}

	@Override
	public String toString() {
		return "Token [line=" + line + ", column=" + column + ", type=" + type + "]";
	}

	public String toString(int i) {
		return "'" + type.name() + "' at " + getPosition();
	}

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject().put("position", new JSONObject().put("line", line).put("column", column)).put("type", type.name());
	}

}
