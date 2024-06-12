package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ParamScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_PointerDerefConsumer extends CompilerConsumer<X86Compiler, PointerDerefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, PointerDerefNode node) throws CompilerException {
		GlobalLogger.log("FieldAccess: " + node);

		String ident = node.getExpr().getIdent().getLeaf().getValue();

		compiler.compile(node.getExpr());
		String reg = mem.getLatest();

		FunDefNode funDef = null;

		if (node.hasFunDefParent() && (funDef = node.getFunDefParent()).isParamDefDescriptor(ident)) { // fun param

			ParamScopeDescriptor def = funDef.getParamDefDescriptor(ident);
			FunDefParamNode letDef = def.getNode();

			letDef.getType().normalizeSize();
			int size = letDef.getType().getBytesSize();
			funDef.getFunDefParent().getParams().normalizeSize();
			int paramsSize = funDef.getFunDefParent().getParams().getBytesSize();

			compiler.writeinstln("mov" + (size == 8 ? "" : "zx") + " " + mem.getAsSize(reg, size) + ", [" + reg + "]  ; Load param from addr: " + letDef.getIdent() + " > " + def.getStackOffset() + "/" + paramsSize);

		} else { // in global-scope / static, or not a parameter but local variable

			if (!container.containsDescriptor(ident)) {
				throw new CompilerException("LetDef: '" + ident + "' not found in current scope.");
			}

			LetScopeDescriptor def = container.getLetDefDescriptor(ident);
			LetDefNode letDef = def.getNode();

			int size = letDef.getType().getBytesSize();

			if (!def.isAllocated()) {
				throw new CompilerException("LetDef: " + ident + ", isn't accessible in current scope.");
			}

			if (letDef.isiStatic()) {
				compiler.writeinstln("mov" + (size == 8 ? "" : "zx") + " " + mem.getAsSize(reg, size) + ", [" + reg + "]  ; Load static var from addr: " + letDef.getIdent());
			} else {
				compiler.writeinstln("mov" + (size == 8 ? "" : "zx") + " " + mem.getAsSize(reg, size) + ", [" + reg + "]  ; Load local var from addr: " + letDef.getIdent());
			}

		}
	}

}
