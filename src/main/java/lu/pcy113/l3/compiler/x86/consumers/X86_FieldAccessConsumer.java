package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ParamScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_FieldAccessConsumer extends CompilerConsumer<X86Compiler, FieldAccessNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FieldAccessNode node) throws CompilerException {
		GlobalLogger.log("FieldAccess: " + node);

		if (node.getIdent().size() > 1) {
			compiler.implement();
		} else {
			String ident = node.getIdent().getLeaf().getValue();

			FunDefNode funDef = null;

			if (node.hasFunDefParent() && (funDef = node.getFunDefParent()).isParamDefDescriptor(ident)) { // fun param

				ParamScopeDescriptor letDesc = funDef.getParamDefDescriptor(ident);
				FunDefParamNode letDef = letDesc.getNode();

				letDef.getType().normalizeSize(container);
				int size = letDef.getType().getBytesSize();
				funDef.getFunDefParent().getParams().normalizeSize();
				int paramsSize = funDef.getFunDefParent().getParams().getBytesSize();

				String reg = mem.alloc();

				compiler.writeinstln(getInstr(node, size) + " " + mem.getAsSize(reg, size) + ", [rbp+" + (8 + (paramsSize - letDesc.getStackOffset())) + "]  ; Load param: " + letDef.getIdent() + " > " + letDesc.getStackOffset() + "/" + paramsSize);

			} else { // in global-scope / static, or not a parameter but local variable

				if (!container.containsDescriptor(ident)) {
					throw new CompilerException("LetDef: '" + ident + "' not found in current scope.");
				}

				LetScopeDescriptor letDesc = container.getLetDefDescriptor(ident);
				LetDefNode letDef = letDesc.getNode();

				int size = letDef.getType().getBytesSize();

				if (!letDef.isAllocated()) {
					throw new CompilerException("LetDef: " + ident + ", isn't accessible in current scope.");
				}

				String reg = mem.alloc();

				if (letDef.isiStatic()) {
					compiler.writeinstln(getInstr(node, size) + " " + mem.getAsSize(reg, size) + ", [" + letDesc.getAsmName() + "]  ; Load static var: " + letDef.getIdent());
				} else {
					compiler.writeinstln(getInstr(node, size) + " " + mem.getAsSize(reg, size) + ", [rbp-" + (size + letDesc.getStackOffset()) + "]  ; Load local var: " + letDef.getIdent());
				}

			}

		}
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
