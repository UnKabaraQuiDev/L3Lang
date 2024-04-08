package lu.pcy113.l3.parser.ast;

public class NumLitNode extends Node {

	private Object value;

	public NumLitNode(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+value+")";
	}

}
