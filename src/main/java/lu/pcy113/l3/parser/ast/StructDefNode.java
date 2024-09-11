package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class StructDefNode extends ScopeContainerNode {

	public StructDefNode(IdentifierLitNode lit) {
		add(lit);
	}

	public IdentifierLitNode getIdent() {
		return (IdentifierLitNode) children.get(0);
	}

}
