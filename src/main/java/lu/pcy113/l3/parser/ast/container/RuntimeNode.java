package lu.pcy113.l3.parser.ast.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.symbols.NodeSymbols;

public class RuntimeNode extends ListNode {

	private final FileNode mainNode;
	private final List<FileNode> files;
	private final NodeSymbols symbols = new NodeSymbols();

	public RuntimeNode(final FileNode mainNode, final List<FileNode> files) {
		this.mainNode = mainNode;
		this.files = files;

		this.mainNode.getSymbols().setParent(this.getSymbols());
		this.files.forEach(f -> f.getSymbols().setParent(this.getSymbols()));

		this.getSymbols().registerFile(mainNode);
		this.files.forEach(f -> this.getSymbols().registerFile(f));
	}

	public RuntimeNode(final FileNode mainNode) {
		this.mainNode = mainNode;
		this.files = new ArrayList<>();
	}

	public FileNode getMainNode() {
		return this.mainNode;
	}

	public List<FileNode> getFiles() {
		return this.files;
	}

	@Override
	public NodeSymbols getSymbols() {
		return this.symbols;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("main", this.mainNode.toJSONObject())
				.put("files", new JSONArray(this.files.stream().map(Node::toJSONObject).collect(Collectors.toList())))
				.put("symbols", this.symbols.toJSONObject());
	}

}
