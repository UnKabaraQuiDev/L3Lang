package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class X86_FileConsumer extends CompilerConsumer<X86Compiler, FileNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FileNode node) throws CompilerException {
		for (Node n : node) {
			if (n instanceof LetDefNode) {
				continue;
			}
			if (n instanceof FunDefNode && (((FunDefNode) n).isMain() && !node.isMain())) {
				continue;
			}
			
			compiler.compile(n);
		}
	}

}
