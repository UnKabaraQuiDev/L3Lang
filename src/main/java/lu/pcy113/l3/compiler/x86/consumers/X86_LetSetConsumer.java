package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ParamScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_LetSetConsumer extends CompilerConsumer<X86Compiler, LetSetNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, LetSetNode node) throws CompilerException {
		GlobalLogger.log("LetSet: " + node);

		FieldAccessNode field = node.getLet();

		if (field.getIdent().size() > 1) { // struct type

			final LetScopeDescriptor letDesc = container.getLetDefDescriptor(field.getIdent().getFirst().getValue());
			final LetDefNode letDef = letDesc.getNode();
			final UserTypeNode type = (UserTypeNode) letDef.getType();

			if (!letDef.isAllocated()) {
				throw new CompilerException("LetDef: " + letDef.getIdent() + ", isn't accessible in current scope.");
			}

			int offset = letDesc.getStackOffset(), size = 0;

			System.err.println("base offset: " + offset);

			StructScopeDescriptor structDesc = container.getStructDefDescriptor(type.getIdentifier().getFirst().getValue());
			StructDefNode structDef = structDesc.getNode();

			for (int i = 1; i < field.getIdent().getTokens().size(); i++) {

				IdentifierToken ident = field.getIdent().getTokens().get(i);
				LetScopeDescriptor subLetDesc = structDef.getLetDefDescriptor(ident.getValue());
				LetDefNode subLetDef = subLetDesc.getNode();

				if (subLetDesc.getNode().getType() instanceof UserTypeNode) {
					UserTypeNode subType = (UserTypeNode) subLetDef.getType();
					structDesc = container.getStructDefDescriptor(subType.getIdentifier().getFirst().getValue());
					structDef = structDesc.getNode();
				}

				offset += subLetDesc.getStackOffset();
				size = subLetDef.getType().getBytesSize();

			}

			String ident = field.getIdent().getLeaf().getValue();
			FunDefNode funDef;

			if (node.hasFunDefParent() && (funDef = node.getFunDefParent()).isParamDefDescriptor(ident)) { // fun param

				compiler.implement();

			} else { // in global-scope / static, or not a parameter but local variable

				if (letDef.isiStatic()) {
					mem.setLatest(letDesc.getAsmName() + "-" + offset);
				} else {
					mem.setLatest("rbp-" + offset);
				}

				compiler.compile(node.getExpr());

			}

		} else { // primitive type

			final String ident = field.getIdent().getLeaf().getValue();

			FunDefNode funDef;

			if (node.hasFunDefParent() && (funDef = node.getFunDefParent()).isParamDefDescriptor(ident)) { // fun param

				ParamScopeDescriptor letDesc = funDef.getParamDefDescriptor(ident);
				FunDefParamNode funLetDef = letDesc.getNode();

				funLetDef.getType().normalizeSize(container);
				int size = funLetDef.getType().getBytesSize();
				funDef.getFunDefParent().getParams().normalizeSize();
				int paramsSize = funDef.getFunDefParent().getParams().getBytesSize();

				mem.setLatest("rbp+" + (8 + (paramsSize - letDesc.getStackOffset()))); // TODO check if param vars are still working

				compiler.compile(node.getExpr());

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

				if (letDef.isiStatic()) {
					mem.setLatest(letDesc.getAsmName()); // TODO check if static vars are still working
				} else {
					mem.setLatest("rbp-" + letDesc.getStackOffset());
				}

				compiler.compile(node.getExpr());

			}

		}
	}

}
