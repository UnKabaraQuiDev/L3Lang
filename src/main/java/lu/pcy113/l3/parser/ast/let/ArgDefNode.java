package lu.pcy113.l3.parser.ast.let;

import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class ArgDefNode extends LetDefNode {

	public ArgDefNode(final TypeNode type, final IdentifierNode identifier) {
		super(type, identifier);
	}

}
