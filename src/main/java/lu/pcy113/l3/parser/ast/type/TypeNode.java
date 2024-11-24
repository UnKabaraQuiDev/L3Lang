package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class TypeNode extends Node {

	private String ident;

	public TypeNode(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("ident", ident);
	}

}
