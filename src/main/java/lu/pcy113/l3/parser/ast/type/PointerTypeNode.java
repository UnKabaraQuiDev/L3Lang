package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

public class PointerTypeNode extends TypeNode {

	private TypeNode parent;

	public PointerTypeNode(TypeNode type) {
		super("pointer<" + type.getIdent() + ">");
		this.parent = type;
	}

	@Override
	public int computeSize() {
		return 8;
	}
	
	public TypeNode getParent() {
		return parent;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("parent", parent);
	}

}
