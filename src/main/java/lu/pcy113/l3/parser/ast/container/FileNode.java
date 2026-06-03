package lu.pcy113.l3.parser.ast.container;

import java.util.Arrays;

import org.json.JSONObject;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.PackageNode;

import lu.kbra.pclib.PCUtils;

public class FileNode extends ListNode {

	private final String path;
	private final String name;
	private PackageNode packageNode = new PackageNode(Arrays.asList(new IdentifierNode("")));

	public FileNode(final String path, final String name) {
		this.path = path;
		this.name = name;
	}

	public void addChild(final Node child) {
		this.children.add(child);
	}

	public boolean hasMain() {
		return this.children.stream().filter(FunDefNode.class::isInstance).map(PCUtils::<FunDefNode>cast)
				.anyMatch(FunDefNode::isMain);
	}

	public FunDefNode getMain() {
		return this.children.stream().filter(FunDefNode.class::isInstance).map(PCUtils::<FunDefNode>cast)
				.filter(FunDefNode::isMain).findFirst().orElseThrow(() -> new L3Exception("No main in " + this.path));
	}

	public String getPath() {
		return this.path;
	}

	public String getName() {
		return this.name;
	}

	public PackageNode getPackageNode() {
		return this.packageNode;
	}

	public void setPackageNode(final PackageNode packageNode) {
		this.packageNode = packageNode;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("package", this.packageNode.toJSONObject()).put("path", this.path).put("name",
				this.name);
	}

}
