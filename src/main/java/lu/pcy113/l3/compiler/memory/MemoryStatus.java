package lu.pcy113.l3.compiler.memory;

import java.io.PrintStream;

public interface MemoryStatus {

	String alloc();

	String allocFP();

	void free(String reg);

	boolean hasFree();

	boolean isFree(String reg);

	boolean alloc(String reg);

	void freeAll();

	String getAsSize(String reg, int bytes);

	String getLatest();

	void setLatest(String reg);

	void dump(PrintStream out);

	boolean hasFreeFP();

}