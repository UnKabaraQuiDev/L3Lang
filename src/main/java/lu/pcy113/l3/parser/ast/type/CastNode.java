package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class CastNode extends Node {

	private TypeNode castType;
	private Node value;

	public CastNode(TypeNode castType, Node value) {
		this.castType = castType;
		this.value = value;
	}

	public TypeNode getCastType() {
		return castType;
	}

	public Node getValue() {
		return value;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("castType", castType.toJSONObject()).put("value", value.toJSONObject());
	}

}
