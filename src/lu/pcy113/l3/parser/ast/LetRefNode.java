package lu.pcy113.l3.parser.ast;

public class LetRefNode extends Node {
	
	public LetRefNode(Node node) {
		add(node);
	}

	public Node getNode() {
		return children.get(0);
	}
	
}
