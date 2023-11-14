package lu.pcy113.l3.lexer;

public class LexerException extends Exception {

	public LexerException(Throwable thr, String message, int line, int column, String value) {
		super("Exception at line "+line+":"+column+": "+message, thr);
	}

	public LexerException(String string) {
		super(string);
	}
	
}
