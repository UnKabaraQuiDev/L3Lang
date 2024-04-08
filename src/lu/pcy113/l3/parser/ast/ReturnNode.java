package lu.pcy113.l3.parser.ast;

public class ReturnNode extends Node {

	public ReturnNode(Node expr) {
		add(expr);
	}
	
	public Node getExpr() {
		return children.get(0);
	}
	
}
