package lu.pcy113.l3.parser.ast;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;

public class Node implements JSONConvertible {

	private Node parent;

	public Node register(Node parent) {
		this.parent = parent;
		return this;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return new JSONObject();
	}

}
