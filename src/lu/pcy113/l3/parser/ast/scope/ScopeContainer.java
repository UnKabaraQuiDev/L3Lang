package lu.pcy113.l3.parser.ast.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.VarNumNode;

public interface ScopeContainer {
	
	boolean containsDescriptor(String name);
	boolean localContainsDescriptor(String name);
	
	HashMap<String, List<ScopeDescriptor>> getLocalDescriptors(); 
	HashMap<String, List<ScopeDescriptor>> getDescriptors(); 
	
	ScopeDescriptor getLocalDescriptor(String name);
	ScopeDescriptor getClosestDescriptor(String name);
	
	Collection<ScopeDescriptor> getLocalDescriptors(String name);
	Collection<ScopeDescriptor> getDescriptors(String name);
	
	ScopeContainer getContainer(String name);
	
	boolean addDescriptor(String name, ScopeDescriptor scopeDescriptor);
	
	boolean containsFunDescriptor(FunCallNode node);
	boolean containsLetTypeDefDescriptor(LetTypeDefNode node);
	boolean containsFunDescriptor(FunDefNode node);
	boolean containsLetTypeDefDescriptor(VarNumNode node);
	boolean containsStructScopeDescriptor(StructDefNode node) throws CompilerException;
	
	FunScopeDescriptor getFunDescriptor(FunCallNode node) throws CompilerException;
	LetScopeDescriptor getLetTypeDefDescriptor(LetTypeDefNode node) throws CompilerException;
	FunScopeDescriptor getFunDescriptor(FunDefNode node) throws CompilerException;
	LetScopeDescriptor getLetTypeDefDescriptor(VarNumNode node) throws CompilerException;
	LetScopeDescriptor getLetTypeDefDescriptor(String ident) throws CompilerException;
	StructScopeDescriptor getStructScopeDescriptor(StructDefNode node) throws CompilerException;
	StructScopeDescriptor getStructScopeDescriptor(String ident) throws CompilerException;
	
}
