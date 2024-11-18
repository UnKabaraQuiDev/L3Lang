package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class PrimitiveTypeNode extends TypeNode {

	private TokenType type;

	public PrimitiveTypeNode(TokenType ident) {
		this.type = ident;
	}

	public TokenType getType() {
		return type;
	}

	public boolean isInteger() {
		return type.matches(TokenType.INT);
	}

	public boolean isDouble() {
		return type.matches(TokenType.DOUBLE);
	}

	public boolean isFloat() {
		return type.matches(TokenType.FLOAT);
	}

	@Override
<<<<<<< HEAD
	public void normalizeSize(ScopeContainer container) throws CompilerException {
=======
	public void normalizeSize(ScopeContainer container) {
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
		int size = getBytesSize();
		if (size >= 4) {
			setBytesSize(8);
		} else if (size >= 1) {
			setBytesSize(2);
		}
	}

	@Override
	public boolean typeMatches(ExprNode param) throws CompilerException {
		return (param.isDouble() && this.isDouble()) || (param.isInteger() && this.isInteger()) || (param.isFloat() && this.isFloat());
	}

	@Override
	public int getBytesSize() {
<<<<<<< HEAD
		try {
			return sizeOverride ? bytesOverride : MemoryUtil.getPrimitiveSize(type);
		} catch (CompilerException e) {
			throw new RuntimeException(e);
		}
=======
		return sizeOverride ? bytesOverride : MemoryUtil.getPrimitiveSize(type);
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
	}
<<<<<<< HEAD

	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}
=======
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git

	@Override
	public String toString() {
		return super.toString() + "(" + type.toShortString() + ", sizeOverride=" + sizeOverride + ", size=" + getBytesSize() + ")";
	}

}
