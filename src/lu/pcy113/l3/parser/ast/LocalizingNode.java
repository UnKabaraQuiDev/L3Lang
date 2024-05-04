package lu.pcy113.l3.parser.ast;

public class LocalizingNode extends Node {
	
	public LocalizingNode(Node node) {
		add(node);
	}

	public Node getNode() {
		return children.get(0);
	}
	
}
