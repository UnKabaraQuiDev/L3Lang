package lu.pcy113.l3.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import lu.pcy113.l3.parser.ast.RuntimeNode;

public abstract class L3Compiler {

	protected RuntimeNode root;
	protected File outFile;
	protected FileWriter fw;
	protected StringBuilder dataBuilder;

	public L3Compiler(RuntimeNode env, String outPath) {
		this.root = env;
		this.outFile = new File(outPath);
	}

	public abstract void compile() throws CompilerException;

	private int sectionIndex = 1;
	protected String newSection() {
		return "_sec_"+(sectionIndex++);
	}
	
	private int varIndex = 1;
	protected String newVar() {
		return "var_"+(varIndex++);
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
		dataBuilder.append("\t"+string+"\n");
	}

	protected void writeln(String string) throws CompilerException {
		try {
			fw.write(string + "\n");
		} catch (IOException e) {
			throw new CompilerException("Could not write to output writer to: " + outFile, e);
		}
	}

	protected void appendData() throws CompilerException {
		try {
			fw.write("section .data\n");
			fw.write(dataBuilder.toString());
		} catch (IOException e) {
			throw new CompilerException("Could not append data section to output writer: " + outFile, e);
		}
	}
	
	protected void flushAndClose() throws CompilerException {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			throw new CompilerException("Could not flush and close output writer to: " + outFile, e);
		}
	}

	protected FileWriter createWriter() throws CompilerException {
		try {
			dataBuilder = new StringBuilder();
			return new FileWriter(outFile);
		} catch (IOException e) {
			throw new CompilerException("Could not create output writer to: " + outFile, e);
		}
	}

	protected void createFile() throws CompilerException {
		try {
			if (!outFile.getAbsoluteFile().getParentFile().exists()) {
				outFile.getAbsoluteFile().getParentFile().mkdirs();
			}
			outFile.createNewFile();
		} catch (IOException e) {
			throw new CompilerException("Could not create output file: " + outFile, e);
		}
	}

	public RuntimeNode getInput() {
		return root;
	}

	public FileWriter getFw() {
		return fw;
	}

	public File getOutFile() {
		return outFile;
	}

}
