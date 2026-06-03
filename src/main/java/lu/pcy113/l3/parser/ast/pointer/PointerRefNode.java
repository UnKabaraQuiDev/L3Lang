package lu.pcy113.l3.parser.ast.pointer;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class PointerRefNode extends Node {

	private final IdentifierNode ident;

	public PointerRefNode(final IdentifierNode ident) {
		this.ident = ident;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("ident", this.ident.toJSONObject());
	}

}
