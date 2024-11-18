package lu.pcy113.l3.parser.ast;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class RuntimeNode extends Node implements Iterable<Node> {

	private List<Node> children;

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject();
		forEach((c) -> obj.accumulate("children", c));
		return obj;
	}

}
