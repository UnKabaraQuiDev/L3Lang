package lu.pcy113.l3.parser.ast.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class WhileNode extends Node {

	private final Node condition;
	private final Node body;
	private final Node elseBranch;

	public WhileNode(final Node condition, final Node body, final Node elseBranch) {
		this.condition = condition;
		this.body = body;
		this.elseBranch = elseBranch;
	}

	public Node getCondition() {
		return this.condition;
	}

	public Node getBody() {
		return this.body;
	}

	public Node getElseBranch() {
		return this.elseBranch;
	}

	public boolean hasElseBranch() {
		return this.elseBranch != null;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject object = super.toJSONObject().put("condition", this.condition.toJSONObject()).put("body",
				this.body.toJSONObject());
		if (this.hasElseBranch()) {
			object.put("else", this.elseBranch.toJSONObject());
		}
		return object;
	}
}
