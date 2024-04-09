package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.FunArgsDefNode;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;

public class FunDefNode extends ScopeContainerNode {

	private Token returnType;
	private IdentifierToken ident;

	public FunDefNode(Token returnType, IdentifierToken ident) {
		// FunDefArgsNode
		this.returnType = returnType;
		this.ident = ident;
	}

	public Token getReturnType() {
		return returnType;
	}

	public IdentifierToken getIdent() {
		return ident;
	}
	
	public FunArgsDefNode getArgs() {
		return (FunArgsDefNode) children.get(0);
	}
	
	public FunBodyDefNode getBody() {
		return (FunBodyDefNode) children.get(1);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + returnType.getType().getValue() + ": " + ident.getIdentifier() + ")";
	}

}
