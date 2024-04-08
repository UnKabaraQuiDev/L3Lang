package lu.pcy113.l3.parser.ast;

public class FunArgNumLitValueNode extends Node {

	private Node value;
	
	public FunArgNumLitValueNode(Node node) {
		this.value = node;
	}
	
	public Node getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+value+")";
	}
	
}
