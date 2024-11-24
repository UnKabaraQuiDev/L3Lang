package lu.pcy113.l3.parser.ast.symbols;

import lu.pcy113.l3.impl.ASMNamed;
import lu.pcy113.l3.parser.ast.let.LetDefNode;

public class LetDefSymbol extends NodeSymbol<LetDefNode> implements ASMNamed {

	private String name;

	public LetDefSymbol(LetDefNode node) {
		super(node);

		if (node.isStatic()) {
			this._static(node.getIdentifier().getValue() + (int) Math.round(Math.random() * 1000));
		}
	}

	public void _static(String name) {
		this.name = name;
	}

	public void relativeStack(int offset) {
		this.name = "RBP-" + offset;
	}

	@Override
	public String name() {
		return name;
	}

}
