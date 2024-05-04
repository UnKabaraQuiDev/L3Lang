package lu.pcy113.l3.parser.ast;

public class ArrayInitNode extends Node implements ArrayInit {

	private boolean empty;
	private int arraySize, stackSize;

	public ArrayInitNode(TypeNode type, int arraySize) {
		add(type);
		this.empty = true;
		this.arraySize = arraySize;
	}

	public ArrayInitNode(TypeNode type) {
		add(type);
		this.empty = true;
	}

	@Override
	public Node add(Node child) {
		if(children.size()+1 > 2) {
			empty = false;
		}
		return super.add(child);
	}
	
	public boolean isEmpty() {
		return empty;
	}

	@Override
	public int getStackSize() {
		return stackSize;
	}

	@Override
	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	@Override
	public boolean hasExpr() {
		return children.size() > 2;
	}

	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
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
	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + arraySize + ", " + empty + ")";
	}

	public boolean isRaw() {
		return hasExpr() && children.subList(1, children.size()).stream().allMatch(c -> c instanceof NumLitNode);
	}

}
