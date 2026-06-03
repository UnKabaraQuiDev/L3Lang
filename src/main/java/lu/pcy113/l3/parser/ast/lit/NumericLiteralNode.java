package lu.pcy113.l3.parser.ast.lit;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class NumericLiteralNode extends Node {

	private final NumericLiteralToken value;

	public NumericLiteralNode(final NumericLiteralToken consume) {
		this.value = consume;
	}

	public NumericLiteralToken getValue() {
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
