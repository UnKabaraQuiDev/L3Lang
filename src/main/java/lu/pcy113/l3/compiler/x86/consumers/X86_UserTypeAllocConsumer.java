package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;

public class X86_UserTypeAllocConsumer extends CompilerConsumer<X86Compiler, UserTypeAllocNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, UserTypeAllocNode node) throws CompilerException {
		final StructScopeDescriptor structDesc = container.getStructDefDescriptor(((UserTypeNode) node.getType()).getIdentifier());
		final StructDefNode structDef = structDesc.getNode();

		final String locRegister = mem.getLatest();

		int offset = 0;

		for (LetSetNode n : node.getLets()) {
			final LetScopeDescriptor letDesc = structDef.getLetDefDescriptor(n.getLet().getIdent().getLeaf().getValue());
			final LetDefNode letDef = letDesc.getNode();

			final ExprNode subExpr = n.getExpr();

			int size = letDef.getType().getBytesSize();

			if (subExpr instanceof RecursiveArithmeticOp) {
				compiler.compile(subExpr);

				String reg = mem.getLatest();

				compiler.writeinstln("mov [" + locRegister + "-" + (size + offset) + "], " + mem.getAsSize(reg, size) + "  ; Save local struct var, size=" + size + ", stackOffset=" + letDesc.getStackOffset() + ", offset=" + offset + ".");

				mem.free(reg);
			} else if (subExpr instanceof UserTypeAllocNode) {
				mem.setLatest(locRegister + "-" + offset);

				compiler.compile(subExpr);
			} else {
				compiler.implement(subExpr);
			}

			offset += size;

			letDef.setAllocated(true);
		}
	}

}
