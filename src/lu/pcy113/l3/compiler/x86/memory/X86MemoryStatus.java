package lu.pcy113.l3.compiler.x86.memory;

import lu.pcy113.l3.compiler.memory.MemoryStatus;

public class X86MemoryStatus implements MemoryStatus {

	@Override
	public String getFreeRegister() {
		return null;
	}

	@Override
	public void freeRegister(String reg) {

	}

	@Override
	public boolean hasFreeRegister() {
		return false;
	}

	@Override
	public boolean isRegisterFree(String reg) {
		return false;
	}

}
