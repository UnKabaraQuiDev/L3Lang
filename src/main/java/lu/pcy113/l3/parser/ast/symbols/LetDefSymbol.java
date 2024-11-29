package lu.pcy113.l3.parser.ast.symbols;

import lu.pcy113.l3.impl.ASMNamed;
import lu.pcy113.l3.parser.ast.let.LetDefNode;

public class LetDefSymbol extends NodeSymbol<LetDefNode> implements ASMNamed {

	private static int STATIC_IDENT = 0;
	
	private String asmName;

	public LetDefSymbol(LetDefNode node) {
		super(node);

		if (node.isStatic()) {
			this.static_(); // node.getIdentifier().getValue() + (int) Math.round(Math.random() * 1000)
		}
	}

	public void static_() {
		asmName = "static_let_" + STATIC_IDENT++;
	}

	public void stack(int offset) {
		asmName = "[rbp-" + offset + "]";
	}

	public void register(String reg) {
		asmName = reg;
	}

	public void setName(String asmName) {
		this.asmName = asmName;
	}
	
	@Override
	public String name() {
		return asmName;
	}

}
