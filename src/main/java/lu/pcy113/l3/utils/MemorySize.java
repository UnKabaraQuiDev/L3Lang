package lu.pcy113.l3.utils;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class MemorySize {

	private final int bits, bytes;
	private String customSizeIdent;

	public MemorySize(final int bits, final int bytes) {
		this.bits = bits;
		this.bytes = bytes;
	}

	public MemorySize(final TokenType token) {
		this.bits = MemorySize.getBits(token);
		this.bytes = MemorySize.getBytes(token);

		if (this.bits == -1 || this.bytes == -1) {
			throw new IllegalArgumentException("Invalid token type.");
		}
	}

	public MemorySize(final Token token) {
		final TokenType type = token.getType();
		this.bits = MemorySize.getBits(type);
		this.bytes = MemorySize.getBytes(type);

		if ((this.bits == -1 || this.bytes == -1) && token instanceof IdentifierToken) {
			this.customSizeIdent = ((IdentifierToken) token).getValue();
		}
	}

	public int getBits() {
		return this.bits;
	}

	public int getBytes() {
		return this.bytes;
	}

	public String getCustomSizeIdent() {
		return this.customSizeIdent;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{bits=" + this.bits + ", bytes=" + this.bytes + ", customSizeIdent="
				+ this.customSizeIdent + "}";
	}

	/**
	 * @returns number of bits needed
	 */
	public static int getBits(final TokenType tokenType) {
		switch (tokenType) {
		case INT_1:
			return 1;
		case INT_8:
			return 8;
		case INT_16:
			return 16;
		case INT_32:
			return 32;
		case INT_64:
			return 64;
		case INT_8_S:
			return 8;
		case INT_16_S:
			return 16;
		case INT_32_S:
			return 32;
		case INT_64_S:
			return 64;
		}
		return -1;
	}

	/**
	 * @returns number of bytes needed
	 */
	public static int getBytes(final TokenType tokenType) {
		return switch (tokenType) {
		case INT_1 -> 1;
		case INT_8 -> 1;
		case INT_16 -> 2;
		case INT_32 -> 4;
		case INT_64 -> 8;
		case INT_8_S -> 1;
		case INT_16_S -> 2;
		case INT_32_S -> 4;
		case INT_64_S -> 8;
		default -> -1;
		};
	}

}
