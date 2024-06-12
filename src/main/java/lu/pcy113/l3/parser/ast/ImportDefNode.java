package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class ImportDefNode extends Node {

	private IdentifierLitNode path;
	private IdentifierLitNode ident;

	public ImportDefNode(IdentifierLitNode strLit, IdentifierLitNode ident) {
		this.path = strLit;
		this.ident = ident;
	}

	public IdentifierLitNode getPath() {
		return path;
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public String getIdentValue() {
		return ident.asString();
	}

}
