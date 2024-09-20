package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public abstract class TypeNode extends Node {
	
	protected boolean sizeOverride = false;
	protected int bytesOverride = -1;
	
	public abstract int getBytesSize();

	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

	public abstract boolean typeMatches(ExprNode param) throws CompilerException;

	public abstract void normalizeSize(ScopeContainer container);

}
