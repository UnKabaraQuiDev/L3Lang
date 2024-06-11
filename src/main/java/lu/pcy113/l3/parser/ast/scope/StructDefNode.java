package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class StructDefNode extends ScopeContainerNode {

	private IdentifierToken ident;

	public StructDefNode(IdentifierToken ident) {
		this.ident = ident;
	}

	public int getSize() {
		return children.stream().mapToInt(c -> ((LetDefNode) c).getStackSize()).sum();
	}
		
	public IdentifierToken getIdent() {
		return ident;
	}

}
