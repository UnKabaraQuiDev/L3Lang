package lu.pcy113.l3.parser.ast;

import org.checkerframework.checker.units.qual.Volume;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ParserException;

public class IfContainerNode extends Node implements AsmNamed, ReturnSafeNode {

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
	public Node add(Node child) {
		if (child instanceof ElseDefNode && hasElse()) {
			throw new RuntimeException(new ParserException("If Container cannot have multiple 'else' statements"));
		} else if (child instanceof FinallyDefNode && hasFinally()) {
			throw new RuntimeException(new ParserException("If Container cannot have multiple 'finally' statements"));
		}

		Node ret = super.add(child);

		sortChildren();

		return ret;
	}

	/**
	 * Probably not necessary bc of parser
	 */
	public void sortChildren() {
		getChildren().sort((a, b) -> a instanceof FinallyDefNode ? 2 : a instanceof ElseDefNode ? 1 : -1);
	}

	public boolean hasFinally() {
		return children.parallelStream().anyMatch(n -> n instanceof FinallyDefNode);
	}

	public FinallyDefNode getFinally() throws CompilerException {
		return (FinallyDefNode) children.parallelStream().filter(n -> n instanceof FinallyDefNode).findFirst().orElseThrow(() -> new CompilerException("This if node has not finally statement."));
	}

	public boolean hasElse() {
		return children.parallelStream().anyMatch(n -> n instanceof ElseDefNode);
	}

	public ElseDefNode getElse() throws CompilerException {
		return (ElseDefNode) children.parallelStream().filter(n -> n instanceof ElseDefNode).findFirst().orElseThrow(() -> new CompilerException("This if node has not else statement."));
	}

	@Override
	public boolean isReturnSafe() {
		boolean allIfsContain = true, elseContain = false, finallyContain = false;

		for (Node n : this) {
			if (n instanceof IfDefNode) {
				allIfsContain &= ((IfDefNode) n).isReturnSafe();
			}
		}

		if (hasElse()) {
			try {
				elseContain = getElse().isReturnSafe();
			} catch (CompilerException e) {
				e.printStackTrace();
			}
		}

		if (hasFinally()) {
			try {
				finallyContain = getFinally().isReturnSafe();
			} catch (CompilerException e) {
				e.printStackTrace();
			}
		}

		return (allIfsContain || finallyContain) && elseContain;
	}

}
