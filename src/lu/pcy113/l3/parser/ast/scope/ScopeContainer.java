package lu.pcy113.l3.parser.ast.scope;

import java.util.HashMap;

public interface ScopeContainer {
	
	boolean containsDescriptor(String name);
	boolean localContainsDescriptor(String name);
	
	HashMap<String, ScopeDescriptor> getLocalDescriptors(); 
	HashMap<String, ScopeDescriptor> getDescriptors(); 
	
	ScopeDescriptor getDescriptor(String name);
	
	ScopeContainer getContainer(String name);
	
	boolean addDescriptor(String name, ScopeDescriptor scopeDescriptor);
	
}
