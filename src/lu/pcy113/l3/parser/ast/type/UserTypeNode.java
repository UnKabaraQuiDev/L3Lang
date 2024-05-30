package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class UserTypeNode extends TypeNode {

	private IdentifierLitNode ident;

	public UserTypeNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public IdentifierLitNode getIdentifier() {
		return ident;
	}

	@Override
	public int getBytesSize() throws CompilerException {
		throw new CompilerException("Not implemented.");
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ")";
	}

}