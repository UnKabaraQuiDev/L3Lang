package lu.pcy113.l3;

public class L3Exception extends RuntimeException {

	public L3Exception(final String string) {
		super(string);
	}

	public L3Exception(final String string, final Throwable th) {
		super(string, th);
	}

}
