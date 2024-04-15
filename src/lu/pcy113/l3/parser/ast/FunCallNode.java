package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class FunCallNode extends Node {

	private boolean preset = false;
	private IdentifierToken name;

	public FunCallNode(IdentifierToken ident, boolean preset) {
		this.name = ident;
		this.preset = preset;
	}
	
	public IdentifierToken getIdent() {
		return name;
	}
	
	public boolean isPreset() {
		return preset;
	}
	
	public FunArgsValNode getArgs() {
		return (FunArgsValNode) children.get(0);
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+name.getValue()+", "+(preset ? "preset" : "def")+")";
	}

}
