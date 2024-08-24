package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;

public interface RecursiveArithmeticOp {

	boolean isDouble() throws CompilerException;

	boolean isInteger() throws CompilerException;

	boolean isFloat() throws CompilerException;

}
