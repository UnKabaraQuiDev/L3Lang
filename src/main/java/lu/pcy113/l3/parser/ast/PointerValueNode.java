package lu.pcy113.l3.parser.ast;

public class PointerValueNode extends Node {

	public PointerValueNode(Node node) {
		add(node);
	}
	
	public Node getExpr() {
		return children.get(0);
	}

}
