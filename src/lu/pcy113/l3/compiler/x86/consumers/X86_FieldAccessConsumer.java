package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_FieldAccessConsumer extends CompilerConsumer<X86Compiler, FieldAccessNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FieldAccessNode node) throws CompilerException {
		GlobalLogger.log("FieldAccess: " + node);
		
		if (node.getIdent().size() > 1) {
			compiler.implement();
		} else {
			String ident = node.getIdent().getLeaf().getValue();
			if (!container.containsDescriptor(ident)) {
				throw new CompilerException("LetDef: '" + ident + "' not found in current scope.");
			}

			LetScopeDescriptor def = container.getLetDefDescriptor(ident);
			LetDefNode letDef = def.getNode();

			int size = letDef.getType().getBytesSize();

			if (!def.isAllocated()) {
				throw new CompilerException("LetDef: " + ident + ", isn't accessible in current scope.");
			}

			String reg = mem.alloc();

			if (letDef.isiStatic()) {
				compiler.writeinstln("mov " + mem.getAsSize(reg, size) + ", [" + def.getAsmName() + "]  ; Load: " + letDef.getIdent());
			} else {
				compiler.writeinstln("mov " + mem.getAsSize(reg, size) + ", [rbp-" + (size + def.getStackOffset()) + "]  ; Load: " + letDef.getIdent());
			}

		}
	}

}
