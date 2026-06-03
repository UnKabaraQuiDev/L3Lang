package lu.pcy113.l3.parser.ast.ident;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class IdentifierNode extends Node {

	private String value;

	public IdentifierNode(final String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", this.value);
	}

}
