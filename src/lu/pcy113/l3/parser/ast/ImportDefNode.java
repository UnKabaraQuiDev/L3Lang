package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.utils.FileUtils;

public class ImportDefNode extends Node {

	private StringLiteralToken path;
	private IdentifierToken ident;

	public ImportDefNode(StringLiteralToken strLit) {
		this.path = strLit;
		this.ident = new IdentifierToken(TokenType.IDENT, strLit.getLine(), strLit.getColumn(),
				FileUtils.getExtension(path.getValue()));
	}

	public ImportDefNode(StringLiteralToken strLit, IdentifierToken ident) {
		this.path = strLit;
		if (ident == null)
			this.ident = new IdentifierToken(TokenType.IDENT, strLit.getLine(), strLit.getColumn(),
					FileUtils.getExtension(path.getValue()));
		else
			this.ident = ident;
	}

	public StringLiteralToken getPath() {
		return path;
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public String getIdentValue() {
		return ident.getValue();
	}

}
