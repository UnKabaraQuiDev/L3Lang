package lu.pcy113.l3.parser.ast;

public class DelocalizingNode extends Node {
	
	public DelocalizingNode(Node node) {
		add(node);
	}

	public Node getNode() {
		return children.get(0);
	}
	
}
