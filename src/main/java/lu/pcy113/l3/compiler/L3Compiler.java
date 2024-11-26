package lu.pcy113.l3.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;
import lu.pcy113.pclib.PCUtils;

public abstract class L3Compiler {

	protected RuntimeNode root;
	protected File outDir, outFileExec;
	protected FileWriter fw;

	public L3Compiler(RuntimeNode env, File outDir) {
		this.root = env;
		this.outDir = outDir;
		this.outFileExec = new File(PCUtils.removeFileExtension(outDir.getPath() + "/" + env.getMainNode().getName()));
	}

	public abstract void compile();

	private int sectionIndex = 1;

	public String newSection() {
		return "sec_" + (sectionIndex++);
	}

	protected void exec(String cmd, File dir) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
		processBuilder.directory(dir);

		Process process = processBuilder.start();

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

		int exitCode = process.waitFor();
		System.out.println("--- Process exited with code: " + exitCode);
	}

	public abstract MemoryStatus getMemoryStatus();

	public File getOutFileExec() {
		return outFileExec;
	}

	public class FileCompilerUnit {

		protected File outFileAsm, outFileObj;
		protected StringBuilder dataBuilder, textBuilder, bssBuilder;

		public FileCompilerUnit(File outDir, String path) {
			outFileAsm = new File(PCUtils.replaceFileExtension(path, "asm"));
			outFileObj = new File(PCUtils.replaceFileExtension(path, "o"));
		}

		public void writeinstln(String string) {
			writeln("\t" + string);
		}

		public void writeinstln(String string, String comment) {
			writeinstln(string + "\t; " + comment);
		}

		public void writedataln(String string) {
			dataBuilder.append("\t" + string + "\n");
		}

		public void writebssln(String string) {
			bssBuilder.append("\t" + string + "\n");
		}

		public void writetextln(String string) {
			textBuilder.append("\t" + string + "\n");
		}

		public void writeln(String string) {
			try {
				fw.write(string + "\n");
				fw.flush();
			} catch (IOException e) {
				throw new L3Exception("Could not write to output writer to: " + outFileAsm, e);
			}
		}

		public void appendData() {
			try {
				fw.write("section .data\n");
				fw.write(dataBuilder.toString());
			} catch (IOException e) {
				throw new L3Exception("Could not append data section to output writer: " + outFileAsm, e);
			}
		}

		public void appendBSS() {
			try {
				fw.write("section .bss\n");
				fw.write(bssBuilder.toString());
			} catch (IOException e) {
				throw new L3Exception("Could not append bss section to output writer: " + outFileAsm, e);
			}
		}

		public void appendText() {
			try {
				fw.write("section .text\n");
				fw.write(textBuilder.toString());
			} catch (IOException e) {
				throw new L3Exception("Could not append text section to output writer: " + outFileAsm, e);
			}
		}

		public void flushAndClose() {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				throw new L3Exception("Could not flush and close output writer to: " + outFileAsm, e);
			}
		}

		public FileWriter createWriter() {
			final File realFile = new File(outDir, outFileAsm.getPath());

			try {
				dataBuilder = new StringBuilder();
				textBuilder = new StringBuilder();
				bssBuilder = new StringBuilder();
				return new FileWriter(realFile);
			} catch (IOException e) {
				throw new L3Exception("Could not create output writer to: " + outFileAsm, e);
			}
		}

		public void createFile() {
			final File realFile = new File(outDir, outFileAsm.getPath());

			try {
				if (!outDir.exists()) {
					outDir.mkdirs();
				}

				if (!realFile.getParentFile().exists()) {
					realFile.getParentFile().mkdirs();
				}

				realFile.createNewFile();
			} catch (IOException e) {
				throw new L3Exception("Could not create output file: " + realFile, e);
			}
		}

		public RuntimeNode getInput() {
			return root;
		}

		public FileWriter getFw() {
			return fw;
		}

		public File getOutFileAsm() {
			return outFileAsm;
		}

		public File getOutFileObj() {
			return outFileObj;
		}

		public File getOutDir() {
			return outDir;
		}

		public void implement() {
			throw new L3Exception("Not implemented.");
		}

		public void implement(Object obj) {
			throw new L3Exception("Not implemented (" + obj.getClass() + "): " + obj + ".");
		}

	}

}
