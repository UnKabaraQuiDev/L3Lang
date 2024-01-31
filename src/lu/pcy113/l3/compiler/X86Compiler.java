package lu.pcy113.l3.compiler;

import java.io.PrintStream;

import lu.pcy113.l3.parser.ast.Expr;
import lu.pcy113.l3.parser.ast.VariableDeclarationExpr;
import lu.pcy113.l3.parser.ast.containers.EnvContainer;
import lu.pcy113.l3.parser.ast.containers.ExprContainer;
import lu.pcy113.l3.utils.MemorySize;

public class X86Compiler extends L3Compiler {
	
	private int heapSize = 2048;
	
	public X86Compiler(EnvContainer env, PrintStream err) {
		super(env, err);
	}

	@Override
	public void compile() throws CompilerException {
		calcHeapSize(input);
		System.out.println(input.getSize());
	}

	private int calcHeapSize(ExprContainer container) {
		int size = 0;
		for(Expr child : container.getChildren()) {
			if(child instanceof ExprContainer)
				size += calcHeapSize((ExprContainer) child);
			else if(child instanceof VariableDeclarationExpr)
				size += realiseSize(((VariableDeclarationExpr) child).getMemorySize());
		}
		container.setSize(size);
		return size;
	}

	private int realiseSize(MemorySize memorySize) {
		// TODO 
		return memorySize.getBytes();
	}
	
}
