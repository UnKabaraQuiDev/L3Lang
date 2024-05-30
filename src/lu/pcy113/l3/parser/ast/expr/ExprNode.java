package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;

public abstract class ExprNode extends Node {

	public abstract boolean isDecimal() throws CompilerException;

	public abstract boolean isInteger() throws CompilerException;

}
