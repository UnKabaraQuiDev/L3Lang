package lu.pcy113.l3.parser.ast.expr;

import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;

public class ExplicitArrayDefNode extends ExprNode {

	public ExplicitArrayDefNode(List<ExprNode> funArgs) {
		funArgs.forEach(this::add); // can't use addAll bc it doesn't register the parent in the child
	}

	public List<ExprNode> getExprs() {
		return children.stream().map(c -> (ExprNode) c).collect(Collectors.toList());
	}

	@Override
	public boolean isDouble() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

}
