package lu.pcy113.l3.parser.ast.lit;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.parser.ast.Node;

public class NumericLiteralNode extends Node {

	private NumericLiteralToken value;

	public NumericLiteralNode(NumericLiteralToken consume) {
		this.value = consume;
	}

	public NumericLiteralToken getValue() {
		return value;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", value.toJSONObject());
	}

}
