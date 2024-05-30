package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;

public class VoidTypeNode extends TypeNode {

	@Override
	public int getBytesSize() throws CompilerException {
		return 0;
	}

}
