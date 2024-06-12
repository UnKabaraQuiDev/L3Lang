package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.ImportDefNode;

public class ImportScopeDescriptor extends ScopeDescriptor {

	private ImportDefNode node;

	public ImportScopeDescriptor(ImportDefNode importDef) {
		super(importDef.getIdent());
		this.node = importDef;
	}

	public ImportDefNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + ident.asString() + " (" + node.getPath().asString() + ") -> " + getAsmName() + ")";
	}

}
