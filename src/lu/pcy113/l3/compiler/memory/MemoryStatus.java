package lu.pcy113.l3.compiler.memory;

public interface MemoryStatus {

	String getFreeRegister();

	void freeRegister(String reg);

	boolean hasFreeRegister();
	
	boolean isRegisterFree(String reg);
	
}
