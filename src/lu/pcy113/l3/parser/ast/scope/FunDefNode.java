package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.FunParamsDefNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunDefNode extends ScopeContainerNode {

	private IdentifierToken ident;

	public FunDefNode(TypeNode returnType, IdentifierToken ident) {
		add(returnType);
		this.ident = ident;
	}

	public TypeNode getReturnType() {
		return (TypeNode) children.get(0);
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public FunParamsDefNode getArgs() {
		return (FunParamsDefNode) children.get(1);
	}

	public FunBodyDefNode getBody() {
		return (FunBodyDefNode) children.get(2);
	}

	public boolean matchReturnType(Class<? extends TypeNode> clazz) {
		return clazz.isInstance(getReturnType().getClass());
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getReturnType() + ": " + ident.getValue() + ")";
	}

	public boolean isMain() {
		return ident.getValue().equals("main") && this.getArgs().isLeaf() && getReturnType() instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) getReturnType()).getType().equals(TokenType.INT_8);
	}

}
