package lu.pcy113.l3.parser.ast.ident;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class LongIdentifierNode extends IdentifierNode {

	protected List<IdentifierNode> members;
	
	public LongIdentifierNode(List<IdentifierNode> values) {
		super(values.get(values.size() - 1).getValue());
		
		this.members = values;
	}

	public List<IdentifierNode> getMembers() {
		return members;
	}
	
	@Override
	public String getValue() {
		return members.stream().map(IdentifierNode::getValue).collect(Collectors.joining("."));
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("value", getValue()).put("children", new JSONArray(members.stream().map(IdentifierNode::toJSONObject).collect(Collectors.toList())));
	}

}
