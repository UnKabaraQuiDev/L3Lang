package lu.pcy113.l3.parser.ast.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class MacroCallNode extends Node {

	private final String name;
	private final List<Node> arguments;

	public MacroCallNode(final String name, final List<Node> arguments) {
		this.name = name;
		this.arguments = new ArrayList<>(arguments);
	}

	public String getName() {
		return this.name;
	}

	public List<Node> getArguments() {
		return Collections.unmodifiableList(this.arguments);
	}

	public boolean isBreakpoint() {
		return "break".equals(this.name);
	}

	public boolean isInlineAssembly() {
		return "asm".equals(this.name) || "asmln".equals(this.name) || "asmlb".equals(this.name);
	}

	public boolean isLlvm() {
		return "llvm".equals(this.name);
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", this.name)
				.put("arguments",
						new JSONArray(this.arguments.stream().map(Node::toJSONObject).collect(Collectors.toList())))
				.put("breakpoint", this.isBreakpoint()).put("inlineAssembly", this.isInlineAssembly())
				.put("llvm", this.isLlvm());
	}
}
