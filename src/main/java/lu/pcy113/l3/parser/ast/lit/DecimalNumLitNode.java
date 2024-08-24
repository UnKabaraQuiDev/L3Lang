package lu.pcy113.l3.parser.ast.lit;

public class DecimalNumLitNode extends NumLitNode<Number> {

	private Number value;

	public DecimalNumLitNode(double value) {
		this.value = value;
	}

	public DecimalNumLitNode(float value) {
		this.value = value;
	}

	@Override
	public boolean isFloat() {
		return value instanceof Float;
	}

	@Override
	public boolean isDouble() {
		return value instanceof Double;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + value + " (" + (isInteger() ? "int" : (isFloat() ? "float" : (isDouble() ? "double" : "null"))) + "))";
	}

}
