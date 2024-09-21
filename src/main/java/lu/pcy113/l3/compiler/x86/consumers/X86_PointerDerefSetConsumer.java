package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.PointerDerefSetNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.pclib.datastructure.pair.ReadOnlyPair;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_PointerDerefSetConsumer extends CompilerConsumer<X86Compiler, PointerDerefSetNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, PointerDerefSetNode node) throws CompilerException {
		GlobalLogger.log("PointerDerefSet: " + node);

		PointerDerefNode pointer = node.getPointer();

		ExprNode pointerExpr1 = pointer.getPointerExpr();

		if (!(pointerExpr1 instanceof FieldAccessNode)) {
			compiler.implement(pointer); // We need to know the type of the var for the sub expr (node.getExpr())
		}

		final FieldAccessNode fieldPointer = (FieldAccessNode) pointerExpr1;
		final LetScopeDescriptor letDesc = container.getLetDefDescriptor(fieldPointer);
		final LetDefNode letDef = letDesc.getNode();
		final TypeNode type = ((PointerTypeNode) letDef.getType()).getTypeNode();

		compiler.compile(pointerExpr1);
		String pointerReg = mem.getLatest();

		if (pointer.hasExpr()) {
			if (pointer.getExpr() instanceof FieldAccessNode) {
				ReadOnlyPair<Integer, Integer> offset_size = X86_PointerDerefConsumer.calcOffset(container, letDesc, letDef, (UserTypeNode) type, (FieldAccessNode) pointer.getExpr());

				compiler.writeinstln("sub " + pointerReg + ", " + (offset_size.getValue() + offset_size.getKey()) + "  ; Adding offset to pointer addr");
			} else {
				compiler.implement(pointer.getExpr());
			}
		}

		if (node.getExpr() instanceof UserTypeAllocNode) {
			compiler.compile(node.getExpr());
		}else {
			compiler.compile(node.getExpr());
			String valReg = mem.getLatest();
			
			compiler.writeinstln("mov [" + pointerReg + "], " + valReg + "  ; Storing value in pointer");
			
			mem.free(valReg);
		}
	}

}
