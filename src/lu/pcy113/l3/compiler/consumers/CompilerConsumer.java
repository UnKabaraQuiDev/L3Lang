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
		accept(compiler, compiler.getMemoryStatus(), node.getClosestContainer(), node);
	}

	protected abstract void accept(C compiler, MemoryStatus mem, ScopeContainer container, T node) throws CompilerException;

}
