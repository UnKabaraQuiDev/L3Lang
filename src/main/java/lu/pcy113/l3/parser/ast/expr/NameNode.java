package lu.pcy113.l3.parser.ast.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public class NameNode extends Node {

	private final String name;
	private final List<TypeReferenceNode> genericArguments;

	public NameNode(final String name) {
		this(name, List.of());
	}

	public NameNode(final String name, final List<TypeReferenceNode> genericArguments) {
		this.name = name;
		this.genericArguments = new ArrayList<>(genericArguments);
	}

	public String getName() {
		return this.name;
	}

	public List<TypeReferenceNode> getGenericArguments() {
		return Collections.unmodifiableList(this.genericArguments);
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", this.name).put("genericArguments",
				new JSONArray(this.genericArguments.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}
}
