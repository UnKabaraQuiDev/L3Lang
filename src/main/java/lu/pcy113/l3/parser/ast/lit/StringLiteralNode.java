package lu.pcy113.l3.parser.ast.lit;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class StringLiteralNode extends Node {

	private final StringLiteralToken value;

	public StringLiteralNode(final StringLiteralToken consume) {
		this.value = consume;
	}

	public StringLiteralToken getValue() {
		return this.value;
	}

	@Override
	public String toSourceString() {
		return "\"" + value.getValue() + "\"";
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", this.value.toJSONObject());
	}

}
