package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ValueType;
import lu.pcy113.l3.parser.ast.lit.DecimalNumLitNode;
import lu.pcy113.l3.parser.ast.lit.IntegerNumLitNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_NumLitConsumer extends CompilerConsumer<X86Compiler, NumLitNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, NumLitNode node) throws CompilerException {
		GlobalLogger.log("NumLit: " + node);

		String reg = mem.alloc();

		if (node instanceof DecimalNumLitNode && ((DecimalNumLitNode) node).isFloat()) {
			compiler.writeinstln("mov " + mem.getAsSize(reg, MemoryUtil.getPrimitiveSize(ValueType.FLOAT)) + ", " + Float.floatToRawIntBits((float) node.getValue()));
		} else if (node instanceof DecimalNumLitNode && ((DecimalNumLitNode) node).isDouble()) {
			compiler.writeinstln("mov " + mem.getAsSize(reg, MemoryUtil.getPrimitiveSize(ValueType.DOUBLE)) + ", " + Double.doubleToRawLongBits((double) node.getValue()));
		} else if (node instanceof IntegerNumLitNode) {
			compiler.writeinstln("mov " + reg + ", " + node.getValue());
		}
	}

}
