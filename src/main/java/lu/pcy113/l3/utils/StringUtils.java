package lu.pcy113.l3.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class StringUtils {

	public static String readAll(final Reader reader) throws IOException {
		try (BufferedReader r = new BufferedReader(reader)) {
			final StringBuilder stringBuilder = new StringBuilder();
			String line;

			while ((line = r.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			return stringBuilder.toString();
		}
	}

	public static String repeat(final String string, final int indent) {
		String str = "";
		for (int i = 0; i < indent; i++) {
			str += string;
		}
		return str;
	}

}
