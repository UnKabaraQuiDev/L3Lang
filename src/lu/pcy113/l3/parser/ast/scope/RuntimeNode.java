package lu.pcy113.l3.parser.ast.scope;

import java.util.stream.Collectors;

import lu.pcy113.l3.parser.ast.Node;

public class RuntimeNode extends ScopeContainerNode {

	public RuntimeNode(FileNode... root) {
		for(FileNode n : root) {
			add(n);
		}
	}
	
	public FileNode getMainFile() {
		return (FileNode) children.getFirst();
	}
	
	public Iterable<Node> getSecondaryFiles() {
		return children.stream().skip(1).collect(Collectors.toSet());
	}

}
