package lu.pcy113.l3.compiler.llvm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;

import lu.kbra.pclib.PCUtils;

public class LwjglLlvmCompiler extends L3Compiler {

	private File outFileIr;
	private File outFileObj;

	public LwjglLlvmCompiler(final RuntimeNode env, final File outDir) {
		super(env, outDir);
	}

	@Override
	public void compile() {
		if (!this.outDir.exists() && !this.outDir.mkdirs()) {
			throw new L3Exception("Could not create output directory: " + this.outDir);
		}

		String moduleBaseName = PCUtils.removeFileExtension(this.root.getMainNode().getName());
		if (moduleBaseName == null || moduleBaseName.isBlank()) {
			moduleBaseName = "main";
		}
		this.outFileIr = new File(this.outDir, moduleBaseName + ".ir");
		this.outFileObj = new File(this.outDir, moduleBaseName + ".o");

		final String ir = new LlvmIrGenerator(this.root).generate();
		this.writeFile(this.outFileIr, ir);

		try {
			new LwjglLlvmDriver().compileIr(ir, this.outFileIr, this.outFileObj);
		} catch (final RuntimeException ex) {
			throw ex;
		} catch (final Exception ex) {
			throw new L3Exception(
					"LLVM IR was written to " + this.outFileIr + ", but LWJGL/LLVM could not emit an object file. "
							+ "Make sure the LWJGL LLVM native dependency for your platform is present.",
					ex);
		}
	}

	private void writeFile(final File file, final String text) {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(text);
		} catch (final IOException e) {
			throw new L3Exception("Could not write LLVM IR file: " + file, e);
		}
	}

	public File getOutFileIr() {
		return this.outFileIr;
	}

	public File getOutFileObj() {
		return this.outFileObj;
	}

	@Override
	public MemoryStatus getMemoryStatus() {
		return null;
	}
}
