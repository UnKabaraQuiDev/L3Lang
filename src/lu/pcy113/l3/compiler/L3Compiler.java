package lu.pcy113.l3.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import lu.pcy113.l3.parser.ast.scope.RuntimeNode;

public abstract class L3Compiler {

	protected RuntimeNode root;
	protected File outFileAsm, outFileObj, outFileExec, outDir;
	protected FileWriter fw;
	protected StringBuilder dataBuilder, textBuilder;

	public L3Compiler(RuntimeNode env, File outFile) {
		this.root = env;
		this.outDir = outFile.getAbsoluteFile().getParentFile();
		this.outFileAsm = new File(outFile.getPath()+".asm");
		this.outFileObj = new File(outFile.getPath()+".o");
		this.outFileExec = outFile;
	}

	public abstract void compile() throws CompilerException;

	private int sectionIndex = 1;

	protected String newSection() {
		return "sec_" + (sectionIndex++);
	}

	private int varIndex = 1;

	protected String newVar() {
		return "var_" + (varIndex++);
	}

	protected void exec(String cmd, File dir) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(cmd);

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

	protected void writeinstln(String string) throws CompilerException {
		writeln("\t" + string);
	}

	protected void writedataln(String string) throws CompilerException {
		dataBuilder.append("\t" + string + "\n");
	}

	protected void writetextln(String string) throws CompilerException {
		textBuilder.append("\t" + string + "\n");
	}

	protected void writeln(String string) throws CompilerException {
		try {
			fw.write(string + "\n");
		} catch (IOException e) {
			throw new CompilerException("Could not write to output writer to: " + outFileAsm, e);
		}
	}

	protected void appendData() throws CompilerException {
		try {
			fw.write("section .data\n");
			fw.write(dataBuilder.toString());
		} catch (IOException e) {
			throw new CompilerException("Could not append data section to output writer: " + outFileAsm, e);
		}
	}

	protected void appendText() throws CompilerException {
		try {
			fw.write("section .text\n");
			fw.write(textBuilder.toString());
		} catch (IOException e) {
			throw new CompilerException("Could not append data section to output writer: " + outFileAsm, e);
		}
	}

	protected void flushAndClose() throws CompilerException {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			throw new CompilerException("Could not flush and close output writer to: " + outFileAsm, e);
		}
	}

	protected FileWriter createWriter() throws CompilerException {
		try {
			dataBuilder = new StringBuilder();
			textBuilder = new StringBuilder();
			return new FileWriter(outFileAsm);
		} catch (IOException e) {
			throw new CompilerException("Could not create output writer to: " + outFileAsm, e);
		}
	}

	protected void createFile() throws CompilerException {
		try {
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			outFileAsm.createNewFile();
		} catch (IOException e) {
			throw new CompilerException("Could not create output file: " + outFileAsm, e);
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

	public File getOutFileExec() {
		return outFileExec;
	}

	public File getOutFileObj() {
		return outFileObj;
	}

	public File getOutDir() {
		return outDir;
	}

}
