package lu.pcy113.l3.utils;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class MemorySize {
	
	private int bits, bytes;
	private String customSizeIdent;
	
	public MemorySize(int bits, int bytes) {
		this.bits = bits;
		this.bytes = bytes;
	}
	public MemorySize(TokenType token) {
		this.bits = getBits(token);
		this.bytes = getBytes(token);
		
		if(bits == -1 || bytes == -1) {
			throw new IllegalArgumentException("Invalid token type.");
		}
	}
	public MemorySize(Token token) {
		TokenType type = token.getType();
		this.bits = getBits(type);
		this.bytes = getBytes(type);
		
		if((bits == -1 || bytes == -1) && token instanceof IdentifierToken) {
			this.customSizeIdent = ((IdentifierToken) token).getIdentifier();
		}
	}
	
	public int getBits() {return bits;}
	public int getBytes() {return bytes;}
	public String getCustomSizeIdent() {return customSizeIdent;}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"{bits="+bits+", bytes="+bytes+", customSizeIdent="+customSizeIdent+"}";
	}
	
	/**
	 * @returns number of bits needed
	 */
	public static int getBits(TokenType tokenType) {
		switch (tokenType) {
		case VAR_1:
			return 1;
		case VAR_8:
			return 8;
		case VAR_16:
			return 16;
		case VAR_32:
			return 32;
		case VAR_64:
			return 64;
		case VAR_8_S:
			return 8;
		case VAR_16_S:
			return 16;
		case VAR_32_S:
			return 32;
		case VAR_64_S:
			return 64;
		}
		return -1;
	}
	
	/**
	 * @returns number of bytes needed
	 */
	public static int getBytes(TokenType tokenType) {
		switch (tokenType) {
		case VAR_1:
			return 1;
		case VAR_8:
			return 1;
		case VAR_16:
			return 2;
		case VAR_32:
			return 4;
		case VAR_64:
			return 8;
		case VAR_8_S:
			return 1;
		case VAR_16_S:
			return 2;
		case VAR_32_S:
			return 4;
		case VAR_64_S:
			return 8;
		}
		return -1;
	}

}
