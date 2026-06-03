package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class MembersAccess extends Node {

	private final Node parent;
	private final IdentifierNode prop;

	public MembersAccess(final Node parent, final IdentifierNode prop) {
		this.parent = parent;
		this.prop = prop;
	}

	public Node getParent() {
		return this.parent;
	}

	public IdentifierNode getProp() {
		return this.prop;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("parent", this.parent.toJSONObject()).put("prop", this.prop.toJSONObject());
	}

}
