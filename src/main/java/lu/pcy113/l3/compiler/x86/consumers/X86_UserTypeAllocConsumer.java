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
		StructScopeDescriptor structDesc = container.getStructDefDescriptor(((UserTypeNode) node.getType()).getIdentifier().getLeaf().getValue());
		StructDefNode structDef = structDesc.getNode();

		for (LetSetNode n : node.getLets()) {
			LetScopeDescriptor letDesc = structDef.getLetDefDescriptor(n.getLet().getIdent().getLeaf().getValue());
			LetDefNode letDef = letDesc.getNode();
			letDesc.setAllocated(true);

			ExprNode subExpr = n.getExpr();
			
			if (subExpr instanceof RecursiveArithmeticOp) {
				compiler.compile(n.getExpr());

				String reg = mem.getLatest();
				
				int size = letDef.getType().getBytesSize();
				
				compiler.writeinstln("push " + mem.getAsSize(reg, size) + "; Save local struct var, size=" + size + ", offset=" + letDesc.getStackOffset() + ".");
				// compiler.writeinstln("mov [rbp-" + (letDesc.getStackOffset()) + "], " + mem.getAsSize(reg, letDef.getType().getBytesSize()) + " ; Save local struct var, size=" + size + ", offset=" + def.getStackOffset() + ".");
				
				mem.free(reg);
			} else if (subExpr instanceof UserTypeAllocNode) {
				compiler.compile(subExpr);
			}else {
				compiler.implement(subExpr);
			}
			
		}
	}

}
