package lu.pcy113.l3.parser.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class ListNode extends Node implements Iterable<Node> {

	protected List<Node> children = new ArrayList<Node>();

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject();
		forEach((c) -> obj.accumulate("children", c.toJSONObject()));
		return obj;
	}

}
