package lu.pcy113.l3.parser.ast.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class PreprocessorDirectiveNode extends Node {

	private final String name;
	private final List<String> rawTokens;

	public PreprocessorDirectiveNode(final String name, final List<Token> rawTokens) {
		this.name = name;
		this.rawTokens = new ArrayList<>();
		for (final Token token : rawTokens) {
			this.rawTokens.add(token.getType().name());
		}
	}

	public String getName() {
		return this.name;
	}

	public List<String> getRawTokens() {
		return Collections.unmodifiableList(this.rawTokens);
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("name", this.name).put("rawTokens", new JSONArray(this.rawTokens));
	}
}
