package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class FileScopeDescriptor extends ScopeDescriptor {

	private FileNode node;

	public FileScopeDescriptor(FileNode file) {
		super(new IdentifierLitNode(new IdentifierToken(TokenType.IDENT, 0, 0, file.getSource())));
		this.node = file;
	}

	public FileNode getNode() {
		return node;
	}

}
