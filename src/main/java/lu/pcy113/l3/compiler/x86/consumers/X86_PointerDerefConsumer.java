package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.scope.StructScopeDescriptor;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.l3.parser.ast.type.UserTypeNode;
import lu.pcy113.pclib.datastructure.pair.Pairs;
import lu.pcy113.pclib.datastructure.pair.ReadOnlyPair;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_PointerDerefConsumer extends CompilerConsumer<X86Compiler, PointerDerefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, PointerDerefNode node) throws CompilerException {
		GlobalLogger.log("PointerDeref: " + node);

		ExprNode pointer = node.getPointerExpr();

		if (!(pointer instanceof FieldAccessNode)) {
			compiler.implement(pointer); // We need to know the type of the var for the sub expr (node.getExpr())
		}

		final FieldAccessNode fieldPointer = (FieldAccessNode) pointer;
		final LetScopeDescriptor letDesc = container.getLetDefDescriptor(fieldPointer);
		final LetDefNode letDef = letDesc.getNode();
		final TypeNode type = ((PointerTypeNode) letDef.getType()).getTypeNode();

		compiler.compile(pointer);
		String pointerReg = mem.getLatest();

		if (node.hasExpr()) {
			if (node.getExpr() instanceof FieldAccessNode) {
				ReadOnlyPair<Integer, Integer> offset_size = calcOffset(container, letDesc, letDef, (UserTypeNode) type, (FieldAccessNode) node.getExpr());

				// compiler.writeinstln("add " + pointerReg + ", " + offset_size.getKey() + " ; Adding offset to pointer");

				compiler.writeinstln("mov " + pointerReg + ", [" + pointerReg + "-" + (offset_size.getValue() + offset_size.getKey()) + "]  ; Loading value from pointer with offset");
			} else {
				compiler.implement(node.getExpr());
			}
		}
	}

	private ReadOnlyPair<Integer, Integer> calcOffset(ScopeContainer container, final LetScopeDescriptor letDesc, final LetDefNode letDef, final UserTypeNode type, final FieldAccessNode node) throws CompilerException {
		int offset = 0, size = 0;

		StructScopeDescriptor structDesc = container.getStructDefDescriptor(type.getIdentifier().getFirst().getValue());
		StructDefNode structDef = structDesc.getNode();

		System.err.println("ident: " + node);

		for (int i = 0; i < node.getIdent().getTokens().size(); i++) {

			IdentifierToken ident = node.getIdent().getTokens().get(i);
			LetScopeDescriptor subLetDesc = structDef.getLetDefDescriptor(ident.getValue());
			LetDefNode subLetDef = subLetDesc.getNode();

			if (subLetDesc.getNode().getType() instanceof UserTypeNode) {
				UserTypeNode subType = (UserTypeNode) subLetDef.getType();
				structDesc = container.getStructDefDescriptor(subType.getIdentifier().getFirst().getValue());
				structDef = structDesc.getNode();
			}

			offset += subLetDesc.getStackOffset();
			size = subLetDef.getType().getBytesSize();

			System.err.println("adds: " + subLetDesc.getStackOffset() + "=" + offset + ", size = " + size);

		}

		return Pairs.readOnly(offset, size);
	}

}
