package lu.pcy113.l3.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {

	public static String getIncrement(final String filePath) {
		final String woExt = FileUtils.removeExtension(filePath);
		final String ext = FileUtils.getExtension(filePath);

		int index = 1;
		while (Files.exists(Paths.get(woExt + "-" + index + "." + ext))) {
			index++;
		}

		return woExt + "-" + index + "." + ext;
	}

	public static String readStringFile(final String filePath) {
		String str;
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException("File [" + filePath + "] does not exist");
		}
		try {
			str = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (final IOException excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}
		return str;
	}

	public static String recursiveTree(final String path) throws IOException {
		String list = "";
		// list all the files in the 'path' directory and add them to the string 'list'
		final File directory = new File(path);
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isFile()) {
					list += file + "\n";
				} else {
					list += FileUtils.recursiveTree(file.getCanonicalPath());
				}
			}
		}
		return list;
	}

	public static String appendName(final String path, final String suffix) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1" + suffix + "$2");
	}

	public static String replaceExtension(final String path, final String ext) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1." + ext);
	}

	public static String removeExtension(final String path) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1");
	}

	public static String getExtension(final String path) {
		return path.replaceAll("(.+\\.)([^.]+)$", "$2");
	}

}
