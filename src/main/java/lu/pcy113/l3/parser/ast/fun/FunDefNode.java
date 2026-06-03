package lu.pcy113.l3.parser.ast.fun;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.let.ArgDefNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class FunDefNode extends ListNode {

	private final TypeNode returnType;
	private final IdentifierNode identifier;
	private final List<ArgDefNode> args;

	public FunDefNode(final ListNode file, final TypeNode returnType, final IdentifierNode identifier, final List<ArgDefNode> args) {
		super.symbols.setParent(file.getSymbols());
		this.returnType = returnType;
		this.identifier = identifier;
		this.args = args;
	}

	public boolean isMain() {
		return this.returnType instanceof final PrimitiveTypeNode pt && pt.getType().matches(TokenType.INT_8)
				&& "main".equals(this.identifier.getValue()) && this.args.isEmpty();
	}

	public TypeNode getReturnType() {
		return this.returnType;
	}

	public IdentifierNode getIdentifier() {
		return this.identifier;
	}

	public List<ArgDefNode> getArgs() {
		return this.args;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("returnType", this.returnType.toJSONObject())
				.put("identifier", this.identifier.toJSONObject())
				.put("args", new JSONArray(this.args.stream().map(Node::toJSONObject).collect(Collectors.toList())));
	}

}
