package lu.pcy113.l3.parser.ast.method;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public class ParameterNode extends Node {

	private final TypeReferenceNode type;
	private final String name;

	public ParameterNode(final TypeReferenceNode type, final String name) {
		this.type = type;
		this.name = name;
	}

	public TypeReferenceNode getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("type", this.type.toJSONObject()).put("name", this.name);
	}
}
