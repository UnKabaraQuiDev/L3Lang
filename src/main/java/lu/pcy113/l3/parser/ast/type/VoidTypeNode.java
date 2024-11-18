package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class VoidTypeNode extends TypeNode {

	@Override
<<<<<<< HEAD
	public void normalizeSize(ScopeContainer container) throws CompilerException {
=======
	public void normalizeSize(ScopeContainer container) {
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
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
