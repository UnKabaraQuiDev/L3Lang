package lu.pcy113.l3.parser.ast;

public class ReturnNode extends Node {
	
	public ReturnNode(TypeNode type, Node expr) {
		add(type);
		add(expr);
	}

	public Node getExpr() {
		return children.get(1);
	}

	public boolean returnsVoid() {
		return getReturnType().isVoid();
	}

	public TypeNode getReturnType() {
		return (TypeNode) children.get(0);
	}

	public boolean hasExpr() {
		return children.size() > 1;
	}

}
