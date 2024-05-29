package lu.pcy113.l3.parser.ast;

public class PointerDerefNode extends Node {

	public PointerDerefNode(Node node) {
		add(node);
	}
	
	public Node getNode() {
		return children.get(0);
	}
	
}
