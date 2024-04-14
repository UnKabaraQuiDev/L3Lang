package lu.pcy113.l3.parser.ast;

public class ArrayInitNode extends Node implements ArrayInit {

	private boolean empty;
	private int arraySize;

	public ArrayInitNode(TypeNode type, int arraySize) {
		add(type);
		this.empty = true;
		this.arraySize = arraySize;
	}

	public boolean isEmpty() {
		return empty;
	}
	
	@Override
	public boolean hasExpr() {
		return children.size() > 2;
	}
	
	@Override
	public int getArraySize() {
		return arraySize;
	}
	
	@Override
	public Node getExpr(int i) {
		return children.get(i);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + arraySize + ", " + empty + ")";
	}

}
