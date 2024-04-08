package lu.pcy113.l3.parser.ast.scope;

import java.util.HashMap;

import lu.pcy113.l3.parser.ast.Node;

public class ScopeContainerNode extends Node implements ScopeContainer {
	
	private HashMap<String, ScopeDescriptor> descriptors = new HashMap<>();
	
	@Override
	public boolean containsDescriptor(String name) {
		if(localContainsDescriptor(name)) {
			return true;
		}
		
		ScopeContainerNode parent = this.getContainer();
		
		if(parent == null)
			return false;
		
		return ((ScopeContainerNode) parent).containsDescriptor(name);
	}

	@Override
	public boolean localContainsDescriptor(String name) {
		return descriptors.containsKey(name);
	}

	@Override
	public ScopeDescriptor getLocalDescriptor(String name) {
		return descriptors.get(name);
	}
	
	@Override
	public ScopeDescriptor getClosestDescriptor(String name) {
		if(localContainsDescriptor(name)) {
			return descriptors.get(name);
		}
		return getContainer(name).getClosestDescriptor(name);
	}

	@Override
	public ScopeContainer getContainer(String name) {
		if(localContainsDescriptor(name)) {
			return this;
		}
		
		Node parent = this.getContainer();
		
		if(((ScopeContainerNode) parent).containsDescriptor(name)) {
			return (ScopeContainer) parent;
		}
		
		return null;
	}

	@Override
	public boolean addDescriptor(String name, ScopeDescriptor scopeDescriptor) {
		return descriptors.put(name, scopeDescriptor) == null;
	}

	@Override
	public HashMap<String, ScopeDescriptor> getLocalDescriptors() {
		return descriptors;
	}

	@Override
	public HashMap<String, ScopeDescriptor> getDescriptors() {
		HashMap<String, ScopeDescriptor> ssd = new HashMap<String, ScopeDescriptor>();
		ssd.putAll(ssd);
		
		Node parent = this;
		while(parent != null) {
			parent = parent.getParent();
			if(parent instanceof ScopeContainerNode) {
				ssd.putAll(((ScopeContainerNode) parent).getLocalDescriptors());
			}
		}
		
		return ssd;
	}

}
