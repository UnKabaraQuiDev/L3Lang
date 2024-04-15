package lu.pcy113.l3.parser.ast;

public class FunArgValNode extends Node {

	private int index, stackSize = 0;

	public FunArgValNode(int index, Node node) {
		this.index = index;
		add(node);
	}

	public Node getExpr() {
		return children.get(0);
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + index + ")";
	}

}
