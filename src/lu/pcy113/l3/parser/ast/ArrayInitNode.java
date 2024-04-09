package lu.pcy113.l3.parser.ast;

public class ArrayInitNode extends Node {

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

	public int getArraySize() {
		return arraySize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + arraySize + ", " + empty + ")";
	}

}
