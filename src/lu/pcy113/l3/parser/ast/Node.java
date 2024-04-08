package lu.pcy113.l3.parser.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.utils.StringUtils;

public class Node {

	private Node parent;
	private LinkedList<Node> children;

	public Node() {
	}

	public Node(Node parent) {
		parent.add(this);
		this.parent = parent;
	}

	public void visit(L3Compiler compiler) throws CompilerException {
		// do nothing
	}
	
	public Node add(Node child) {
		if (child == null)
			return null;
		if (children == null)
			children = new LinkedList<>();
		children.add(child);
		return child;
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

	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		String ret = tab + toString() + (!isLeaf() ? "[\n" + (children.stream().map(c -> c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]" : "");
		return ret;
	}

	@Override
	public String toString() {
		return "Node@" + this.getClass().getSimpleName();
	}

}
