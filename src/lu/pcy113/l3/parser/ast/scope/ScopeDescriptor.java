package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class ScopeDescriptor {

	private static int index = 0;

	private IdentifierToken ident;
	private String asmName = "sd_" + index++;

	public ScopeDescriptor(IdentifierToken ident) {
		this.ident = ident;
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public String getAsmName() {
		return asmName;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + ident.getIdentifier() + " " + ident.getLine() + ":" + ident.getColumn() + ")";
	}

}
