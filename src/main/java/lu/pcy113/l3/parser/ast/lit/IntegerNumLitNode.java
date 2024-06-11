package lu.pcy113.l3.parser.ast.lit;

public class IntegerNumLitNode extends NumLitNode<Long> {

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

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + ")";
	}

}
