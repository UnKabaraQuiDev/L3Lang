package lu.pcy113.l3.parser.ast.symbols;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class NodeSymbol<T extends Node> implements JSONConvertible {

	protected T node;

	public NodeSymbol(T node) {
		this.node = node;
	}

	public T getNode() {
		return node;
	}

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject().put("type", getClass().getSimpleName());
	}

}
