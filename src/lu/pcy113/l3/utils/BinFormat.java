package lu.pcy113.l3.utils;

public final class BinFormat {

	public static long fromBinDigitsToLong(String string) {
		long sta = 0;
		for (int i = 0; i < string.length(); i++) {
			sta |= string.charAt(i) == '1' ? 1 : 0;
			sta <<= 1;
		}
		sta <<= Long.BYTES*8*string.length();
		return sta;
	}

}
