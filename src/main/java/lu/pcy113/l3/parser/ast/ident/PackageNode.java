package lu.pcy113.l3.parser.ast.ident;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class PackageNode extends Node {

	private final List<IdentifierNode> values;
	private final String value;

	public PackageNode(final List<IdentifierNode> values) {
		this.values = values;
		this.value = String.join(".", values.stream().map(IdentifierNode::getValue).toArray(String[]::new));
	}

	public String getValue() {
		return this.value;
	}

	public List<IdentifierNode> getValues() {
		return this.values;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", this.value).put("values",
				new JSONArray(this.values.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
