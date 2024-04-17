package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;

public class NumLitNode extends Node {

	private Object value;
	private int stackSize = 4;

	public NumLitNode(Object value) {
		this.value = value;
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
