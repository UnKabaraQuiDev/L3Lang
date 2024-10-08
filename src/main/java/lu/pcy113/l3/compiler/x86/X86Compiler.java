package lu.pcy113.l3.compiler.x86;

import java.io.File;
import java.io.IOException;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.x86.consumers.X86_ArrayAccessConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_BinaryOpConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_ExplicitArrayDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_FieldAccessConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_FileConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_ForDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_FunCallConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_FunDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_IfContainerConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_LetDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_LetRefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_LetSetConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_NumLitConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_PackageDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_PointerDerefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_PointerDerefSetConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_RegisterValueConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_ReturnConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_RuntimeConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_ScopeBodyConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_StructDefConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_UnaryOpConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_UserTypeAllocConsumer;
import lu.pcy113.l3.compiler.x86.consumers.X86_WhileDefConsumer;
import lu.pcy113.l3.compiler.x86.memory.X86MemoryStatus;
import lu.pcy113.l3.parser.ast.ArrayAccessNode;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.ForDefNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetRefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.PointerDerefSetNode;
import lu.pcy113.l3.parser.ast.RegisterValueNode;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.WhileDefNode;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.ExplicitArrayDefNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.expr.UnaryOpNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86Compiler extends L3Compiler {

	private X86MemoryStatus memoryStatus = new X86MemoryStatus();

	private X86_RuntimeConsumer runtimeConsumer = new X86_RuntimeConsumer();
	private X86_PackageDefConsumer packageDefConsumer = new X86_PackageDefConsumer();
	private X86_LetDefConsumer letDefConsumer = new X86_LetDefConsumer();
	private X86_FileConsumer fileConsumer = new X86_FileConsumer();
	private X86_FunDefConsumer funDefConsumer = new X86_FunDefConsumer();
	private X86_ReturnConsumer returnConsumer = new X86_ReturnConsumer();
	private X86_NumLitConsumer numLitConsumer = new X86_NumLitConsumer();
	private X86_BinaryOpConsumer binaryOpConsumer = new X86_BinaryOpConsumer();
	private X86_FieldAccessConsumer fieldAccessConsumer = new X86_FieldAccessConsumer();
	private X86_FunCallConsumer funCallConsumer = new X86_FunCallConsumer();
	private X86_UnaryOpConsumer unaryOpConsumer = new X86_UnaryOpConsumer();
	private X86_IfContainerConsumer ifContainerConsumer = new X86_IfContainerConsumer();
	private X86_ScopeBodyConsumer scopeBodyConsumer = new X86_ScopeBodyConsumer();
	private X86_ForDefConsumer forDefConsumer = new X86_ForDefConsumer();
	private X86_WhileDefConsumer whileDefConsumer = new X86_WhileDefConsumer();
	private X86_LetSetConsumer letSetConsumer = new X86_LetSetConsumer();
	private X86_RegisterValueConsumer registerValueConsumer = new X86_RegisterValueConsumer();
	private X86_LetRefConsumer letRefConsumer = new X86_LetRefConsumer();
	private X86_PointerDerefConsumer pointerDerefConsumer = new X86_PointerDerefConsumer();
	private X86_PointerDerefSetConsumer pointerDerefSetConsumer = new X86_PointerDerefSetConsumer();
	private X86_StructDefConsumer structDefConsumer = new X86_StructDefConsumer();
	private X86_UserTypeAllocConsumer userTypeAllocConsumer = new X86_UserTypeAllocConsumer();
	private X86_ExplicitArrayDefConsumer explicitArrayDefConsumer = new X86_ExplicitArrayDefConsumer();
	private X86_ArrayAccessConsumer arrayAccessConsumer = new X86_ArrayAccessConsumer();

	public X86Compiler(RuntimeNode env, File binDirPath, String fileName) {
		super(env, new File(binDirPath, fileName));

		runtimeConsumer.attach(this);
		packageDefConsumer.attach(this);
		letDefConsumer.attach(this);
		fileConsumer.attach(this);
		funDefConsumer.attach(this);
		returnConsumer.attach(this);
		numLitConsumer.attach(this);
		binaryOpConsumer.attach(this);
		fieldAccessConsumer.attach(this);
		funCallConsumer.attach(this);
		unaryOpConsumer.attach(this);
		ifContainerConsumer.attach(this);
		scopeBodyConsumer.attach(this);
		forDefConsumer.attach(this);
		letSetConsumer.attach(this);
		registerValueConsumer.attach(this);
		whileDefConsumer.attach(this);
		letRefConsumer.attach(this);
		pointerDerefConsumer.attach(this);
		pointerDerefSetConsumer.attach(this);
		structDefConsumer.attach(this);
		userTypeAllocConsumer.attach(this);
		explicitArrayDefConsumer.attach(this);
		arrayAccessConsumer.attach(this);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writetextln("global _start");

		writeln("BITS 64");
		writeln("_start:");

		compile(root);

		appendBSS();
		appendText();
		appendData();

		flushAndClose();

		try {

			String oOutFile = outFileObj.getPath();
			String execOutFile = outFileExec.getPath();
			String outAsmPath = outFileAsm.getPath();

			exec("nasm -f elf64 -g -o " + oOutFile + " " + outAsmPath, outDir);
			// exec("gcc -m32 elf_i386 -o " + rmExtOutFile + " " + oOutFile, dir);
			exec("ld -o " + execOutFile + " " + oOutFile, outDir);
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
		} else if (node instanceof FunDefNode) {
			funDefConsumer.accept((FunDefNode) node);
		} else if (node instanceof ReturnNode) {
			returnConsumer.accept((ReturnNode) node);
		} else if (node instanceof NumLitNode) {
			numLitConsumer.accept((NumLitNode) node);
		} else if (node instanceof BinaryOpNode) {
			binaryOpConsumer.accept((BinaryOpNode) node);
		} else if (node instanceof FieldAccessNode) {
			fieldAccessConsumer.accept((FieldAccessNode) node);
		} else if (node instanceof FunCallNode) {
			funCallConsumer.accept((FunCallNode) node);
		} else if (node instanceof UnaryOpNode) {
			unaryOpConsumer.accept((UnaryOpNode) node);
		} else if (node instanceof IfContainerNode) {
			ifContainerConsumer.accept((IfContainerNode) node);
		} else if (node instanceof ScopeBodyNode) {
			scopeBodyConsumer.accept((ScopeBodyNode) node);
		} else if (node instanceof ForDefNode) {
			forDefConsumer.accept((ForDefNode) node);
		} else if (node instanceof WhileDefNode) {
			whileDefConsumer.accept((WhileDefNode) node);
		} else if (node instanceof LetSetNode) {
			letSetConsumer.accept((LetSetNode) node);
		} else if (node instanceof RegisterValueNode) {
			registerValueConsumer.accept((RegisterValueNode) node);
		} else if (node instanceof LetRefNode) {
			letRefConsumer.accept((LetRefNode) node);
		} else if (node instanceof PointerDerefNode) {
			pointerDerefConsumer.accept((PointerDerefNode) node);
		} else if (node instanceof PointerDerefSetNode) {
			pointerDerefSetConsumer.accept((PointerDerefSetNode) node);
		} else if (node instanceof StructDefNode) {
			structDefConsumer.accept((StructDefNode) node);
		} else if (node instanceof UserTypeAllocNode) {
			userTypeAllocConsumer.accept((UserTypeAllocNode) node);
		} else if (node instanceof ExplicitArrayDefNode) {
			explicitArrayDefConsumer.accept((ExplicitArrayDefNode) node);
		} else if (node instanceof ArrayAccessNode) {
			arrayAccessConsumer.accept((ArrayAccessNode) node);
		} else {
			implement(node);
		}
	}

	public void implement() throws CompilerException {
		throw new CompilerException("not implemented; ");
	}

	public void implement(Object obj) throws CompilerException {
		throw new CompilerException("not implemented: " + obj.getClass().getName() + " (" + obj + ")");
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

	public String getDataType(int bytes) {
		switch (bytes) {
		case 1:
			return "b";
		case 2:
			return "w";
		case 4:
			return "d";
		case 8:
			return "q";
		}
		return null;
	}

	public String getMovType(int bytes) {
		switch (bytes) {
		case 1:
			return "byte";
		case 2:
			return "word";
		case 4:
			return "dword";
		case 8:
			return "qword";
		}
		return null;

	}

}
