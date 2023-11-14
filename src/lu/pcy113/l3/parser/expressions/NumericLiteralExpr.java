package lu.pcy113.l3.parser.expressions;

public class NumericLiteralExpr extends Expr {
	
	private long value;
	
	public NumericLiteralExpr(long v) {
		this.value = v;
	}
	
	public long getValue() {return value;}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"[value="+value+"]";
	}
	
}
