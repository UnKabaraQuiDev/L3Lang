package lu.pcy113.l3.compiler.x86_64;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86_64.memory.X86_64MemoryStatus;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.pclib.PCUtils;

public class X86_64Compiler extends L3Compiler {

	private X86_64MemoryStatus mem;

	public X86_64Compiler(RuntimeNode env, File outFile) {
		super(env, outFile);
	}

	@Override
	public void compile() {
		final FileCompilerUnit mainFu = compileFile(super.root.getMainNode());

		String inFiles = "./" + PCUtils.replaceFileExtension(super.root.getMainNode().getPath(), "o");
		for (FileNode p : super.root.getFiles()) {
			inFiles += " ./" + PCUtils.replaceFileExtension(p.getPath(), "o");
		}
		try {
			System.out.println(PCUtils.recursiveTree(outDir.getPath()));

			exec("ld -o ./" + outFileExec.getName() + " " + inFiles, outDir);

			exec("./" + outFileExec.getName(), outDir);
		} catch (IOException | InterruptedException e) {
			throw new L3Exception("Could not exec: '" + outFileExec.getPath() + "' and '" + inFiles + "' in " + outDir.getParent(), e);
		}
	}

	private FileCompilerUnit compileFile(FileNode file) {
		System.out.println("--- Compiling file: " + file.getName());
		FileCompilerUnit fu = new FileCompilerUnit(super.outDir, file.getPath());

		fu.createFile();

		fw = fu.createWriter();

		fu.writetextln("global _start");

		fu.writeln("BITS 64");
		fu.writeln("_start:");

		compile(file, fu);

		fu.appendBSS();
		fu.appendText();
		fu.appendData();

		fu.flushAndClose();

		try {
			super.exec("nasm -f elf64 -g -o " + fu.getOutFileObj().getPath() + " " + fu.getOutFileAsm().getPath(), outDir);
		} catch (IOException | InterruptedException e) {
			throw new L3Exception("Could not build: '" + fu.getOutFileAsm().getPath() + "', '" + outFileExec.getPath() + "' and '" + fu.getOutFileObj().getPath() + "' in " + outDir.getParent(), e);
		}

		return fu;
	}

	private void compile(final FileNode file, final FileCompilerUnit fu) {

		if (file.hasMain()) {
			final FunDefNode funDef = file.getMain();

			fu.writeln("normal_exit:");
			fu.writeinstln("mov rdi, 12");
			fu.writeinstln("mov rax, 60");
			fu.writeinstln("syscall");
		}

	}

	@Override
	public MemoryStatus getMemoryStatus() {
		return mem;
	}

}
