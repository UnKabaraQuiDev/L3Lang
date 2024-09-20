package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ArrayAccessNode;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.type.ArrayTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_ArrayAccessConsumer extends CompilerConsumer<X86Compiler, ArrayAccessNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ArrayAccessNode node) throws CompilerException {
		GlobalLogger.log("ArrayAccess: " + node);

		final FieldAccessNode fieldAccess = (FieldAccessNode) node.getFirstChild(FieldAccessNode.class);

		final LetScopeDescriptor letDesc = container.getLetDefDescriptor(fieldAccess);
		final LetDefNode letDef = letDesc.getNode();

		final ArrayTypeNode arrayType = (ArrayTypeNode) letDef.getType();

		final int arrayDepth = node.getParentCount(ArrayAccessNode.class) + 1; // getting the depth of the current ExplicitArrayDefNode
		final TypeNode finalType = arrayType.getSubType(arrayDepth); // getting subelement for current depth
		System.err.println("act arr type: " + arrayDepth + " a " + finalType);

		compiler.compile(node.getExpr());
		final String pointerReg = mem.getLatest();

		compiler.compile(node.getOffset());
		final String offsetReg = mem.getLatest();

		compiler.writeinstln("mov " + pointerReg + ", [" + pointerReg + "+" + offsetReg + "*" + (finalType instanceof ArrayTypeNode ? ((ArrayTypeNode) finalType).getRealByteSize() : finalType.getBytesSize()) + "]");

		mem.free(offsetReg);
		mem.setLatest(pointerReg);
	}

	private String getInstr(FieldAccessNode node, int size) throws CompilerException {
		if (node.isInteger()) {
			return "mov" + (size == 8 ? "" : "zx");
		} else if (node.isDouble()) {
			return "movss";
		} else if (node.isFloat()) {
			return "movsd";
		}
		throw new CompilerException("No type ? " + node + " for size: " + size);
	}

}
