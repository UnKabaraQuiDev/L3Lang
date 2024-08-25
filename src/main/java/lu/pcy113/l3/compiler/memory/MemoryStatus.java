package lu.pcy113.l3.compiler.memory;

import java.io.PrintStream;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;

public interface MemoryStatus {

	String alloc();
	
	String allocFP();

	void free(String reg);

	boolean hasFree();

	boolean isFree(String reg);

	boolean alloc(String reg) throws CompilerException;

	void freeAll();

	String getAsSize(String reg, int bytes);

	String getLatest();

	void setLatest(String reg);

	void pushStack(Node node) throws CompilerException;

	Node popStack() throws CompilerException;

	void clearStack();

	void dump(PrintStream out);

	boolean hasFreeFP();

}
