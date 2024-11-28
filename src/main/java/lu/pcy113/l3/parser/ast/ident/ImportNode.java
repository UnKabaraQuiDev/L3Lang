package lu.pcy113.l3.parser.ast.ident;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class ImportNode extends Node {

	private List<IdentifierNode> idents;
	private IdentifierNode identifier;

	public ImportNode(List<IdentifierNode> idents) {
		this.idents = idents;
		this.identifier = idents.get(idents.size() - 1);
	}

	public ImportNode(List<IdentifierNode> idents, IdentifierNode identifier) {
		this.idents = idents;
		this.identifier = identifier;
	}
	
	public IdentifierNode getIdentifier() {
		return identifier;
	}
	
	public List<IdentifierNode> getIdents() {
		return idents;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("idents", new JSONArray(idents.stream().map(Node::toJSONObject).collect(Collectors.toList()))).put("identifier", identifier.toJSONObject());
	}

}
