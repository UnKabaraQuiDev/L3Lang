package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class FunCallNode extends Node {

	private boolean preset = false;
	private IdentifierToken name, source;

	public FunCallNode(IdentifierToken ident, boolean preset) {
		this.name = ident;
		this.preset = preset;
	}

	public FunCallNode(IdentifierToken source, IdentifierToken ident) {
		this.source = source;
		this.name = ident;
	}

	public IdentifierToken getSource() {
		return source;
	}

	public boolean hasSource() {
		return source != null;
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
		return super.toString() + "(" + (hasSource() ? source.getValue() : "") + "." + name.getValue() + ", " + (preset ? "preset" : "def") + ")";
	}

}
