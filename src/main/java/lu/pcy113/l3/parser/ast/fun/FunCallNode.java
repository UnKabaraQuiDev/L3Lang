package lu.pcy113.l3.parser.ast.fun;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;

public class FunCallNode extends Node {

	private IdentifierNode identifier;
	private List<Node> args;

	public FunCallNode(IdentifierNode identifier, List<Node> args) {
		this.identifier = identifier;
		this.args = args;
	}

	public IdentifierNode getIdentifier() {
		return identifier;
	}

	public List<Node> getArgs() {
		return args;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("identifier", identifier.toJSONObject()).put("args", new JSONArray(args.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
