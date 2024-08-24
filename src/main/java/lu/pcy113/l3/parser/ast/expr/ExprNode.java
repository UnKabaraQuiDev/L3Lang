package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;

public abstract class ExprNode extends Node {

	public abstract boolean isDouble() throws CompilerException;

	public abstract boolean isInteger() throws CompilerException;

	public abstract boolean isFloat() throws CompilerException;
	
}
