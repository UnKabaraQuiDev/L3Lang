package lu.pcy113.l3.parser.ast.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.scope.BlockNode;
import lu.pcy113.l3.parser.ast.type.TypeReferenceNode;

public class MethodDefNode extends ListNode {

	private final TypeReferenceNode returnType;
	private final String name;
	private final List<String> genericParameters;
	private final List<ParameterNode> parameters;
	private final BlockNode body;

	public MethodDefNode(final TypeReferenceNode returnType, final String name, final List<String> genericParameters,
			final List<ParameterNode> parameters, final BlockNode body) {
		this.returnType = returnType;
		this.name = name;
		this.genericParameters = new ArrayList<>(genericParameters);
		this.parameters = new ArrayList<>(parameters);
		this.body = body;
		this.setChildren(body.getChildren());
	}

	public TypeReferenceNode getReturnType() {
		return this.returnType;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getGenericParameters() {
		return Collections.unmodifiableList(this.genericParameters);
	}

	public List<ParameterNode> getParameters() {
		return Collections.unmodifiableList(this.parameters);
	}

	public BlockNode getBody() {
		return this.body;
	}

	public boolean isMain() {
		return "main".equals(this.name) && this.parameters.isEmpty();
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("returnType", this.returnType.toJSONObject()).put("name", this.name)
				.put("genericParameters", new JSONArray(this.genericParameters))
				.put("parameters",
						new JSONArray(this.parameters.stream().map(Node::toJSONObject).collect(Collectors.toList())))
				.put("body", this.body.toJSONObject());
	}
}
