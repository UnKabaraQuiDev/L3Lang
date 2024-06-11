package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.lit.DecimalNumLitNode;
import lu.pcy113.l3.parser.ast.lit.IntegerNumLitNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_NumLitConsumer extends CompilerConsumer<X86Compiler, NumLitNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, NumLitNode node) throws CompilerException {
		GlobalLogger.log("NumLit: " + node);

		String reg = mem.alloc();

		if (node instanceof DecimalNumLitNode) {
			compiler.implement(node);
		} else if (node instanceof IntegerNumLitNode) {
			compiler.writeinstln("mov " + reg + ", " + node.getValue());
		}
	}

}
