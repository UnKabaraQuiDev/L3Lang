package lu.pcy113.l3.parser.ast.fun;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class FunCallNode extends Node {

	private final Node parent;
	private final List<Node> args;
	private final boolean preset;

	public FunCallNode(final Node parent, final List<Node> args, final boolean preset) {
		this.parent = parent;
		this.args = args;
		this.preset = preset;
	}

	public Node getParent() {
		return this.parent;
	}

	public List<Node> getArgs() {
		return this.args;
	}

	public boolean isPreset() {
		return this.preset;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("parent", this.parent.toJSONObject())
				.put("args", new JSONArray(this.args.stream().map(Node::toJSONObject).collect(Collectors.toList())))
				.put("preset", this.preset);
	}

}
