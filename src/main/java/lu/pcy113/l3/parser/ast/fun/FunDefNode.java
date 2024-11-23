package lu.pcy113.l3.parser.ast.fun;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.let.ArgDefNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunDefNode extends Node {

	private TypeNode returnType;
	private IdentifierNode identifier;
	private List<ArgDefNode> args;
	private List<Node> body;

	public FunDefNode(TypeNode returnType, IdentifierNode identifier, List<ArgDefNode> args, List<Node> body) {
		this.returnType = returnType;
		this.identifier = identifier;
		this.args = args;
		this.body = body;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("body", new JSONArray(body.stream().map(Node::toJSONObject).collect(Collectors.toList()))).put("returnType", returnType.toJSONObject()).put("identifier", identifier.toJSONObject()).put("args", new JSONArray(args.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
