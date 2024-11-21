package lu.pcy113.l3.parser.ast.ident;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class PackageNode extends Node {

	private List<IdentifierNode> values;
	private String value;

	public PackageNode(List<IdentifierNode> values) {
		this.values = values;
		this.value = String.join(".", values.stream().map(IdentifierNode::getValue).toArray(String[]::new));
	}

	public String getValue() {
		return value;
	}

	public List<IdentifierNode> getValues() {
		return values;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", value).put("values", new JSONArray(values.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
