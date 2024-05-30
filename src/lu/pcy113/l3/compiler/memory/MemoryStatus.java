package lu.pcy113.l3.compiler.memory;

public interface MemoryStatus {

	String alloc();

	void free(String reg);

	boolean hasFree();
	
	boolean isFree(String reg);
	
	boolean alloc(String reg);
	
	void freeAll();
	
}
