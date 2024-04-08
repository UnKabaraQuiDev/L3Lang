package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

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

	@Override
	public String toString() {
		return super.toString() + "(" + returnType.getType().getValue() + ": " + ident.getIdentifier() + ")";
	}

}
