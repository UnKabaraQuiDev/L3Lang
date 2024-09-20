package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.ExplicitArrayDefNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.type.ArrayTypeNode;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_ExplicitArrayDefConsumer extends CompilerConsumer<X86Compiler, ExplicitArrayDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ExplicitArrayDefNode node) throws CompilerException {
		GlobalLogger.log("ExplicitArrayDef: " + node);

		final LetDefNode letDefParent = node.getParent(LetDefNode.class);
		ArrayTypeNode arrayType = (ArrayTypeNode) letDefParent.getType();

		final int arrayDepth = node.getParentCount(ExplicitArrayDefNode.class); // getting the depth of the current ExplicitArrayDefNode
		arrayType = arrayType.getSubType(arrayDepth); // getting subelement for current depth

		final int expectedElementCount = arrayType.getElementCount();
		final int elementSize = arrayType.getBytesSize() / expectedElementCount;
		final int givenElementCount = node.getExprs().size();

		if (givenElementCount != expectedElementCount) {
			throw new CompilerException("Expected " + expectedElementCount + " elements but got " + givenElementCount + " in " + node.toString(0));
		}

		compiler.writeinstln("push rsp  ; Storing array pointer (depth=" + arrayDepth + ")");

		if (arrayType.getSubType() instanceof ArrayTypeNode) {

			for (ExprNode subExpr : node.getExprs()) {
				compiler.compile((ExplicitArrayDefNode) subExpr);
			}

		} else if (arrayType.getSubType() instanceof RecursiveArithmeticOp) {

			for (ExprNode subExpr : node.getExprs()) {
				compiler.compile(subExpr);

				final String latest = mem.getLatest();

				compiler.writeinstln("push " + compiler.getMovType(elementSize) + " " + mem.getAsSize(latest, elementSize) + "  ; Save local array element, size=" + elementSize);

				mem.free(latest);
			}

		} else if (arrayType.getSubType() instanceof UserTypeNode) {

			for (ExprNode subExpr : node.getExprs()) {
				compiler.compile((UserTypeAllocNode) subExpr);
			}

		}

		for (ExprNode subExpr : node.getExprs()) {
			if (subExpr instanceof RecursiveArithmeticOp) {
				compiler.compile(subExpr);

				final String latest = mem.getLatest();

				compiler.writeinstln("push " + compiler.getMovType(elementSize) + " " + mem.getAsSize(latest, elementSize) + "  ; Save local array element, size=" + elementSize);

				mem.free(latest);
			} else if (subExpr instanceof UserTypeAllocNode || subExpr instanceof ExplicitArrayDefNode) {
				compiler.compile(subExpr);
			} else {
				compiler.implement(subExpr);
			}
		}
	}

}
