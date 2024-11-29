package lu.pcy113.l3.compiler.x86_64;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86_64.memory.X86_64MemoryStatus;
import lu.pcy113.l3.compiler.x86_64.visitors.FunDefVisitor;
import lu.pcy113.l3.compiler.x86_64.visitors.LetDefVisitor;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.pclib.PCUtils;

public class X86_64Compiler extends L3Compiler {

	private X86_64MemoryStatus mem;

	public X86_64Compiler(RuntimeNode env, File outFile) {
		super(env, outFile);
	}

	@Override
	public void compile() {
		final FileCompilerUnit mainFu = compileFile(super.root.getMainNode(), super.root.getFiles(), true);

		for (FileNode otherFiles : super.root.getFiles()) {
			compileFile(otherFiles, Arrays.asList(), false);
		}

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

	private FileCompilerUnit compileFile(FileNode file, List<FileNode> others, boolean main) {
		System.out.println("--- Compiling file: " + file.getName() + " to: " + file.getPath());
		FileCompilerUnit fu = new FileCompilerUnit(super.outDir, file.getPath());

		fu.createFile();

		fw = fu.createWriter();

		fu.writeln("BITS 64");

		if (main) {
			fu.writetextln("global _start");
			fu.writeln("_start:");
		}

		compile(file, fu, main);

		fu.writeln("_static_ext__:");
		for (FileNode other : others) {
			final String staticName = "_static_" + other.getName();
			fu.writeinstln("call " + staticName);
			fu.writetextln("extern " + staticName);
		}
		fu.writeinstln("ret");

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

	private void compile(final FileNode file, final FileCompilerUnit fu, final boolean main) {

		final String staticName = "_static_" + file.getName();

		fu.writeinstln("call " + staticName);

		if (file.hasMain()) {
			final FunDefNode fun = file.getMain();

			fu.writeinstln("call " + file.getSymbols().get(fun).name());

			fu.writeln("exit:");
			fu.writeinstln("mov rdi, rax");
			// fu.writeinstln("mov rdi, 12");
			fu.writeinstln("mov rax, 60");
			fu.writeinstln("syscall");
		}
		
		fu.writeln(staticName + ":");
		fu.writetextln("global " + staticName);

		file.stream().filter((c) -> c instanceof LetDefNode).map(PCUtils::<LetDefNode>cast).forEach(letDef -> {
			LetDefVisitor.visit(letDef, file, fu);
		});

		if (main) {
			fu.writeinstln("call _static_ext__");
		}
		fu.writeinstln("ret");

		file.stream().filter((c) -> c instanceof FunDefNode).map(PCUtils::<FunDefNode>cast).forEach(fun -> {
			FunDefVisitor.visit(fun, file, fu);

			fu.writetextln("global " + file.getSymbols().get(fun).name());
		});
	}

	@Override
	public MemoryStatus getMemoryStatus() {
		return mem;
	}

}
