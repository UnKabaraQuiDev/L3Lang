package lu.pcy113.l3.parser.ast.container;

import java.util.Arrays;

import org.json.JSONObject;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.PackageNode;
import lu.pcy113.pclib.PCUtils;

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
	
	public boolean hasMain() {
		return children.stream().filter(PCUtils::<FunDefNode>isInstance).map(PCUtils::<FunDefNode>cast).anyMatch(FunDefNode::isMain);
	}
	
	public FunDefNode getMain() {
		return children.stream().filter(PCUtils::<FunDefNode>isInstance).map(PCUtils::<FunDefNode>cast).filter(FunDefNode::isMain).findFirst().orElseThrow(() -> new L3Exception("No main in " + path));
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
