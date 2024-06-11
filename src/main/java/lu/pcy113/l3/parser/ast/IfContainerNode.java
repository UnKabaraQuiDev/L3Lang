package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;

public class IfContainerNode extends Node implements AsmNamed {

	private String asmName;

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	public boolean hasFinally() {
		return children.parallelStream().anyMatch(n -> n instanceof FinallyDefNode);
	}

	public FinallyDefNode getFinally() throws CompilerException {
		return (FinallyDefNode) children.parallelStream().filter(n -> n instanceof FinallyDefNode).findFirst().orElseThrow(() -> new CompilerException("This if node has not finally statement."));
	}

}
