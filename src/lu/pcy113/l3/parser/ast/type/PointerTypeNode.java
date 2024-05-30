package lu.pcy113.l3.parser.ast.type;

public class PointerTypeNode extends TypeNode {

	public PointerTypeNode(TypeNode node) {
		add(node);
	}

	public TypeNode getNode() {
		return (TypeNode) children.get(0);
	}

}
