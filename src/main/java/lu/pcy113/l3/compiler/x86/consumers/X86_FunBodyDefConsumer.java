package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

/**
 * @deprecated replaced by {@link X86_ScopeBodyConsumer} 
 */
@Deprecated
public class X86_FunBodyDefConsumer extends CompilerConsumer<X86Compiler, FunBodyDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FunBodyDefNode node) throws CompilerException {
		GlobalLogger.log("FunBodyDef: " + node);

		compiler.writeinstln(";  Fun body start - - -");
		
		for (Node n : node) {
			compiler.compile(n);
			
			/*if (n instanceof LetDefNode) {
				compiler.compile((LetDefNode) n);
			} else if (n instanceof ReturnNode) {
				compiler.compile((ReturnNode) n);
			} else {
				compiler.implement(n);
			}*/
		}
		
		compiler.writeinstln(";  Fun body end - - -");
	}

}
