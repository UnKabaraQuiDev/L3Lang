package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class LetDefNode extends Node {

	private TypeNode type;
	private IdentifierNode identifier;
	private Node value;
	private boolean _static;

	public LetDefNode(TypeNode type, IdentifierNode identifier, Node value) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}

	public LetDefNode(TypeNode type, IdentifierNode identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public LetDefNode(TypeNode type, IdentifierNode identifier, Node value, boolean _static) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
		this._static = _static;
	}

	public LetDefNode(TypeNode type, IdentifierNode identifier, boolean _static) {
		this.type = type;
		this.identifier = identifier;
		this._static = _static;
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

	public boolean isStatic() {
		return _static;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject().put("type", type.toJSONObject()).put("identifier", identifier.toJSONObject());
		obj.put("initialized", hasValue());
		obj.put("static", _static);
		if (hasValue()) {
			obj.put("value", value.toJSONObject());
		}
		return obj;
	}

}
