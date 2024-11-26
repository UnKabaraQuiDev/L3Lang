package lu.pcy113.l3.parser.ast.pointer;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class PointerMemberAccess extends Node {

	private Node parent;
	private IdentifierNode prop;

	public PointerMemberAccess(Node expr, IdentifierNode simpleIdentifier) {
		this.parent = expr;
		this.prop = simpleIdentifier;
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
