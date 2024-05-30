package lu.pcy113.l3.compiler.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public abstract class CompilerConsumer<C extends L3Compiler, T extends Node> {

	protected C compiler;

	public void attach(C compiler) {
		this.compiler = compiler;
	}

	public final void accept(T node) throws CompilerException {
		if(compiler == null) {
			throw new RuntimeException(new CompilerException("This consumer ("+this.getClass().getName()+") wasn't attached to any compiler."));
		}
		
		accept(compiler, compiler.getMemoryStatus(), node.getClosestContainer(), node);
	}

	protected abstract void accept(C compiler, MemoryStatus mem, ScopeContainer container, T node) throws CompilerException;

}
