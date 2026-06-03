package lu.pcy113.l3.parser.ast.ident;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ImportNode extends Node {

	private final List<IdentifierNode> idents;
	private final IdentifierNode identifier;
	private final String value;

	public ImportNode(final List<IdentifierNode> idents) {
		this(idents, idents.get(idents.size() - 1));
	}

	public ImportNode(final List<IdentifierNode> idents, final IdentifierNode identifier) {
		this.idents = idents;
		this.identifier = identifier;

		this.value = String.join(".", idents.stream().map(IdentifierNode::getValue).collect(Collectors.toList()));
	}

	public IdentifierNode getIdentifier() {
		return this.identifier;
	}

	public List<IdentifierNode> getIdents() {
		return this.idents;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject()
				.put("idents", new JSONArray(this.idents.stream().map(Node::toJSONObject).collect(Collectors.toList())))
				.put("identifier", this.identifier.toJSONObject());
	}

}
