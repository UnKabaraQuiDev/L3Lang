package lu.pcy113.l3.parser.ast.type;

public class VoidTypeNode extends TypeNode {

	public VoidTypeNode() {
		super("void");
	}

	@Override
	public int computeSize() {
		return 0;
	}

}
