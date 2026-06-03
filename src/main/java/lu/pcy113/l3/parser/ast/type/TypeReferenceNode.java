package lu.pcy113.l3.parser.ast.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.parser.ast.abstr.Node;

public class TypeReferenceNode extends TypeNode {

	private final String name;
	private final List<TypeReferenceNode> genericArguments;
	private final int pointerDepth;
	private final boolean varArg;

	public TypeReferenceNode(final String name) {
		this(name, List.of(), 0, false);
	}

	public TypeReferenceNode(final String name, final List<TypeReferenceNode> genericArguments, final int pointerDepth, final boolean varArg) {
		super(name);
		this.name = name;
		this.genericArguments = new ArrayList<>(genericArguments);
		this.pointerDepth = pointerDepth;
		this.varArg = varArg;
	}

	public String getName() {
		return this.name;
	}

	public List<TypeReferenceNode> getGenericArguments() {
		return Collections.unmodifiableList(this.genericArguments);
	}

	public int getPointerDepth() {
		return this.pointerDepth;
	}

	public boolean isVarArg() {
		return this.varArg;
	}

	public boolean isVoid() {
		return this.pointerDepth == 0 && "void".equalsIgnoreCase(this.name);
	}

	public boolean isBoolean() {
		return this.pointerDepth == 0 && ("bool".equalsIgnoreCase(this.name) || "boolean".equalsIgnoreCase(this.name));
	}

	public boolean isFloatingPoint() {
		final String normalized = this.normalizedName();
		return this.pointerDepth == 0 && ("float".equals(normalized) || "float32".equals(normalized)
				|| "double".equals(normalized) || "float64".equals(normalized));
	}

	public boolean isSigned() {
		final String normalized = this.normalizedName();
		return normalized.endsWith("_s") || normalized.endsWith("s");
	}

	public int bitWidth() {
		if (this.pointerDepth > 0) {
			return 64;
		}
		final String normalized = this.normalizedName();
		if (normalized != null) {
			switch (normalized) {
			case "bool":
			case "boolean":
				return 1;
			case "byte":
			case "char":
			case "int8":
			case "var8":
			case "int8_s":
			case "var8_s":
			case "int8s":
			case "var8s":
				return 8;
			case "short":
			case "int16":
			case "var16":
			case "int16_s":
			case "var16_s":
			case "int16s":
			case "var16s":
				return 16;
			case "int":
			case "int32":
			case "var32":
			case "int32_s":
			case "var32_s":
			case "int32s":
			case "var32s":
			case "float":
			case "float32":
				return 32;
			case "long":
			case "int64":
			case "var64":
			case "int64_s":
			case "var64_s":
			case "int64s":
			case "var64s":
			case "double":
			case "float64":
				return 64;
			case "int128":
			case "var128":
			case "int128_s":
			case "var128_s":
			case "int128s":
			case "var128s":
				return 128;
			default:
				break;
			}
		}
		return 64;
	}

	@Override
	public int computeSize() {
		if (this.isVoid()) {
			return 0;
		}
		return Math.max(1, this.bitWidth() / 8);
	}

	private String normalizedName() {
		return this.name.toLowerCase(Locale.ROOT);
	}

	public TypeReferenceNode withPointerDepth(final int pointerDepth) {
		return new TypeReferenceNode(this.name, this.genericArguments, pointerDepth, this.varArg);
	}

	public TypeReferenceNode withVarArg(final boolean varArg) {
		return new TypeReferenceNode(this.name, this.genericArguments, this.pointerDepth, varArg);
	}

	public String displayName() {
		final String genericPart = this.genericArguments.isEmpty() ? ""
				: this.genericArguments.stream().map(TypeReferenceNode::displayName)
						.collect(Collectors.joining(", ", "<", ">"));
		return this.name + genericPart + ":".repeat(this.pointerDepth) + (this.varArg ? "..." : "");
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", this.name).put("display", this.displayName())
				.put("pointerDepth", this.pointerDepth).put("varArg", this.varArg)
				.put("genericArguments", new JSONArray(
						this.genericArguments.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}
}
