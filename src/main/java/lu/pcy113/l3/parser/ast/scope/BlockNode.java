package lu.pcy113.l3.parser.ast.scope;

import java.util.List;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class BlockNode extends ListNode {

	private final boolean computed;

	public BlockNode(final boolean computed) {
		this.computed = computed;
	}

	public BlockNode(final boolean computed, final List<Node> children) {
		super(children);
		this.computed = computed;
	}

	public boolean isComputed() {
		return this.computed;
	}

	public void add(final Node node) {
		this.children.add(node);
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("computed", this.computed);
	}
}
