package lu.pcy113.l3.parser.ast.lit;

public class IntegerNumLitNode extends NumLitNode {

	private long value;

	public IntegerNumLitNode(long value) {
		this.value = value;
	}

	@Override
	public boolean isDecimal() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return true;
	}

	public long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + ")";
	}

}
