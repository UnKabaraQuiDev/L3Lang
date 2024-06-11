package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class VoidTypeNode extends TypeNode {

	@Override
	public void normalizeSize() throws CompilerException {
		// do nothing
	}

	@Override
	public boolean typeMatches(ExprNode param) throws CompilerException {
		return false; // TODO ?
	}

	@Override
	public int getBytesSize() throws CompilerException {
		return 0;
	}

	@Override
	public void setBytesSize(int bytes) {
		// does nothing
	}

}
