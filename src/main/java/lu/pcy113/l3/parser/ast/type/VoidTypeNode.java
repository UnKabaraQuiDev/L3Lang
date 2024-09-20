package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class VoidTypeNode extends TypeNode {

	@Override
	public void normalizeSize(ScopeContainer container) {
		// do nothing
	}

	@Override
	public boolean typeMatches(ExprNode param) throws CompilerException {
		return false; // TODO ?
	}

	@Override
	public int getBytesSize() {
		return 0;
	}

	@Override
	public void setBytesSize(int bytes) {
		// does nothing
	}

}
