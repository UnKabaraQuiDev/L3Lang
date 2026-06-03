package lu.pcy113.l3.parser.ast.expr;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public class NewArrayNode extends Node {

	private final TypeReferenceNode elementType;
	private final Node length;

	public NewArrayNode(final TypeReferenceNode elementType, final Node length) {
		this.elementType = elementType;
		this.length = length;
	}

	public TypeReferenceNode getElementType() {
		return this.elementType;
	}

	public Node getLength() {
		return this.length;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("elementType", this.elementType.toJSONObject()).put("length",
				this.length.toJSONObject());
	}

}
