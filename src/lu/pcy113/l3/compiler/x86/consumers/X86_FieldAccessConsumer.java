package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class X86_FieldAccessConsumer extends CompilerConsumer<X86Compiler, FieldAccessNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FieldAccessNode node) throws CompilerException {
		if (node.getIdent().size() > 1) {
			compiler.implement();
		} else {
			String ident = node.getIdent().getLeaf().getValue();
			if (container.containsDescriptor(ident)) {

				LetScopeDescriptor def = container.getLetDefDescriptor(ident);
				LetDefNode letDef = def.getNode();

				int size = letDef.getType().getBytesSize();

				if (def.isAllocated()) {

					String reg = mem.alloc();

					compiler.writeinstln("mov " + mem.getAsSize(reg, size) + ", [rbp-" + (size + def.getStackOffset()) + "]");

				} else {
					throw new CompilerException("LetDef: " + ident + ", isn't accessible in current scope.");
				}

			} else {
				throw new CompilerException("LetDef: " + ident + ", not found in current scope.");
			}
		}
	}

}
