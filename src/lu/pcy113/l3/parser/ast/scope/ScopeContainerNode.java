package lu.pcy113.l3.parser.ast.scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.Node;

public class ScopeContainerNode extends Node implements ScopeContainer {

	private HashMap<String, List<ScopeDescriptor>> descriptors = new HashMap<>();

	@Override
	public boolean containsDescriptor(String name) {
		if (localContainsDescriptor(name)) {
			return true;
		}

		ScopeContainer parent = this.getParentContainer();

		if (parent == null)
			return false;

		return parent.containsDescriptor(name);
	}

	@Override
	public boolean localContainsDescriptor(String name) {
		return descriptors.containsKey(name);
	}

	@Override
	public ScopeDescriptor getLocalDescriptor(String name) {
		return descriptors.get(name).get(0);
	}

	@Override
	public ScopeDescriptor getClosestDescriptor(String name) {
		if (localContainsDescriptor(name)) {
			return descriptors.get(name).get(0);
		}
		ScopeContainer container = getContainer(name);
		if (container == null) {
			return null;
		}
		return container.getClosestDescriptor(name);
	}

	@Override
	public ScopeContainer getContainer(String name) {
		if (localContainsDescriptor(name)) {
			return this;
		}

		Node parent = this.getParentContainer();

		if (((ScopeContainerNode) parent).containsDescriptor(name)) {
			return (ScopeContainer) parent;
		}

		return null;
	}

	@Override
	public boolean addDescriptor(String name, ScopeDescriptor scopeDescriptor) {
		if (descriptors.containsKey(name)) {
			return descriptors.get(name).add(scopeDescriptor);
		} else {
			List<ScopeDescriptor> list = new ArrayList<ScopeDescriptor>();
			list.add(scopeDescriptor);
			return descriptors.put(name, list) == null;
		}
	}

	@Override
	public HashMap<String, List<ScopeDescriptor>> getLocalDescriptors() {
		return descriptors;
	}

	@Override
	public HashMap<String, List<ScopeDescriptor>> getDescriptors() {
		HashMap<String, List<ScopeDescriptor>> ssd = new HashMap<>();
		ssd.putAll(getLocalDescriptors());
		if (getParentContainer() != null) {
			getParentContainer().getDescriptors().entrySet().forEach(c -> {
				if (ssd.containsKey(c.getKey())) {
					ssd.get(c.getKey()).addAll(c.getValue());
				} else {
					ssd.put(c.getKey(), c.getValue());
				}
			});
		}
		return ssd;
	}

	@Override
	public Collection<ScopeDescriptor> getLocalDescriptors(String name) {
		List<ScopeDescriptor> map = new ArrayList<ScopeDescriptor>();
		descriptors.entrySet().stream().filter(c -> c.getKey().equals(name)).map(c -> c.getValue()).forEach(c -> map.addAll(c));
		return map.stream().collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public Collection<ScopeDescriptor> getDescriptors(String name) {
		List<ScopeDescriptor> map = new ArrayList<ScopeDescriptor>();
		getDescriptors().entrySet().stream().filter(c -> c.getKey().equals(name)).map(c -> c.getValue()).forEach(c -> map.addAll(c));
		return map.stream().collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public FunScopeDescriptor getFunDefDescriptor(FunCallNode node) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FunCallNode) node).getIdent().asString());

		col = col.stream().filter(c -> c instanceof FunScopeDescriptor).filter(c -> {
			try {
				return ((FunScopeDescriptor) c).getNode().getParams().paramsEquals(node.getParams());
			} catch (CompilerException e) {
				e.printStackTrace();
				return false;
			}
		}).collect(Collectors.toCollection(ArrayList::new));

		if (col.size() > 1) {
			throw new CompilerException("FunDefNode: " + node + ", defined multiple times with the same arguments:\n"
					+ col.stream().map(c -> ((FunScopeDescriptor) c).getNode().toString() + " (" + ((FunScopeDescriptor) c).getNode().getParams().toString(0) + ")").collect(Collectors.joining(", \n")));
		}

		return (FunScopeDescriptor) col.stream().findFirst().orElseThrow(() -> new CompilerException("FunDefNode: " + node + " (" + node.getParams().toString(0) + ")" + ", not defined."));
	}

	@Override
	public LetScopeDescriptor getLetDefDescriptor(LetDefNode node) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(((LetDefNode) node).getIdent().asString());

		return (LetScopeDescriptor) col.stream().filter(c -> c instanceof LetScopeDescriptor).filter(c -> ((LetScopeDescriptor) c).getNode().equals(node)).findFirst()
				.orElseThrow(() -> new CompilerException("LetDef: " + node + ", not defined."));
	}

	@Override
	public LetScopeDescriptor getLetDefDescriptor(LetSetNode node) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(((LetSetNode) node).getLetIdent().getValue());

		return (LetScopeDescriptor) col.stream().filter(c -> c instanceof LetScopeDescriptor).filter(c -> ((LetScopeDescriptor) c).getNode().equals(node)).findFirst()
				.orElseThrow(() -> new CompilerException("LetDef: " + node + ", not defined."));
	}

	@Override
	public FunScopeDescriptor getFunDefDescriptor(FunDefNode node) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FunDefNode) node).getIdent().getLeaf().getValue());

		return (FunScopeDescriptor) col.stream().filter(c -> c instanceof FunScopeDescriptor).filter(c -> ((FunScopeDescriptor) c).getNode().equals(node)).findFirst()
				.orElseThrow(() -> new CompilerException("FunDefNode: " + node + ", not defined."));
	}

	@Override
	public LetScopeDescriptor getLetDefDescriptor(FieldAccessNode node) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FieldAccessNode) node).getIdent().asString());

		return (LetScopeDescriptor) col.stream().filter(c -> c instanceof LetScopeDescriptor).findFirst().orElseThrow(() -> new CompilerException("LetDef: " + node + ", not defined."));
	}

	@Override
	public LetScopeDescriptor getLetDefDescriptor(String ident) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(ident);

		return (LetScopeDescriptor) col.stream().filter(c -> c instanceof LetScopeDescriptor).findFirst().orElseThrow(() -> new CompilerException("Let: " + ident + ", not defined."));
	}

	@Override
	public boolean containsFunDefDescriptor(FunCallNode node) {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FunCallNode) node).getIdent().getLeaf().getValue());

		col = col.stream().filter(c -> c instanceof FunScopeDescriptor).filter(c -> {
			try {
				return ((FunScopeDescriptor) c).getNode().getParams().paramsEquals(node.getParams());
			} catch (CompilerException e) {
				e.printStackTrace();
				return false;
			}
		}).collect(Collectors.toCollection(ArrayList::new));

		return col.stream().findFirst().isPresent();
	}

	@Override
	public boolean containsLetDefDescriptor(LetDefNode node) {
		Collection<ScopeDescriptor> col = this.getDescriptors(((LetDefNode) node).getIdent().asString());

		return col.stream().filter(c -> c instanceof LetScopeDescriptor).findFirst().isPresent();
	}

	@Override
	public boolean containsFunDefDescriptor(FunDefNode node) {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FunDefNode) node).getIdent().getLeaf().getValue());

		return col.stream().filter(c -> c instanceof FunScopeDescriptor).filter(c -> ((FunScopeDescriptor) c).getNode().equals(node)).findFirst().isPresent();
	}

	@Override
	public boolean containsLetDefDescriptor(FieldAccessNode node) {
		Collection<ScopeDescriptor> col = this.getDescriptors(((FieldAccessNode) node).getIdent().asString());

		return col.stream().filter(c -> c instanceof LetScopeDescriptor).findFirst().isPresent();
	}

	@Override
	public FunScopeDescriptor addFunDefDescriptor(FunDefNode node) {
		FunScopeDescriptor fun = new FunScopeDescriptor(node.getIdent(), node);
		addDescriptor(node.getIdent().getLeaf().getValue(), fun);
		return fun;
	}

	@Override
	public LetScopeDescriptor addLetDefDescriptor(LetDefNode node) {
		LetScopeDescriptor fun = new LetScopeDescriptor(node.getIdent(), node);
		addDescriptor(node.getIdent().getLeaf().getValue(), fun);
		return fun;
	}

	@Override
	public Collection<ScopeDescriptor> getFunDefDescriptors(String ident) throws CompilerException {
		Collection<ScopeDescriptor> col = this.getDescriptors(ident);

		col = col.stream().filter(c -> c instanceof FunScopeDescriptor).collect(Collectors.toCollection(ArrayList::new));

		return col;
	}

}
