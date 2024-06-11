package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public abstract class TypeNode extends Node {
	
	protected boolean sizeOverride = false;
	protected int bytesOverride = -1;
	
	public abstract int getBytesSize() throws CompilerException;

	public abstract void setBytesSize(int bytes);

	public abstract boolean typeMatches(ExprNode param) throws CompilerException;

	public abstract void normalizeSize() throws CompilerException;

}
