package lu.pcy113.l3.compiler;

public class CompilerException extends Exception {

	public CompilerException(String string, Throwable e) {
		super(string, e);
	}

	public CompilerException(String string) {
		super(string);
	}

}
