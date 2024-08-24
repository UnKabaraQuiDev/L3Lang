package lu.pcy113.l3.parser.ast.lit;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public class NullLitNode extends Node implements RecursiveArithmeticOp {

	private Object value;
	private int stackSize = 4;

	public NullLitNode(Object value) {
		this.value = value;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return value instanceof Float;
	}

	@Override
	public boolean isDouble() {
		return value instanceof Double;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return !isDouble() && !isFloat();
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
