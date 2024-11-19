package lu.pcy113.l3.parser.ast.container;

import java.util.Arrays;

import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.PackageNode;

public class FileNode extends ListNode {

	private String path;
	private String name;
	private PackageNode packageNode = new PackageNode(Arrays.asList(new IdentifierNode("")));

	public FileNode(String path, String name) {
		this.path = path;
		this.name = name;
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public PackageNode getPackageNode() {
		return packageNode;
	}

	public void setPackageNode(PackageNode packageNode) {
		this.packageNode = packageNode;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("package", packageNode.toJSONObject()).put("path", path).put("name", name);
	}

}
