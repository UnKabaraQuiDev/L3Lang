package lu.pcy113.l3.parser.ast;

import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.pclib.PCUtils;

public class StructDefNode extends ScopeContainerNode {

	public StructDefNode(IdentifierLitNode lit) {
		add(lit);
	}

	public IdentifierLitNode getIdent() {
		return (IdentifierLitNode) children.get(0);
	}

	public List<LetDefNode> getFields() {
		return children.stream().filter(c -> c instanceof LetDefNode).map(c -> (LetDefNode) c).collect(Collectors.toList());
	}

	// TODO
	public List<FunDefNode> getFuns() {
		return children.stream().filter(c -> c instanceof FunDefNode).map(c -> (FunDefNode) c).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return super.toString() + " -> " + PCUtils.try_(() -> getParentContainer().getStructDefDescriptor(getIdent()), (e) -> e.getMessage());
	}

}
