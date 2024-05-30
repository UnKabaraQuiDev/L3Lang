package lu.pcy113.l3.parser.ast.scope;

import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.FunDefParamsNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunDefNode extends ScopeContainerNode {

	private IdentifierLitNode ident;

	public FunDefNode(TypeNode type, IdentifierLitNode ident, FunDefParamsNode funParamsDef) {
		this.ident = ident;
		add(type);
		add(funParamsDef);
	}

	public TypeNode getReturnType() {
		return (TypeNode) children.get(0);
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public FunDefParamsNode getParams() {
		return (FunDefParamsNode) children.get(1);
	}

	public boolean hasBody() {
		return children.size() > 2;
	}

	public FunBodyDefNode getBody() {
		return (FunBodyDefNode) children.get(2);
	}

	public boolean matchReturnType(Class<? extends TypeNode> clazz) {
		return clazz.isInstance(getReturnType().getClass());
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getReturnType() + ": " + ident.asString() + ")";
	}

	public boolean isMain() {
		return ident.asString().equals("main") && this.getParams().isLeaf() && getReturnType() instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) getReturnType()).getType().equals(TokenType.INT_8);
	}

	public int getMemorySize() throws CompilerException {
		int size = 0;
		for (ScopeDescriptor desc : getBody().getLocalDescriptors().values().parallelStream().flatMap(c -> c.stream()).collect(Collectors.toSet())) {
			if (desc instanceof LetScopeDescriptor) {
				System.err.println("desc for: " + desc.getIdentifier() + " = " + ((LetScopeDescriptor) desc).getNode().getType().getBytesSize());
				size += ((LetScopeDescriptor) desc).getNode().getType().getBytesSize();
			}
		}
		return size;
	}

}
