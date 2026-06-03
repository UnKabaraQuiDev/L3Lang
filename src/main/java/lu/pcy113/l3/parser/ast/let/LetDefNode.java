package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class LetDefNode extends Node {

	private final TypeNode type;
	private final IdentifierNode identifier;
	private Node value;
	private boolean _static;

	public LetDefNode(final TypeNode type, final IdentifierNode identifier, final Node value) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}

	public LetDefNode(final TypeNode type, final IdentifierNode identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public LetDefNode(final TypeNode type, final IdentifierNode identifier, final Node value, final boolean _static) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;
		this._static = _static;
	}

	public LetDefNode(final TypeNode type, final IdentifierNode identifier, final boolean _static) {
		this.type = type;
		this.identifier = identifier;
		this._static = _static;
	}

	public TypeNode getType() {
		return this.type;
	}

	public IdentifierNode getIdentifier() {
		return this.identifier;
	}

	public Node getValue() {
		return this.value;
	}

	public boolean hasValue() {
		return this.value != null;
	}

	public boolean isStatic() {
		return this._static;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject obj = super.toJSONObject().put("type", this.type.toJSONObject()).put("identifier",
				this.identifier.toJSONObject());
		obj.put("initialized", this.hasValue());
		obj.put("static", this._static);
		if (this.hasValue()) {
			obj.put("value", this.value.toJSONObject());
		}
		return obj;
	}

}
