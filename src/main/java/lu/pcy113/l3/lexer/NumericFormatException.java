package lu.pcy113.l3.lexer;

public class NumericFormatException extends Exception {

	public NumericFormatException(NumberFormatException e) {
		super(e);
	}

}
