package lu.pcy113.l3.parser.ast;

public class ObjectInitNode extends Node {

	public ObjectInitNode(TypeNode type) {
		add(type);
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public ConArgsValNode getArgs() {
		return (ConArgsValNode) children.get(1);
	}

}
