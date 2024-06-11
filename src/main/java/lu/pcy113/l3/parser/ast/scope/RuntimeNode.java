package lu.pcy113.l3.parser.ast.scope;

import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;

public class RuntimeNode extends ScopeContainerNode {

	public RuntimeNode(FileNode... root) {
		for (FileNode n : root) {
			add(n);

			addDescriptor(n.getSource(), new FileScopeDescriptor(n));
		}
	}

	public FileNode getMainFile() {
		return (FileNode) children.getFirst();
	}

	public Iterable<FileNode> getSecondaryFiles() {
		return children.stream().skip(1).map(c -> (FileNode) c).collect(Collectors.toSet());
	}

	public FileScopeDescriptor getFileDescriptor(String name) throws CompilerException {
		return (FileScopeDescriptor) getDescriptors(name).stream().filter(c -> c instanceof FileScopeDescriptor).findFirst().orElseThrow(() -> new CompilerException("Couldn't find file: " + name));
	}

}
