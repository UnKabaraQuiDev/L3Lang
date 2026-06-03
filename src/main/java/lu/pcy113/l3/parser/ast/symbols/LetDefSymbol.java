package lu.pcy113.l3.parser.ast.symbols;

import lu.pcy113.l3.impl.ASMNamed;
import lu.pcy113.l3.parser.ast.let.LetDefNode;

public class LetDefSymbol extends NodeSymbol<LetDefNode> implements ASMNamed {

	private static int STATIC_IDENT = 0;

	private String asmName;

	public LetDefSymbol(final LetDefNode node) {
		super(node);

		if (node.isStatic()) {
			this.static_(); // node.getIdentifier().getValue() + (int) Math.round(Math.random() * 1000)
		}
	}

	public void static_() {
		this.asmName = "static_let_" + LetDefSymbol.STATIC_IDENT++;
	}

	public void stack(final int offset) {
		this.asmName = "[rbp-" + offset + "]";
	}

	public void register(final String reg) {
		this.asmName = reg;
	}

	public void setName(final String asmName) {
		this.asmName = asmName;
	}

	@Override
	public String name() {
		return this.asmName;
	}

}
