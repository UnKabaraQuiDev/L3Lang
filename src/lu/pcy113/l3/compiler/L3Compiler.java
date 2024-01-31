package lu.pcy113.l3.compiler;

import java.io.PrintStream;

import lu.pcy113.l3.parser.ast.containers.EnvContainer;

public abstract class L3Compiler extends ExprIterator {
	
	protected PrintStream writer;
	protected EnvContainer input;
	
	public L3Compiler(EnvContainer env, PrintStream writer) {
		super(env);
		this.input = env;
		this.writer = writer;
	}
	
	public abstract void compile() throws CompilerException;
	
	public EnvContainer getInput() {return input;}
	public PrintStream getWriter() {return writer;}
	
}
