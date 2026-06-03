package lu.pcy113.l3.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86_64.memory.X86_64MemoryStatus;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;

import lu.kbra.pclib.PCUtils;

public abstract class L3Compiler {

	protected RuntimeNode root;
	protected File outDir, outFileExec;
	protected FileWriter fw;
	protected X86_64MemoryStatus memory = new X86_64MemoryStatus();

	public L3Compiler(final RuntimeNode env, final File outDir) {
		this.root = env;
		this.outDir = outDir;
		this.outFileExec = new File(PCUtils.removeFileExtension(outDir.getPath() + "/" + env.getMainNode().getName()));
	}

	public abstract void compile();

	private int sectionIndex = 1;

	public String newSection() {
		return "sec_" + this.sectionIndex++;
	}

	protected void exec(final String cmd, final File dir) throws IOException, InterruptedException {
		final ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
		processBuilder.directory(dir);

		final Process process = processBuilder.start();

		System.out.println("--- Process: " + cmd + ", in " + dir);

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();

		reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((line = reader.readLine()) != null) {
			System.err.println(line);
		}

		final int exitCode = process.waitFor();
		System.out.println("--- Process exited with code: " + exitCode);
	}

	public abstract MemoryStatus getMemoryStatus();

	public File getOutFileExec() {
		return this.outFileExec;
	}

	public class FileCompilerUnit {

		protected File outFileAsm, outFileObj;
		protected StringBuilder dataBuilder, textBuilder, bssBuilder;

		public FileCompilerUnit(final File outDir, final String path) {
			this.outFileAsm = new File(PCUtils.replaceFileExtension(path, "asm"));
			this.outFileObj = new File(PCUtils.replaceFileExtension(path, "o"));
		}

		public void writeinstln(final String string) {
			this.writeln("\t" + string);
		}

		public static final int TAB_WIDTH = 4;
		private final int commentIndent = 5 * FileCompilerUnit.TAB_WIDTH;

		public void writeinstln(final String string, final String comment) {
			this.writeinstln(
					string + "\t".repeat(PCUtils.snap(this.commentIndent - string.length(), FileCompilerUnit.TAB_WIDTH)
							/ FileCompilerUnit.TAB_WIDTH) + "; " + comment);
		}

		public void writedataln(final String string) {
			this.dataBuilder.append("\t" + string + "\n");
		}

		public void writebssln(final String string) {
			this.bssBuilder.append("\t" + string + "\n");
		}

		public void writetextln(final String string) {
			this.textBuilder.append("\t" + string + "\n");
		}

		public void writeln(final String string) {
			try {
				L3Compiler.this.fw.write(string + "\n");
				L3Compiler.this.fw.flush();
			} catch (final IOException e) {
				throw new L3Exception("Could not write to output writer to: " + this.outFileAsm, e);
			}
		}

		public void appendData() {
			try {
				L3Compiler.this.fw.write("section .data\n");
				L3Compiler.this.fw.write(this.dataBuilder.toString());
			} catch (final IOException e) {
				throw new L3Exception("Could not append data section to output writer: " + this.outFileAsm, e);
			}
		}

		public void appendBSS() {
			try {
				L3Compiler.this.fw.write("section .bss\n");
				L3Compiler.this.fw.write(this.bssBuilder.toString());
			} catch (final IOException e) {
				throw new L3Exception("Could not append bss section to output writer: " + this.outFileAsm, e);
			}
		}

		public void appendText() {
			try {
				L3Compiler.this.fw.write("section .text\n");
				L3Compiler.this.fw.write(this.textBuilder.toString());
			} catch (final IOException e) {
				throw new L3Exception("Could not append text section to output writer: " + this.outFileAsm, e);
			}
		}

		public void flushAndClose() {
			try {
				L3Compiler.this.fw.flush();
				L3Compiler.this.fw.close();
			} catch (final IOException e) {
				throw new L3Exception("Could not flush and close output writer to: " + this.outFileAsm, e);
			}
		}

		public FileWriter createWriter() {
			final File realFile = new File(L3Compiler.this.outDir, this.outFileAsm.getPath());

			try {
				this.dataBuilder = new StringBuilder();
				this.textBuilder = new StringBuilder();
				this.bssBuilder = new StringBuilder();
				return new FileWriter(realFile);
			} catch (final IOException e) {
				throw new L3Exception("Could not create output writer to: " + this.outFileAsm, e);
			}
		}

		public void createFile() {
			final File realFile = new File(L3Compiler.this.outDir, this.outFileAsm.getPath());

			try {
				if (!L3Compiler.this.outDir.exists()) {
					L3Compiler.this.outDir.mkdirs();
				}

				if (!realFile.getParentFile().exists()) {
					realFile.getParentFile().mkdirs();
				}

				realFile.createNewFile();
			} catch (final IOException e) {
				throw new L3Exception("Could not create output file: " + realFile, e);
			}
		}

		public RuntimeNode getInput() {
			return L3Compiler.this.root;
		}

		public FileWriter getFw() {
			return L3Compiler.this.fw;
		}

		public File getOutFileAsm() {
			return this.outFileAsm;
		}

		public File getOutFileObj() {
			return this.outFileObj;
		}

		public File getOutDir() {
			return L3Compiler.this.outDir;
		}

		public X86_64MemoryStatus getMemory() {
			return L3Compiler.this.memory;
		}

		public void implement() {
			throw new L3Exception("Not implemented.");
		}

		public void implement(final Object obj) {
			throw new L3Exception("Not implemented (" + obj.getClass() + "): " + obj + ".");
		}

	}

}
