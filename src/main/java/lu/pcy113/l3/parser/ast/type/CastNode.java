package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class CastNode extends Node {

	private final TypeNode castType;
	private final Node value;

	public CastNode(final TypeNode castType, final Node value) {
		this.castType = castType;
		this.value = value;
	}

	public TypeNode getCastType() {
		return this.castType;
	}

	public Node getValue() {
		return this.value;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("castType", this.castType.toJSONObject()).put("value",
				this.value.toJSONObject());
	}

}
