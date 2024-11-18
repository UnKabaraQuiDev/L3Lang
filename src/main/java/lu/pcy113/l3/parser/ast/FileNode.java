package lu.pcy113.l3.parser.ast;

public class FileNode extends ListNode {

	public void addChild(Node child) {
		children.add(child.register(this));
	}

}
