package lu.pcy113.l3.parser.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.l3.utils.StringUtils;

public class Node implements Iterable<Node> {

	protected Node parent;
	protected LinkedList<Node> children = new LinkedList<>();

	public Node() {
	}

	public Node(Node parent) {
		parent.add(this);
		this.parent = parent;
	}

	public Node remove(Node child) {
		if (child == null)
			return this;
		child.unregister(this);
		if (children.remove(child))
			return this;
		return null;
	}

	public Node add(Node child) {
		if (child == null)
			return null;
		child.register(this);
		children.add(child);
		return child;
	}

	private void register(Node parent) {
		this.parent = parent;
	}

	private void unregister(Node parent) {
		this.parent = null;
	}

	public Node getParent() {
		return parent;
	}

	public boolean hasFunDefParent() {
		Node parent = this;
		while (!(parent instanceof FunDefNode)) {
			parent = parent.parent;

			if (parent == null) {
				return false;
			}
		}
		return true;
	}

	public FunDefNode getFunDefParent() throws CompilerException {
		Node parent = this;
		while (!(parent instanceof FunDefNode)) {
			parent = parent.parent;

			if (parent == null) {
				throw new CompilerException("Parent is null.");
			}
		}
		return (FunDefNode) parent;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getFirstChild(Class<T> clazz) {
		for (Node child : children) {
			if (clazz.isInstance(child)) {
				return (T) child;
			} else {
				Node fChild = child.getFirstChild(clazz);
				if (fChild != null) {
					return (T) fChild;
				}
			}
		}
		return null;
	}

	public int getParentCount(Class<? extends Node> clazz) {
		int count = 0;

		Node paren = this.getParent();
		while (paren != null) {
			if (clazz.isInstance(paren)) {
				count++;
			}

			paren = paren.getParent();
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	public <T> T getParent(Class<T> clazz) {
		Node paren = this;

		do {
			paren = paren.getParent();
			if (paren == null) {
				return null;
			}
		} while (!clazz.isInstance(paren));

		return (T) paren;
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

	public ScopeContainerNode getParentContainer() {
		Node parent = this.getParent();
		while (!(parent instanceof ScopeContainerNode)) {
			if (parent == null) {
				return null;
			}
			parent = parent.getParent();
		}
		return (ScopeContainerNode) parent;
	}

	public ScopeContainerNode getClosestContainer() {
		if (this instanceof ScopeContainerNode) {
			return (ScopeContainerNode) this;
		}
		return getParentContainer();
	}

	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		String ret = tab + toString()
				+ (this instanceof ScopeContainerNode
						? "{" + (((ScopeContainerNode) this).getLocalDescriptors().isEmpty() ? "" : "\n")
								+ ((ScopeContainerNode) this).getLocalDescriptors().entrySet().stream().map(e -> StringUtils.repeat("\t", indent + 1) + e.getKey() + " = " + e.getValue()).collect(Collectors.joining(", \n")) + "}"
						: "")
				+ (!isLeaf() ? "[\n" + (children.stream().map(c -> c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]" : "");
		return ret;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

}
