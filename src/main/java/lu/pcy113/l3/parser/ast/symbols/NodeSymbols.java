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
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.let.MembersAccess;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.math.UnaryExpressionNode;
import lu.pcy113.l3.parser.ast.type.CastNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

/**
 *
 */
public class NodeSymbols implements JSONConvertible {

	private final Map<String, List<NodeSymbol<?>>> symbols = new HashMap<>();
	private NodeSymbols parent;

	public void registerFile(final FileNode fileNode) {
		this.addSymbol(fileNode.getPackageNode().getValue() + "." + fileNode.getName(), new FileSymbol(fileNode));
	}

	public void registerImport(final ImportNode importNode) {
		this.addSymbol(importNode.getIdentifier().getValue(), new ImportSymbol(importNode));
	}

	public void registerLet(final LetDefNode letDef) {
		this.addSymbol(letDef.getIdentifier().getValue(), new LetDefSymbol(letDef));
	}

	public void registerFun(final FunDefNode funDef) {
		this.addSymbol(funDef.getIdentifier().getValue(), new FunDefSymbol(funDef));
	}

	public void checkDependencies(final Node expr) {
		if (expr instanceof final BinaryExpressionNode bin) {
			this.checkDependencies(bin.getLeft());
			this.checkDependencies(bin.getRight());

		} else if (expr instanceof final LetDefNode letDef) {
			this.checkDependencies(letDef.getValue());
			this.checkDependencies(letDef.getType());

		} else if (expr instanceof final TypeNode type) {
			this.throwError(this.<TypeSymbol>getSymbol(type.getIdent()) == null, expr);

		} else if (expr instanceof final FunCallNode funCall) {
			// checkDependencies(funCall.getParent());
			// functions are checked at compile time
			funCall.getArgs().forEach(this::checkDependencies);

		} else if (expr instanceof final UnaryExpressionNode unary) {
			this.checkDependencies(unary.getChild());

		} else if (expr instanceof final MembersAccess access) {
			this.checkDependencies(access.getParent());

			/*
			 * final TypeNode type = evalType(access.getParent()); throwError(type == null,
			 * access.getParent());
			 */
			// if user type -> match access.getProp() with a property of the type

		} else if (expr instanceof final IdentifierNode ident) {
			this.throwError(this.getSymbol(ident.getValue()) == null, ident);
			// if user type -> match access.getProp() with a property of the type

		} else if (expr instanceof NumericLiteralNode || expr instanceof StringLiteralNode) {
			// everything ok

		} else if (expr instanceof final CastNode cast) {
			this.checkDependencies(cast.getValue());
		} else {
			throw new L3Exception("Unsupported expression: " + expr);
		}
	}

	private TypeNode evalType(final Node parent) {
		if (parent instanceof final MembersAccess access) {
			return this.evalType(access);
		} else if (parent instanceof final IdentifierNode ident) {
			final LetDefNode letDef = this.<LetDefSymbol>getSymbol(ident.getValue()).getNode();
			return letDef == null ? null : letDef.getType();
		}

		throw new L3Exception("Unsupported parent type: " + parent);
	}

	private void throwError(final boolean b, final Node expr) {
		if (b) {
			throw new L3Exception("Symbol not found: " + expr);
		}
	}

	public LetDefSymbol get(final LetDefNode letDef) {
		return this.<LetDefSymbol>getSymbol(letDef.getIdentifier().getValue());
	}

	public FunDefSymbol get(final FunDefNode funDef) {
		return this.<FunDefSymbol>getSymbol(funDef.getIdentifier().getValue());
	}

	public FunDefSymbol get(final FunCallNode funCall) {
		return this.<FunDefSymbol>getSymbol(((IdentifierNode) funCall.getParent()).getValue()); // doesn't support
																								// nested fun. call
	}

	@SuppressWarnings("unchecked")
	public <T extends NodeSymbol<?>> T getSymbol(final String name) {
		if (this.contains(name) && this.getSymbols(name).size() == 1) {
			return (T) this.getSymbols(name).get(0);
		} else {
			throw new L3Exception("No or multiple matches for: `" + name + "`");
		}
	}

	public List<NodeSymbol<?>> getSymbols(final String name) {
		List<NodeSymbol<?>> symbols = this.symbols.get(name);

		if (symbols == null && this.parent != null) {
			symbols = this.parent.getSymbols(name);
		}

		return symbols;
	}

	public boolean contains(final String name) {
		return this.symbols.containsKey(name) || this.parent != null && this.parent.contains(name);
	}

	public void addSymbol(final String name, final NodeSymbol<?> symbol) {
		this.symbols.computeIfAbsent(name, k -> new ArrayList<>()).add(symbol);
	}

	public void setParent(final NodeSymbols parent) {
		this.parent = parent;
	}

	public NodeSymbols getParent() {
		return this.parent;
	}

	@Override
	public JSONObject toJSONObject() {
		final JSONObject obj = this.parent == null ? new JSONObject() : this.parent.toJSONObject();

		this.symbols.forEach((k, v) -> {
			obj.accumulate("symbols", new JSONObject().put("key", k).put("values",
					new JSONArray(v.stream().map(NodeSymbol::toJSONObject).collect(Collectors.toList()))));
		});

		return obj;
	}

	@Override
	public String toString() {
		return this.toJSONObject().toString(4);
	}

}
