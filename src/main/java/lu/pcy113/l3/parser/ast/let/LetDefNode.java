package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.IdentifierNode;
import lu.pcy113.l3.parser.ast.TypeNode;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class LetDefNode extends Node {

	private TypeNode type;
	private IdentifierNode identifier;
	private Node value;

	public LetDefNode(TypeNode type, IdentifierNode identifier, Node value) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}

	public LetDefNode(TypeNode type, IdentifierNode identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public TypeNode getType() {
		return type;
	}

	public IdentifierNode getIdentifier() {
		return identifier;
	}

	public Node getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject().put("type", type.toJSONObject()).put("identifier", identifier.toJSONObject());
		obj.put("initialized", hasValue());
		if (hasValue()) {
			obj.put("value", value.toJSONObject());
		}
		return obj;
	}

}
