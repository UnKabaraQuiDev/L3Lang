package lu.pcy113.l3.parser;

public class VariableVariableDeclaration {
	
	private VariableType type;
	private String name;
	private Object value;
	
	public VariableVariableDeclaration(VariableType type, String string, Object value) {
		this.type = type;
		this.name = string;
		this.value = value;
	}
	
	public VariableType getType() {return type;}
	public String getName() {return name;}
	public Object getValue() {return value;}
	
}
