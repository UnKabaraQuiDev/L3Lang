package lu.pcy113.l3.utils;

public final class BinFormat {

	public static long fromBinDigitsToLong(String str) {
		// Check if the input string is not null or empty
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        // Remove any leading "0b" if present
        if (str.startsWith("0b") || str.startsWith("0B")) {
            str = str.substring(2);
        }

        // Convert the binary string to a long
        try {
            return Long.parseLong(str, 2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid binary string: " + str, e);
        }
	}

}
