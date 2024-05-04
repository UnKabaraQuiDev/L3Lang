package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;

public class TypeNode extends Node {

	public static final int POINTER_SIZE = 4;

	private TypeNode pointed;

	private boolean generic = true, pointer = false;
	private Token token;
	private TokenType type;

	public TypeNode(boolean generic, Token token) {
		this.generic = generic;
		this.token = token;
	}

	public TypeNode(TypeNode pointed) {
		this.pointed = pointed;
		this.pointer = true;
	}

	public TypeNode(boolean generic, TokenType type) {
		this.generic = generic;
		this.type = type;
	}

	public int getSize() throws CompilerException {
		int size = generic ? 4 : ((StructScopeDescriptor) getClosestContainer().getStructScopeDescriptor(((IdentifierToken) token).getValue())).getNode().getSize();
		return pointer ? POINTER_SIZE : size;
	}

	public boolean isVoid() {
		return getType().equals(TokenType.VOID);
	}

	public TokenType getType() {
		if (type == null && token != null) {
			return token.getType();
		}else if(type == null) {
			return TokenType.TYPE;
		}
		return type;
	}

	public TypeNode getPointedType() throws CompilerException {
		if (pointer) {
			return pointed;
		} else {
			throw new CompilerException("Type: "+token+" ("+type+"), not a pointer, cannot get subtype");
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
