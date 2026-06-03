package lu.pcy113.l3.parser.ast.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ForNode extends Node {

	private final Node initializer;
	private final Node condition;
	private final Node update;
	private final Node body;

	public ForNode(final Node initializer, final Node condition, final Node update, final Node body) {
		this.initializer = initializer;
		this.condition = condition;
		this.update = update;
		this.body = body;
	}

	public Node getInitializer() {
		return this.initializer;
	}

	public Node getCondition() {
		return this.condition;
	}

	public Node getUpdate() {
		return this.update;
	}

	public Node getBody() {
		return this.body;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject object = super.toJSONObject().put("body", this.body.toJSONObject());
		object.put("initializer", this.initializer == null ? null : this.initializer.toJSONObject());
		object.put("condition", this.condition == null ? null : this.condition.toJSONObject());
		object.put("update", this.update == null ? null : this.update.toJSONObject());
		return object;
	}
}
