package lu.pcy113.l3.parser.ast.lit;

public class DecimalNumLitNode extends NumLitNode {

	private double value;

	public DecimalNumLitNode(double value) {
		this.value = value;
	}

	@Override
	public boolean isDecimal() {
		return true;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + ")";
	}

}
