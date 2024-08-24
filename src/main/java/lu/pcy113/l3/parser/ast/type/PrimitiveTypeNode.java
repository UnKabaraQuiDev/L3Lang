package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

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
	public void normalizeSize() throws CompilerException {
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
	public int getBytesSize() throws CompilerException {
		return sizeOverride ? bytesOverride : MemoryUtil.getPrimitiveSize(type);
	}
	
	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + type.toString() + ")";
	}

}
