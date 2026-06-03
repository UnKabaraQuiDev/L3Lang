package lu.pcy113.l3.parser.ast.pointer;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class PointerMemberAccess extends Node {

	private final Node parent;
	private final IdentifierNode prop;

	public PointerMemberAccess(final Node expr, final IdentifierNode simpleIdentifier) {
		this.parent = expr;
		this.prop = simpleIdentifier;
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
