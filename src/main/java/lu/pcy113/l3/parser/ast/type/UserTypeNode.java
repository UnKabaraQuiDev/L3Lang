package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.LetDefNode;
<<<<<<< HEAD
=======
import lu.pcy113.l3.parser.ast.StructDefNode;
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
<<<<<<< HEAD
=======
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git

public class UserTypeNode extends TypeNode {

	private IdentifierLitNode ident;
	private int byteSize = -1;

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
<<<<<<< HEAD

	@Override
	public void normalizeSize(ScopeContainer container) throws CompilerException {
		byteSize = container.getStructDefDescriptor(ident.getLeaf().getValue()).getNode().getChildren().stream().skip(1).mapToInt((c) -> {
			try {
				((LetDefNode) c).getType().normalizeSize(container);
			} catch (CompilerException e) {
				throw new RuntimeException(e);
			}
			return ((LetDefNode) c).getType().getBytesSize();
		}).sum();
	}
=======
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git

	@Override
<<<<<<< HEAD
	public int getBytesSize() {
		if (byteSize == -1)
			throw new RuntimeException(new CompilerException("Normalize size first."));

		return byteSize;
=======
	public void normalizeSize(ScopeContainer container) {
		try {
			StructScopeDescriptor structDesc = container.getStructDefDescriptor(ident);
			StructDefNode structDef = structDesc.getNode();

			int subSize = 0;

			for (LetDefNode def : structDef.getFields()) {
				def.getType().normalizeSize(container);
				subSize += def.getType().getBytesSize();
			}
			
			setBytesSize(bytesOverride);
		} catch (CompilerException e) {
			throw new RuntimeException(e);
		}
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
	}

<<<<<<< HEAD
=======
	public int getBytesSize() {
		if (!sizeOverride) {
			throw new RuntimeException(new CompilerException("Normalize size first."));
		}
		return bytesOverride;
	}

>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

	@Override
	public String toString() {
<<<<<<< HEAD
		return super.toString() + "(" + ident.asString() + ", size=" + byteSize + ")";
=======
		return super.toString() + "(" + ident.asString() + ", sizeOverride="+sizeOverride+", size=" + bytesOverride + ")";
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
	}

}
