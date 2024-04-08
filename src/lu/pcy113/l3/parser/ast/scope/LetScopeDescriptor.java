package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.Node;

public class LetScopeDescriptor extends ScopeDescriptor {

	private Node node;

	public LetScopeDescriptor(IdentifierToken ident, Node node) {
		super(ident);
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

}
