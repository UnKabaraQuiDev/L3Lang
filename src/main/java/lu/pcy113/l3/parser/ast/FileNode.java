package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class FileNode extends ListNode {

	public void addChild(Node child) {
		children.add(child.register(this));
	}

}
