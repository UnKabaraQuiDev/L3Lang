package lu.pcy113.l3.parser.ast.expr;

import lu.pcy113.l3.L3Exception;

public interface RecursiveArithmeticOp {

	boolean isDecimal() throws L3Exception;

	boolean isInteger() throws L3Exception;

}
