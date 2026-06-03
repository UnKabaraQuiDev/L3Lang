package lu.pcy113.l3.compiler.llvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.l3.L3Exception;

public final class NativeExecutableLinker {

	private static final String ENV_LINKER = "L3_LINKER";
	private static final String ENV_LINKER_ARGS = "L3_LINKER_ARGS";
	private static final String PROP_LINKER = "l3.linker";
	private static final String PROP_LINKER_ARGS = "l3.linker.args";
	private static final String PROP_ENTRY = "l3.linker.entry";

	private final String linker;
	private final List<String> linkerArgs;
	private final String entrySymbol;

	public NativeExecutableLinker() {
		this.linker = NativeExecutableLinker.firstNonBlank(System.getProperty(NativeExecutableLinker.PROP_LINKER),
				System.getenv(NativeExecutableLinker.ENV_LINKER), "ld");
		this.linkerArgs = NativeExecutableLinker.splitArgs(
				NativeExecutableLinker.firstNonBlank(System.getProperty(NativeExecutableLinker.PROP_LINKER_ARGS),
						System.getenv(NativeExecutableLinker.ENV_LINKER_ARGS), null));
		this.entrySymbol = NativeExecutableLinker.firstNonBlank(System.getProperty(NativeExecutableLinker.PROP_ENTRY),
				"_start");
	}

	public File link(final File objectFile) {
		final String objectName = objectFile.getName();
		final int dotIndex = objectName.lastIndexOf('.');
		final String executableName = dotIndex <= 0 ? objectName : objectName.substring(0, dotIndex);
		return this.link(objectFile, new File(objectFile.getParentFile(), executableName));
	}

	public File link(final File objectFile, final File executableFile) {
		if (objectFile == null) {
			throw new L3Exception("Cannot link executable: object file is null.");
		}
		if (!objectFile.isFile()) {
			throw new L3Exception("Cannot link executable: object file does not exist: " + objectFile);
		}
		if (executableFile == null) {
			throw new L3Exception("Cannot link executable: executable file is null.");
		}

		final File parent = executableFile.getParentFile();
		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new L3Exception("Could not create executable output directory: " + parent);
		}

		final List<String> command = this.buildCommand(objectFile, executableFile);
		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);

		final StringBuilder output = new StringBuilder();
		final int exitCode;
		try {
			final Process process = builder.start();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append(System.lineSeparator());
				}
			}
			exitCode = process.waitFor();
		} catch (final IOException e) {
			throw new L3Exception("Could not start linker command: " + String.join(" ", command), e);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new L3Exception("Linker command was interrupted: " + String.join(" ", command), e);
		}

		if (exitCode != 0) {
			throw new L3Exception("Linker failed with exit code " + exitCode + ": " + String.join(" ", command)
					+ System.lineSeparator() + output);
		}

		// On Unix this makes the result easier to run when the linker did not set the
		// bit.
		executableFile.setExecutable(true, false);
		return executableFile;
	}

	private List<String> buildCommand(final File objectFile, final File executableFile) {
		final List<String> command = new ArrayList<>();
		command.add(this.linker);

		if (this.linkerArgs.isEmpty()) {
			command.add("-o");
			command.add(executableFile.getAbsolutePath());
			command.add("-e");
			command.add(this.entrySymbol);
			command.add(objectFile.getAbsolutePath());
			return command;
		}

		for (final String arg : this.linkerArgs) {
			command.add(arg.replace("{object}", objectFile.getAbsolutePath())
					.replace("{output}", executableFile.getAbsolutePath()).replace("{entry}", this.entrySymbol));
		}
		return command;
	}

	private static String firstNonBlank(final String first, final String fallback) {
		return NativeExecutableLinker.firstNonBlank(first, null, fallback);
	}

	private static String firstNonBlank(final String first, final String second, final String fallback) {
		if (first != null && !first.isBlank()) {
			return first;
		}
		if (second != null && !second.isBlank()) {
			return second;
		}
		return fallback;
	}

	private static List<String> splitArgs(final String value) {
		if (value == null || value.isBlank()) {
			return List.of();
		}

		final List<String> args = new ArrayList<>();
		final StringBuilder current = new StringBuilder();
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;

		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			if (c == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
				continue;
			}
			if (c == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
				continue;
			}
			if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
				if (current.length() > 0) {
					args.add(current.toString());
					current.setLength(0);
				}
				continue;
			}
			current.append(c);
		}

		if (current.length() > 0) {
			args.add(current.toString());
		}
		return args;
	}
}
