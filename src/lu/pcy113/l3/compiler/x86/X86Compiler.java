package lu.pcy113.l3.compiler.x86;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.x86.consumers.X86_FileConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_LetDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_PackageDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_RuntimeConsumer;
import lu.pcy113.l3.compiler.x86.memory.X86MemoryStatus;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.pclib.GlobalLogger;

public class X86Compiler extends L3Compiler {

	private X86MemoryStatus memoryStatus = new X86MemoryStatus();

	private X86_RuntimeConsumer runtimeConsumer = new X86_RuntimeConsumer();
	private X86_PackageDefConsumer packageDefConsumer = new X86_PackageDefConsumer();
	private X86_LetDefConsumer letDefConsumer = new X86_LetDefConsumer();
	private X86_FileConsumer fileConsumer = new X86_FileConsumer();

	public X86Compiler(RuntimeNode env, File binDirPath, String fileName) {
		super(env, new File(binDirPath, fileName));

		runtimeConsumer.attach(this);
		packageDefConsumer.attach(this);
		letDefConsumer.attach(this);
		fileConsumer.attach(this);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();
		
		writetextln("global _start");
		writedataln("esp_start dd 0");
		
		writeln("BITS 64");
		writeln("_start:");

		compile(root);

		appendText();
		appendData();

		flushAndClose();

		try {

			String oOutFile = outFileObj.getPath();
			String execOutFile = outFileExec.getPath();
			String outAsmPath = outFileAsm.getPath();

			exec("nasm -f elf32 -g -o " + oOutFile + " " + outAsmPath, outDir);
			// exec("gcc -m32 elf_i386 -o " + rmExtOutFile + " " + oOutFile, dir);
			exec("ld -m elf_i386 -o " + execOutFile + " " + oOutFile, outDir);
			exec("./" + execOutFile, outDir);

		} catch (IOException | InterruptedException e) {
			throw new CompilerException("Could not exec: '" + outFileAsm.getPath() + "', '" + outFileExec.getPath() + "' and '" + outFileObj.getPath() + "' in " + outDir.getParent(), e);
		}
	}

	public void compile(Node node) throws CompilerException {
		if (node instanceof RuntimeNode) {
			runtimeConsumer.accept((RuntimeNode) node);
		} else if (node instanceof FileNode) {
			fileConsumer.accept((FileNode) node);
		} else if (node instanceof PackageDefNode) {
			packageDefConsumer.accept((PackageDefNode) node);
		} else if (node instanceof LetDefNode) {
			letDefConsumer.accept((LetDefNode) node);
		}
	}

	public void compilePrintWrite() throws CompilerException {
		// pointer in ecx
		writeinstln("; Print write");
		writeinstln("mov eax, 4  ; Write");
		writeinstln("mov ebx, 1  ; Stdout");
		writeinstln("mov edx, 1  ; Length");
		writeinstln("int 0x80  ; Syscall");
	}

	public void implement() throws CompilerException {
		throw new CompilerException("not implemented; ");
	}

	public void implement(Object obj) throws CompilerException {
		throw new CompilerException("not implemented: " + obj.getClass().getName());
	}

	public void warning(String msg) {
		GlobalLogger.warning(msg);
	}

	public void warning(String pos, String msg) {
		GlobalLogger.warning("[" + pos + ")" + msg);
	}

	@Override
	public X86MemoryStatus getMemoryStatus() {
		return memoryStatus;
	}

}
