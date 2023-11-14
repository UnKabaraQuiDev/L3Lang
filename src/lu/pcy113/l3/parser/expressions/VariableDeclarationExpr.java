package lu.pcy113.l3.parser.expressions;

import lu.pcy113.l3.utils.MemorySize;

public class VariableDeclarationExpr extends VariableExpr {
	
	private MemorySize memorySize;
	private String identifier;
	
	public VariableDeclarationExpr(MemorySize memorySize, String identifier) {
		this.memorySize = memorySize;
		this.identifier = identifier;
	}
	
	public MemorySize getMemorySize() {return memorySize;}
	public String getIdentifier() {return identifier;}
	
	@Override
	public String toString() {
		return "size="+memorySize+", ident="+identifier;
	}

}
