package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public class VariableDeclarationNode extends Node {

	private final TypeReferenceNode type;
	private final String name;
	private final Node initializer;
	private final boolean staticDeclaration;

	public VariableDeclarationNode(final TypeReferenceNode type, final String name, final Node initializer, final boolean staticDeclaration) {
		this.type = type;
		this.name = name;
		this.initializer = initializer;
		this.staticDeclaration = staticDeclaration;
	}

	public TypeReferenceNode getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public Node getInitializer() {
		return this.initializer;
	}

	public boolean hasInitializer() {
		return this.initializer != null;
	}

	public boolean isStaticDeclaration() {
		return this.staticDeclaration;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject object = super.toJSONObject().put("typeRef", this.type.toJSONObject()).put("name", this.name)
				.put("static", this.staticDeclaration).put("initialized", this.hasInitializer());
		if (this.hasInitializer()) {
			object.put("initializer", this.initializer.toJSONObject());
		}
		return object;
	}
}
