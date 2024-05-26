package lu.pcy113.l3.compiler.ast;

import lu.pcy113.l3.L3Exception;

public interface RecursiveArithmeticOp {

	boolean isFloat() throws L3Exception;

	boolean isInt() throws L3Exception;

}
