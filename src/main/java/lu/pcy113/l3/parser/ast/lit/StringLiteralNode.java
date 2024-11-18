package lu.pcy113.l3.parser.ast.lit;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class StringLiteralNode extends Node {

	private StringLiteralToken value;

	public StringLiteralNode(StringLiteralToken consume) {
		this.value = consume;
	}

	public StringLiteralToken getValue() {
		return value;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", value.toJSONObject());
	}

}
