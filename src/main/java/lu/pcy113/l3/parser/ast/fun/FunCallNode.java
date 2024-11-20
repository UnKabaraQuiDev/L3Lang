package lu.pcy113.l3.parser.ast.fun;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class FunCallNode extends Node {

	private Node parent;
	private List<Node> args;

	public FunCallNode(Node parent, List<Node> args) {
		this.parent = parent;
		this.args = args;
	}

	public Node getParent() {
		return parent;
	}

	public List<Node> getArgs() {
		return args;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("parent", parent.toJSONObject()).put("args", new JSONArray(args.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
