package lu.pcy113.l3.compiler;

import java.io.PrintStream;
import java.util.Stack;

import lu.pcy113.l3.parser.expressions.Expr;
import lu.pcy113.l3.parser.expressions.containers.EnvContainer;
import lu.pcy113.l3.parser.expressions.containers.ExprContainer;

public class X86Compiler extends L3Compiler {
	
	private int heapSize = 2048;
	
	public X86Compiler(EnvContainer env, PrintStream err) {
		super(env, err);
	}

	@Override
	public void compile() throws CompilerException {
		Stack<Integer> heap = new Stack<>();
		
		Expr expr;
		
		while((expr = next()) instanceof ExprContainer) {
			// TODO
		}
	}
	
}
