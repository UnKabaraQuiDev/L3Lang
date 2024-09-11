package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_LetDefConsumer extends CompilerConsumer<X86Compiler, LetDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, LetDefNode node) throws CompilerException {
		GlobalLogger.log("LetDef: " + node);

		LetScopeDescriptor def = container.getLetDefDescriptor(node);

		node.getType().normalizeSize(container);
		int size = node.getType().getBytesSize();

		if (node.isiStatic()) {
			if (node.getExpr() instanceof NumLitNode) {
				compiler.writedataln(def.getAsmName() + " d" + compiler.getDataType(size) + " " + ((NumLitNode) node.getExpr()).getValue() + "  ; Defined: " + size + " for " + node.getIdent().asString());

				def.setAllocated(true);
			} else if (node.getExpr() instanceof RecursiveArithmeticOp) {
				compiler.writebssln(def.getAsmName() + " resb " + size + "  ; Reserved: " + size + " for " + node.getIdent().asString());

				compiler.compile(node.getExpr());

				String reg = mem.getLatest();
				compiler.writeinstln("mov [" + def.getAsmName() + "], " + reg);

				def.setAllocated(true);
			} else {
				compiler.writedataln(def.getAsmName() + " times " + size + " db " + " 0  ; Reserved empty: " + size + " for " + node.getIdent().asString());

				def.setAllocated(true);
			}
		} else {
			if (node.getExpr() instanceof RecursiveArithmeticOp) {
				compiler.compile(node.getExpr());

				mem.pushStack(node);

				String reg = mem.getLatest();

				compiler.writeinstln("push " + compiler.getMovType(size) + " " + mem.getAsSize("rax", size) + "  ; Alloc-ed: " + size + " for " + node.getIdent().asString());

				mem.free(reg);

				def.setAllocated(true);
			} else if (node.getExpr() instanceof UserTypeAllocNode) {
				// TODO add support for other than struct

				mem.pushStack(node);

				compiler.writeinstln("sub rsp, " + size + "  ; Alloc-ed empty: " + size + " for " + node.getIdent().asString());

				UserTypeAllocNode ua = (UserTypeAllocNode) node.getExpr();

				StructScopeDescriptor structDesc = container.getStructDefDescriptor(((UserTypeNode) ua.getType()).getIdentifier().getLeaf().getValue());
				StructDefNode structDef = structDesc.getNode();

				for (LetSetNode n : ua.getLets()) {
					LetScopeDescriptor letDesc = structDef.getLetDefDescriptor(n.getLet().getIdent().getLeaf().getValue());
					LetDefNode letDef = letDesc.getNode();
					letDesc.setAllocated(true);

					compiler.compile(n.getExpr());

					String reg = mem.getLatest();

					compiler.writeinstln("mov [rbp-" + (letDesc.getStackOffset()) + "], " + mem.getAsSize(reg, letDef.getType().getBytesSize()) + "  ; Save local struct var, size=" + size + ", offset=" + def.getStackOffset() + ".");

					mem.free(reg);
				}

				def.setAllocated(true);
			} else {
				mem.pushStack(node);

				compiler.writeinstln("sub rsp, " + size + "  ; Alloc-ed empty: " + size + " for " + node.getIdent().asString());

				def.setAllocated(true);
			}
		}

	}

}
