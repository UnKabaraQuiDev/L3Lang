package lu.pcy113.l3.parser.ast.abstr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.symbols.NodeSymbols;

public class ListNode extends Node implements Iterable<Node> {

	protected List<Node> children = new ArrayList<>();
	protected NodeSymbols symbols = new NodeSymbols();

	public ListNode() {
	}

	public ListNode(final List<Node> children) {
		this.children = children;
	}

	@Override
	public Iterator<Node> iterator() {
		return this.children.iterator();
	}

	public boolean isEmpty() {
		return this.children.isEmpty();
	}

	public int size() {
		return this.children.size();
	}

	public Stream<Node> stream() {
		return this.children.stream();
	}

	public NodeSymbols getSymbols() {
		return this.symbols;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public void setChildren(final List<Node> children) {
		this.children = children;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject obj = super.toJSONObject();
		this.forEach(c -> obj.accumulate("children", c.toJSONObject()));
		obj.put("symbols", this.symbols.toJSONObject());
		return obj;
	}

}
