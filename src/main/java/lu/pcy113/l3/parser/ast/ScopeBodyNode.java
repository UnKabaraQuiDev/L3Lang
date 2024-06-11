package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class ScopeBodyNode extends ScopeContainerNode implements AsmNamed, ReturnSafeNode {

	private String asmName;

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	@Override
	public boolean isReturnSafe() {
		boolean returnSafe = false;

		for (Node n : this) {
			if (n instanceof ReturnSafeNode) {
				returnSafe |= ((ReturnSafeNode) n).isReturnSafe();
			}
		}

		return returnSafe;
	}

}
