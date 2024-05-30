package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.ScopeBodyNode;

public class FileNode extends ScopeBodyNode {

	private String source;

	public FileNode(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/*
	 * public boolean containsMainFunDescriptor() throws L3Exception { int count = (int) (long) super.getLocalDescriptors("main").stream().filter(c -> c instanceof FunScopeDescriptor) .filter(c -> ((FunScopeDescriptor)
	 * c).getNode().getArgs().isLeaf() && ((FunScopeDescriptor) c).getNode().getReturnType().getType().softEquals(TokenType.INT)) .collect(Collectors.counting()); if (count > 1) { throw new
	 * L3Exception("Multiple main functions found in FileNode:\n" + (super.getLocalDescriptors("main") .stream().filter(c -> c instanceof FunScopeDescriptor) .filter(c -> ((FunScopeDescriptor) c).getNode().isMain()) .map(c ->
	 * ((FunScopeDescriptor) c).getNode().toString(0)).collect(Collectors.joining("\n")))); } else if (count < 1) { throw new L3Exception("No main function found in FileNode (int main(void))"); } else { return true; } }
	 */

	/*
	 * public ScopeDescriptor getMainFunDescriptor() throws L3Exception { return super.getLocalDescriptors("main").stream().filter(c -> c instanceof FunScopeDescriptor) .filter(c -> ((FunScopeDescriptor) c).getNode().getArgs().isLeaf() &&
	 * ((FunScopeDescriptor) c).getNode().getReturnType().getType().softEquals(TokenType.INT)) .findFirst().orElseThrow(() -> new L3Exception("No main function found in FileNode (int main(void))")); }
	 */

	@Override
	public String toString() {
		return super.toString() + "(" + source + ")";
	}

}
