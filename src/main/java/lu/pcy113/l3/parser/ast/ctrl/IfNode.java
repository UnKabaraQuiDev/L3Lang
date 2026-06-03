package lu.pcy113.l3.parser.ast.ctrl;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class IfNode extends Node {

	private final Node condition;
	private final Node thenBranch;
	private final Node elseBranch;

	public IfNode(final Node condition, final Node thenBranch, final Node elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	public Node getCondition() {
		return this.condition;
	}

	public Node getThenBranch() {
		return this.thenBranch;
	}

	public Node getElseBranch() {
		return this.elseBranch;
	}

	public boolean hasElseBranch() {
		return this.elseBranch != null;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject object = super.toJSONObject().put("condition", this.condition.toJSONObject()).put("then",
				this.thenBranch.toJSONObject());
		if (this.hasElseBranch()) {
			object.put("else", this.elseBranch.toJSONObject());
		}
		return object;
	}
}
