package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class MembersAccess extends Node {

	private Node parent;
	private IdentifierNode prop;

	public MembersAccess(Node parent, IdentifierNode prop) {
		this.parent = parent;
		this.prop = prop;
	}

	public Node getParent() {
		return parent;
	}

	public IdentifierNode getProp() {
		return prop;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("parent", parent.toJSONObject()).put("prop", prop.toJSONObject());
	}

}
