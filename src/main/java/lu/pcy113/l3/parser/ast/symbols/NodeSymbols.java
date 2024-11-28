package lu.pcy113.l3.parser.ast.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.impl.JSONConvertible;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.let.MembersAccess;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.math.UnaryExpressionNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

/**
 * 
 */
public class NodeSymbols implements JSONConvertible {

	private Map<String, List<NodeSymbol<?>>> symbols = new HashMap<String, List<NodeSymbol<?>>>();
	private NodeSymbols parent;

	public void registerImport(ImportNode importNode) {
		addSymbol(importNode.getIdentifier().getValue(), new ImportSymbol(importNode));
	}
	
	public void registerLet(LetDefNode letDef) {
		addSymbol(letDef.getIdentifier().getValue(), new LetDefSymbol(letDef));
	}

	public void registerFun(FunDefNode funDef) {
		addSymbol(funDef.getIdentifier().getValue(), new FunDefSymbol(funDef));
	}

	public void checkDependencies(Node expr) {
		if (expr instanceof BinaryExpressionNode bin) {
			checkDependencies(bin.getLeft());
			checkDependencies(bin.getRight());

		} else if (expr instanceof LetDefNode letDef) {
			checkDependencies(letDef.getValue());
			checkDependencies(letDef.getType());

		} else if (expr instanceof TypeNode type) {
			throwError(this.<TypeSymbol>getSymbol(type.getIdent()) == null, expr);

		} else if (expr instanceof FunCallNode funCall) {
			checkDependencies(funCall.getParent());
			funCall.getArgs().forEach(this::checkDependencies);

		} else if (expr instanceof UnaryExpressionNode unary) {
			checkDependencies(unary.getChild());

		} else if (expr instanceof MembersAccess access) {
			checkDependencies(access.getParent());
			
			/*final TypeNode type = evalType(access.getParent());
			throwError(type == null, access.getParent());*/
			// if user type -> match access.getProp() with a property of the type

		} else if (expr instanceof IdentifierNode ident) {
			throwError(getSymbol(ident.getValue()) == null, ident);
			// if user type -> match access.getProp() with a property of the type

		} else if (expr instanceof NumericLiteralNode) {
			// everything ok

		} else {
			throw new L3Exception("Unsupported expression: " + expr);
		}
	}

	private TypeNode evalType(Node parent) {
		if (parent instanceof MembersAccess access) {
			return evalType(access);
		} else if (parent instanceof IdentifierNode ident) {
			final LetDefNode letDef = this.<LetDefSymbol>getSymbol(ident.getValue()).getNode();
			return letDef == null ? null : letDef.getType();
		}

		throw new L3Exception("Unsupported parent type: " + parent);
	}

	private void throwError(boolean b, Node expr) {
		if (b) {
			throw new L3Exception("Symbol not found: " + expr);
		}
	}

	public LetDefSymbol get(LetDefNode letDef) {
		return this.<LetDefSymbol>getSymbol(letDef.getIdentifier().getValue());
	}

	public FunDefSymbol get(FunDefNode funDef) {
		return this.<FunDefSymbol>getSymbol(funDef.getIdentifier().getValue());
	}

	public FunDefSymbol get(FunCallNode funCall) {
		return this.<FunDefSymbol>getSymbol(((IdentifierNode) funCall.getParent()).getValue()); // doesn't support nested fun. call
	}

	@SuppressWarnings("unchecked")
	public <T extends NodeSymbol<?>> T getSymbol(String name) {
		if (contains(name) && getSymbols(name).size() == 1) {
			return (T) getSymbols(name).get(0);
		} else {
			throw new L3Exception("No or multiple matches for: `" + name + "`");
		}
	}

	public List<NodeSymbol<?>> getSymbols(String name) {
		List<NodeSymbol<?>> symbols = this.symbols.get(name);

		if (symbols == null && parent != null) {
			symbols = parent.getSymbols(name);
		}

		return symbols;
	}

	public boolean contains(String name) {
		return symbols.containsKey(name) || (parent != null && parent.contains(name));
	}

	public void addSymbol(String name, NodeSymbol<?> symbol) {
		symbols.computeIfAbsent(name, k -> new ArrayList<>()).add(symbol);
	}

	public void setParent(NodeSymbols parent) {
		this.parent = parent;
	}

	public NodeSymbols getParent() {
		return parent;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject obj = new JSONObject();

		symbols.forEach((k, v) -> {
			obj.accumulate("symbols", new JSONObject().put("key", k).put("values", new JSONArray(v.stream().map(NodeSymbol::toJSONObject).collect(Collectors.toList()))));
		});

		return obj;
	}

	@Override
	public String toString() {
		return toJSONObject().toString(4);
	}

}
