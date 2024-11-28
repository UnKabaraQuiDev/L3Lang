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

	private FileNode mainNode;
	private List<FileNode> files;
	private NodeSymbols symbols = new NodeSymbols();

	public RuntimeNode(FileNode mainNode, List<FileNode> files) {
		this.mainNode = mainNode;
		this.files = files;
		
		this.mainNode.getSymbols().setParent(this.getSymbols());
		this.files.forEach(f -> f.getSymbols().setParent(this.getSymbols()));
		
		this.getSymbols().registerFile(mainNode);
		this.files.forEach(f -> this.getSymbols().registerFile(f));
	}

	public RuntimeNode(FileNode mainNode) {
		this.mainNode = mainNode;
		this.files= new ArrayList<>();
	}

	public FileNode getMainNode() {
		return mainNode;
	}

	public List<FileNode> getFiles() {
		return files;
	}
	
	public NodeSymbols getSymbols() {
		return symbols;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("main", mainNode.toJSONObject()).put("files", new JSONArray(files.stream().map(Node::toJSONObject).collect(Collectors.toList()))).put("symbols", symbols.toJSONObject());
	}

}
