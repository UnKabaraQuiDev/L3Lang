package lu.pcy113.l3.parser;

public class VariableAllocation extends Expression {
	
	private String name;
	
	public VariableAllocation(String string) {
		this.name = string;
	}
	
	public String getName() {return name;}
	
}
