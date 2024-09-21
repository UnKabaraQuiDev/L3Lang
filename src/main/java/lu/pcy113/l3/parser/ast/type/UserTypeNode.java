package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;

public class UserTypeNode extends TypeNode {

	private IdentifierLitNode ident;

	public UserTypeNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	public IdentifierLitNode getIdentifier() {
		return ident;
	}

	@Override
	public boolean typeMatches(ExprNode param) throws CompilerException {
		throw new CompilerException("Not implemented.");
	}

	@Override
	public void normalizeSize(ScopeContainer container) {
		try {
			StructScopeDescriptor structDesc = container.getStructDefDescriptor(ident);
			StructDefNode structDef = structDesc.getNode();

			int subSize = 0;

			for (LetDefNode def : structDef.getFields()) {
				def.getType().normalizeSize(container);
				subSize += def.getType().getBytesSize();
			}

			setBytesSize(subSize);
		} catch (CompilerException e) {
			throw new RuntimeException(e);
		}
	}

	public int getBytesSize() {
		if (!sizeOverride) {
			throw new RuntimeException(new CompilerException("Normalize size first."));
		}
		return bytesOverride;
	}

	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.asString() + ", sizeOverride=" + sizeOverride + ", size=" + bytesOverride + ")";
	}

}
