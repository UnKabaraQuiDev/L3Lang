package lu.pcy113.l3.parser.ast.symbols;

import lu.pcy113.l3.parser.ast.fun.FunDefNode;

public class FunDefSymbol extends NodeSymbol<FunDefNode> {

	public FunDefSymbol(final FunDefNode node) {
		super(node);
	}

	public String name() {
		return this.node.getIdentifier().getValue();
	}

}
