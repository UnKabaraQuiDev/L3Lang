package lu.pcy113.l3.parser.ast.abstr;

import lu.pcy113.l3.impl.ASMNamed;

public class ASMNamedNode implements ASMNamed {

	private static int COUNT = 1;

	private final String name = Integer.toString(ASMNamedNode.COUNT++);

	@Override
	public String name() {
		return this.name;
	}

}
