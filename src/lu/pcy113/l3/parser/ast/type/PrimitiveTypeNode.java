package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.MemoryUtil;

public class PrimitiveTypeNode extends TypeNode {

	private TokenType type;

	public PrimitiveTypeNode(TokenType ident) {
		this.type = ident;
	}

	public TokenType getType() {
		return type;
	}

	public boolean isDecimal() {
		return type.matches(TokenType.FLOAT);
	}
	
	public boolean isInteger() {
		return type.matches(TokenType.INT);
	}
	
	@Override
	public int getBytesSize() throws CompilerException {
		return MemoryUtil.getPrimitiveSize(type);
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + type.toString() + ")";
	}

}
