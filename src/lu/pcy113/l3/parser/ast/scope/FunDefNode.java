package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.FunArgsDefNode;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.TypeNode;

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
	
	public FunArgsDefNode getArgs() {
		return (FunArgsDefNode) children.get(1);
	}
	
	public FunBodyDefNode getBody() {
		return (FunBodyDefNode) children.get(2);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getReturnType() + ": " + ident.getIdentifier() + ")";
	}

}
