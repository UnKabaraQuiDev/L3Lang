package lu.pcy113.l3.compiler.llvm;

import java.io.File;
import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.llvm.LLVMAnalysis;
import org.lwjgl.llvm.LLVMCore;
import org.lwjgl.llvm.LLVMIRReader;
import org.lwjgl.llvm.LLVMTargetAArch64;
import org.lwjgl.llvm.LLVMTargetARM;
import org.lwjgl.llvm.LLVMTargetMachine;
import org.lwjgl.llvm.LLVMTargetRISCV;
import org.lwjgl.llvm.LLVMTargetWebAssembly;
import org.lwjgl.llvm.LLVMTargetX86;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import lu.pcy113.l3.L3Exception;

final class LwjglLlvmDriver {

	void compileIr(final String ir, final File irFile, final File objectFile) throws Exception {
		long context = 0L;
		long module = 0L;
		long targetMachine = 0L;
		ByteBuffer irBuffer = null;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			context = LLVMCore.LLVMContextCreate();
			irBuffer = MemoryUtil.memUTF8(ir, false);

			final long memoryBuffer = LLVMCore.LLVMCreateMemoryBufferWithMemoryRangeCopy(irBuffer, irFile.getName());
			final PointerBuffer moduleOut = stack.mallocPointer(1);
			final PointerBuffer parseErrorOut = stack.mallocPointer(1);

			if (LLVMIRReader.LLVMParseIRInContext(context, memoryBuffer, moduleOut, parseErrorOut)) {
				throw new L3Exception("LLVM could not parse generated IR: " + readAndDisposeMessage(parseErrorOut));
			}
			module = moduleOut.get(0);

			final PointerBuffer verifyErrorOut = stack.mallocPointer(1);

			if (LLVMAnalysis.LLVMVerifyModule(module, LLVMAnalysis.LLVMReturnStatusAction, verifyErrorOut)) {
				throw new L3Exception("LLVM module verification failed:\n" + readAndDisposeMessage(verifyErrorOut));
			}

			final String triple = LLVMTargetMachine.LLVMGetDefaultTargetTriple();
			if (triple == null || triple.isBlank()) {
				throw new L3Exception("LLVM did not return a default target triple.");
			}

			initializeTargetForTriple(triple);
			LLVMCore.LLVMSetTarget(module, triple);

			final PointerBuffer targetOut = stack.mallocPointer(1);
			final PointerBuffer targetErrorOut = stack.mallocPointer(1);
			if (LLVMTargetMachine.LLVMGetTargetFromTriple(triple, targetOut, targetErrorOut)) {
				throw new L3Exception("LLVM could not select target for triple " + triple + ": "
						+ readAndDisposeMessage(targetErrorOut));
			}

			final long target = targetOut.get(0);
			targetMachine = LLVMTargetMachine.LLVMCreateTargetMachine(target, triple, "generic", "",
					LLVMTargetMachine.LLVMCodeGenLevelDefault, LLVMTargetMachine.LLVMRelocDefault,
					LLVMTargetMachine.LLVMCodeModelDefault);

			if (targetMachine == 0L) {
				throw new L3Exception("LLVM could not create a target machine for triple " + triple + ".");
			}

			final PointerBuffer emitErrorOut = stack.mallocPointer(1);
			if (LLVMTargetMachine.LLVMTargetMachineEmitToFile(targetMachine, module, objectFile.getAbsolutePath(),
					LLVMTargetMachine.LLVMObjectFile, emitErrorOut)) {
				throw new L3Exception("LLVM could not emit object file: " + readAndDisposeMessage(emitErrorOut));
			}
		} finally {
			if (targetMachine != 0L) {
				LLVMTargetMachine.LLVMDisposeTargetMachine(targetMachine);
			}
			if (module != 0L) {
				LLVMCore.LLVMDisposeModule(module);
			}
			if (context != 0L) {
				LLVMCore.LLVMContextDispose(context);
			}
			if (irBuffer != null) {
				MemoryUtil.memFree(irBuffer);
			}
		}
	}

	private static void initializeTargetForTriple(final String triple) {
		final String normalized = triple.toLowerCase();

		if (normalized.startsWith("x86_64") || normalized.startsWith("i386") || normalized.startsWith("i486")
				|| normalized.startsWith("i586") || normalized.startsWith("i686")) {
			LLVMTargetX86.LLVMInitializeX86TargetInfo();
			LLVMTargetX86.LLVMInitializeX86Target();
			LLVMTargetX86.LLVMInitializeX86TargetMC();
			LLVMTargetX86.LLVMInitializeX86AsmPrinter();
			LLVMTargetX86.LLVMInitializeX86AsmParser();
			return;
		}

		if (normalized.startsWith("aarch64") || normalized.startsWith("arm64")) {
			LLVMTargetAArch64.LLVMInitializeAArch64TargetInfo();
			LLVMTargetAArch64.LLVMInitializeAArch64Target();
			LLVMTargetAArch64.LLVMInitializeAArch64TargetMC();
			LLVMTargetAArch64.LLVMInitializeAArch64AsmPrinter();
			LLVMTargetAArch64.LLVMInitializeAArch64AsmParser();
			return;
		}

		if (normalized.startsWith("arm")) {
			LLVMTargetARM.LLVMInitializeARMTargetInfo();
			LLVMTargetARM.LLVMInitializeARMTarget();
			LLVMTargetARM.LLVMInitializeARMTargetMC();
			LLVMTargetARM.LLVMInitializeARMAsmPrinter();
			LLVMTargetARM.LLVMInitializeARMAsmParser();
			return;
		}

		if (normalized.startsWith("riscv32") || normalized.startsWith("riscv64")) {
			LLVMTargetRISCV.LLVMInitializeRISCVTargetInfo();
			LLVMTargetRISCV.LLVMInitializeRISCVTarget();
			LLVMTargetRISCV.LLVMInitializeRISCVTargetMC();
			LLVMTargetRISCV.LLVMInitializeRISCVAsmPrinter();
			LLVMTargetRISCV.LLVMInitializeRISCVAsmParser();
			return;
		}

		if (normalized.startsWith("wasm32") || normalized.startsWith("wasm64")) {
			LLVMTargetWebAssembly.LLVMInitializeWebAssemblyTargetInfo();
			LLVMTargetWebAssembly.LLVMInitializeWebAssemblyTarget();
			LLVMTargetWebAssembly.LLVMInitializeWebAssemblyTargetMC();
			LLVMTargetWebAssembly.LLVMInitializeWebAssemblyAsmPrinter();
			LLVMTargetWebAssembly.LLVMInitializeWebAssemblyAsmParser();
			return;
		}

		throw new L3Exception("Unsupported LLVM target triple: " + triple
				+ ". Add the matching org.lwjgl.llvm.LLVMTarget... initialization calls in LwjglLlvmDriver.");
	}

	private static String readAndDisposeMessage(final PointerBuffer messageOut) {
		final long message = messageOut.get(0);
		if (message == 0L) {
			return "<no LLVM error message>";
		}

		try {
			final String text = MemoryUtil.memUTF8(message);
			return text == null ? "<empty LLVM error message>" : text;
		} finally {
			LLVMCore.nLLVMDisposeMessage(message);
		}
	}
}
