package lu.pcy113.l3.compiler;

import lu.pcy113.l3.L3Exception;

public class CompilerException extends L3Exception {

	public CompilerException(String string, Throwable e) {
		super(string, e);
	}

	public CompilerException(String string) {
		super(string);
	}

}
