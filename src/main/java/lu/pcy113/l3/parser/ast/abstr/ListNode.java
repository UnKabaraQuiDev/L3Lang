package lu.pcy113.l3.parser.ast.abstr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.symbols.NodeSymbols;

public class ListNode extends Node implements Iterable<Node> {

	protected List<Node> children = new ArrayList<Node>();
	protected NodeSymbols symbols = new NodeSymbols();

	public ListNode() {
	}

	public ListNode(List<Node> children) {
		this.children = children;
	}

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public int size() {
		return children.size();
	}

	public Stream<Node> stream() {
		return children.stream();
	}

	public NodeSymbols getSymbols() {
		return symbols;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = super.toJSONObject();
		forEach((c) -> obj.accumulate("children", c.toJSONObject()));
		obj.put("symbols", symbols.toJSONObject());
		return obj;
	}

}
