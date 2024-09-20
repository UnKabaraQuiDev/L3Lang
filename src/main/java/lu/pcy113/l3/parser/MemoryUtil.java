package lu.pcy113.l3.parser;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;

public final class MemoryUtil {

	public static final TokenType POINTER_TYPE = TokenType.INT_64;
	public static final TokenType INT_TYPE = TokenType.INT_32;

	/**
	 * @return Bytes
	 * @throws L3Exception
	 */
	public static int getPrimitiveSize(TokenType type) {
		switch (type) {
		case INT:
			return getPrimitiveSize(INT_TYPE);
		case INT_8:
		case INT_8_S:
		case INT_1:
			return 1;
		case INT_16:
		case INT_16_S:
			return 2;
		case INT_32:
		case INT_32_S:
			return 4;
		case INT_64:
		case INT_64_S:
			return 8;
		case INT_128:
		case INT_128_S:
			return 8;
		case VOID:
			return 0;
		default:
			throw new RuntimeException(new CompilerException("Cannot get size of this type: " + type));
		}
	}

	public static int getPrimitiveSize(ValueType type) throws CompilerException {
		switch (type) {
		case CHAR:
			return 1;
		case FLOAT:
			return 4;
		case DOUBLE:
			return 8;
		case INT_16:
			return 2;
		case INT_32:
			return 4;
		case INT_64:
			return 8;
		case INT_8:
			return 1;
		default:
			throw new CompilerException("Cannot get size of this type: " + type);
		}
	}

}
