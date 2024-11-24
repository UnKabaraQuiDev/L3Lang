package lu.pcy113.l3.parser.ast.abstr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.symbols.NodeSymbols;

public class ListNode extends Node implements Iterable<Node> {

	protected List<Node> children = new ArrayList<Node>();
	protected NodeSymbols symbols = new NodeSymbols();
	
	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}
	
	public NodeSymbols getSymbols() {
		return symbols;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject();
		forEach((c) -> obj.accumulate("children", c.toJSONObject()));
		obj.put("symbols", symbols.toJSONObject());
		return obj;
	}

}
