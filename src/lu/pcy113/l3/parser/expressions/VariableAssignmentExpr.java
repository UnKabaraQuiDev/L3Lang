package lu.pcy113.l3.parser.expressions;

public class VariableAssignmentExpr extends VariableExpr {
	
	private String identifier;
	private NumericLiteralExpr value;
	
	public VariableAssignmentExpr(String identifier, NumericLiteralExpr value) {
		this.identifier = identifier;
		this.value = value;
	}
	
	public String getIdentifier() {return identifier;}
	public NumericLiteralExpr getValue() {return value;}
	
	@Override
	public String toString() {
		return "ident="+identifier+", value="+value;
	}

}
