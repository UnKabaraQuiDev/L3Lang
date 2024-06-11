package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class ScopeDescriptor {

	private static int index = 0;

	protected IdentifierLitNode ident;
	protected String asmName = "sd_" + index++;

	public ScopeDescriptor(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public IdentifierLitNode getIdentifier() {
		return ident;
	}

	public String getAsmName() {
		return asmName;
	}

	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + ident.asString() + " -> " + getAsmName() + ")";
	}

}
