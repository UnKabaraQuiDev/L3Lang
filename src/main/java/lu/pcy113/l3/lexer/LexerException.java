package lu.pcy113.l3.lexer;

import lu.pcy113.l3.L3Exception;

public class LexerException extends L3Exception {

	public LexerException(Throwable thr, String message, int line, int column, String value) {
		super("Exception at line "+line+":"+column+": "+message+" ("+value+")", thr);
	}
	public LexerException(Throwable thr, String message, int line, int column) {
		super("Exception at line "+line+":"+column+": "+message, thr);
	}
	public LexerException(String message, int line, int column) {
		super("Exception at line "+line+":"+column+": "+message);
	}
	public LexerException(String string) {
		super(string);
	}
	
}
