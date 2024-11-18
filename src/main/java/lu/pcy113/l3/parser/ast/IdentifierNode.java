package lu.pcy113.l3.parser.ast;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class IdentifierNode extends Node {

	private String value;

	public IdentifierNode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", value);
	}

}
