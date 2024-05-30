package lu.pcy113.l3.parser.ast.scope;

import java.util.stream.Collectors;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;

public class FileNode extends ScopeBodyNode {

	private String source;
	private boolean main;

	public FileNode(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public boolean isMain() {
		return main;
	}

	public boolean containsMainFunDescriptor() throws CompilerException {
		int count = (int) (long) super.getLocalDescriptors("main").stream().filter(c -> c instanceof FunScopeDescriptor)
				.filter(c -> ((FunScopeDescriptor) c).getNode().isMain()).collect(Collectors.counting());
		if (count > 1) {
			throw new CompilerException("Multiple main functions found in FileNode:\n" + (super.getLocalDescriptors("main").stream().filter(c -> c instanceof FunScopeDescriptor).filter(c -> ((FunScopeDescriptor) c).getNode().isMain())
					.map(c -> ((FunScopeDescriptor) c).getNode().toString(0)).collect(Collectors.joining("\n"))));
		} else if (count < 1) {
			throw new CompilerException("No main function found in FileNode (int main(void))");
		} else {
			return true;
		}
	}

	public FunScopeDescriptor getMainFunDescriptor() throws CompilerException {
		return (FunScopeDescriptor) super.getLocalDescriptors("main").stream().filter(c -> c instanceof FunScopeDescriptor)
				.filter(c -> ((FunScopeDescriptor) c).getNode().isMain()).findFirst()
				.orElseThrow(() -> new CompilerException("No main function found in FileNode (int main(void))"));
	}

	@Override
	public String toString() {
		return super.toString() + "(" + source + ", main=" + main + ")";
	}

}
