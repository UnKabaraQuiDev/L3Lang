package lu.pcy113.l3.parser.ast;

import java.util.LinkedList;

public class Node {
	
	private Node parent;
	private LinkedList<Node> children;
	
	public Node() {
		
	}
	
	public Node(Node parent) {
		parent.add(this);
		this.parent = parent;
	}
	
	public void add(Node child) {
		if(child == null)
			return;
		if(children == null)
			children = new LinkedList<>();
		children.add(child);
	}
	
	public Node getParent() {
		return parent;
	}
	public LinkedList<Node> getChildren() {
		return children;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public boolean isLeaf() {
		return children == null || children.isEmpty();
	}
	
}
