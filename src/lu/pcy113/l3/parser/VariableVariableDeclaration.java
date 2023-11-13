package lu.pcy113.l3.parser;

public class VariableVariableDeclaration {
	
	private ValueType type;
	private String name;
	private Object value;
	
	public VariableVariableDeclaration(ValueType type, String string, Object value) {
		this.type = type;
		this.name = string;
		this.value = value;
	}
	
	public ValueType getType() {return type;}
	public String getName() {return name;}
	public Object getValue() {return value;}
	
}
