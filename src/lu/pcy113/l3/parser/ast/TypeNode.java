package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;

public class TypeNode extends Node {

	private TypeNode pointed;

	private boolean generic = true, pointer = false;
	private Token token;
	private TokenType type;

	/**
	 * struct
	 */
	public TypeNode(Token token) {
		this.generic = false;
		this.token = token;
	}

	public TypeNode(TypeNode pointed) {
		this.pointer = true;
		this.generic = true;
		this.pointed = pointed;
	}

	public TypeNode(TokenType type) {
		this.generic = true;
		this.type = type;
	}

	public int getSize() throws CompilerException {
		return pointer ? MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE)
				: generic ? MemoryUtil.getPrimitiveSize(type) : ((StructScopeDescriptor) getClosestContainer().getStructScopeDescriptor(((IdentifierToken) token).getValue())).getNode().getSize();
	}

	public boolean isVoid() {
		return getType().equals(TokenType.VOID);
	}

	public TokenType getType() {
		if(generic) {
			if(pointer) {
				return pointed.getType();
			}
			return type;
		}else {
			return TokenType.USER_TYPE;
		}
	}

	public TypeNode getPointedType() throws CompilerException {
		if (pointer) {
			return pointed;
		} else {
			throw new CompilerException("Type: " + token + " (" + type + "), not a pointer, cannot get subtype");
		}
	}

	public boolean isGeneric() {
		return generic;
	}

	public Token getIdent() {
		return token;
	}

	public boolean isPointer() {
		return pointer;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TypeNode))
			return false;

		TypeNode tn = (TypeNode) obj;
		return tn.generic == generic && tn.pointer == pointer && tn.isVoid() == isVoid() && tn.getType().equals(getType());
	}

	@Override
	public String toString() {
		return super.toString() + "(generic=" + generic + ", type=" + (getType() != null ? getType().name() : null) + ", pointer=" + isPointer() + (pointer ? ", pointed=" + pointed : "")
				+ (generic ? "" : (", ident=" + ((IdentifierToken) token).getValue())) + ")";
	}

}
