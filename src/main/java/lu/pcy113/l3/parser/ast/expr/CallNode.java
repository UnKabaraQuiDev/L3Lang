package lu.pcy113.l3.parser.ast.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class CallNode extends Node {

	private final Node target;
	private final List<Node> arguments;

	public CallNode(final Node target, final List<Node> arguments) {
		this.target = target;
		this.arguments = new ArrayList<>(arguments);
	}

	public Node getTarget() {
		return this.target;
	}

	public List<Node> getArguments() {
		return Collections.unmodifiableList(this.arguments);
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("target", this.target.toJSONObject()).put("arguments",
				new JSONArray(this.arguments.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}
}
