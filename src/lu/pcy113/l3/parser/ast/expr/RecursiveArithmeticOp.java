package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.compiler.CompilerException;

public interface RecursiveArithmeticOp {

	boolean isDecimal() throws CompilerException;

	boolean isInteger() throws CompilerException;

}
