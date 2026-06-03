package lu.pcy113.l3.lexer;

import lu.pcy113.l3.L3Exception;

public class LexerException extends L3Exception {

	public LexerException(final Throwable thr, final String message, final int line, final int column, final String value) {
		super("Exception at line " + line + ":" + column + ": " + message + " (" + value + ")", thr);
	}

	public LexerException(final Throwable thr, final String message, final int line, final int column) {
		super("Exception at line " + line + ":" + column + ": " + message, thr);
	}

	public LexerException(final String message, final int line, final int column) {
		super("Exception at line " + line + ":" + column + ": " + message);
	}

	public LexerException(final String string) {
		super(string);
	}

}
