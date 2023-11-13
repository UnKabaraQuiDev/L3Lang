package lu.pcy113.l3.parser;

public class VariableSet {
	
	private String name;
	private Expression object;
	
	public VariableSet(String name, Expression object) {
		this.name = name;
		this.object = object;
	}
	
	public String getName() {return name;}
	public Expression getObject() {return object;}

}
