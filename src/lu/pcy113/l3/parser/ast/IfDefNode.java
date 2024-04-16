package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class IfDefNode extends ScopeContainerNode {
	
	public IfDefNode(TypeNode returnType, IdentifierToken ident) {
		add(returnType);
		this.ident = ident;
	}

	public FunArgsDefNode getArgs() {
		return (FunArgsDefNode) children.get(1);
	}
	
	public FunBodyDefNode getBody() {
		return (FunBodyDefNode) children.get(2);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getReturnType() + ": " + ident.getValue() + ")";
	}
	
}
