package lu.pcy113.l3.parser.ast.lit;

import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.Node;

public class NullLitNode extends Node implements RecursiveArithmeticOp {

	private Object value;
	private int stackSize = 4;

	public NullLitNode(Object value) {
		this.value = value;
	}

	@Override
	public boolean isFloat() {
		return value instanceof Float || value instanceof Double;
	}

	@Override
	public boolean isInt() {
		return !isFloat();
	}

	public Object getValue() {
		return value;
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + ")";
	}

}
