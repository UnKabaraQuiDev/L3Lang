package lu.pcy113.l3.parser.ast;

public class FunArgNumLitValueNode extends Node {

	private int index;
	
	public FunArgNumLitValueNode(int index, Node node) {
		add(node);
		this.index = index;
	}
	
	public Node getNode() {
		return children.get(0);
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+index+")";
	}
	
}
