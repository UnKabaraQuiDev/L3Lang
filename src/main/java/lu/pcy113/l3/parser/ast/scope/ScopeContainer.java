package lu.pcy113.l3.parser.ast.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;

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
	
	FunScopeDescriptor addFunDefDescriptor(FunDefNode node);
	
	LetScopeDescriptor addLetDefDescriptor(LetDefNode node);
	
	boolean addDescriptor(String name, ScopeDescriptor scopeDescriptor);

	boolean containsFunDefDescriptor(FunCallNode node);

	boolean containsFunDefDescriptor(FunDefNode node);
	
	boolean containsLetDefDescriptor(LetDefNode node);

	boolean containsLetDefDescriptor(FieldAccessNode node);

	FunScopeDescriptor getFunDefDescriptor(FunCallNode node) throws CompilerException;
	
	FunScopeDescriptor getFunDefDescriptor(FunDefNode node) throws CompilerException;
	
	Collection<ScopeDescriptor> getFunDefDescriptors(String ident) throws CompilerException;

	LetScopeDescriptor getLetDefDescriptor(LetDefNode node) throws CompilerException;

	LetScopeDescriptor getLetDefDescriptor(LetSetNode node) throws CompilerException;

	LetScopeDescriptor getLetDefDescriptor(FieldAccessNode node) throws CompilerException;

	LetScopeDescriptor getLetDefDescriptor(String ident) throws CompilerException;

}
