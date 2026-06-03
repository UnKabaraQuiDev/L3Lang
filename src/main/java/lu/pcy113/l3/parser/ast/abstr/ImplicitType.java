package lu.pcy113.l3.parser.ast.abstr;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken.NumericValueType;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.symbols.LetDefSymbol;
import lu.pcy113.l3.parser.ast.type.CastNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class ImplicitType {

	private final TypeNode type;

	public ImplicitType(final TypeNode type) {
		this.type = type;
	}

	public static ImplicitType computeType(final Node node, final ListNode parent) {
		if (node instanceof final NumericLiteralNode num) {
			return new ImplicitType(new PrimitiveTypeNode(num.getValue().getValueType().getTokenType()));
		} else if (node instanceof final IdentifierNode ident) {
			return new ImplicitType(parent.getSymbols().<LetDefSymbol>getSymbol(ident.getValue()).getNode().getType());
		} else if (node instanceof final BinaryExpressionNode bin) {
			return ImplicitType.computeType(bin.getLeft(), parent)
					.merge(ImplicitType.computeType(bin.getRight(), parent));
		} else if (node instanceof final CastNode cast) {
			return new ImplicitType(cast.getCastType());
		}
		throw new L3Exception("Cannot compute type for " + node.getClass().getSimpleName());
	}

	public TypeNode getType() {
		return this.type;
	}

	public ImplicitType merge(final ImplicitType other) {
		if (this.type instanceof PrimitiveTypeNode) {
			if (this.isDouble() || other.isDouble()) {
				return new ImplicitType(new PrimitiveTypeNode(TokenType.DOUBLE));
			} else if (this.isFloat() || other.isFloat()) {
				return new ImplicitType(new PrimitiveTypeNode(TokenType.FLOAT));
			} else if (this.isInt() || other.isInt()) {
				return NumericValueType.byTokenType(((PrimitiveTypeNode) this.getType()).getType())
						.ordinal() > NumericValueType.byTokenType(((PrimitiveTypeNode) other.getType()).getType())
								.ordinal() ? this.clone() : other.clone();
			} else if (this.isBool() && other.isBool()) {
				return new ImplicitType(new PrimitiveTypeNode(TokenType.BOOLEAN));
			}
		}
		throw new L3Exception("Cannot merge " + this.type + " and " + other.type);
	}

	public boolean isDefined() {
		return this.type != null;
	}

	public boolean isDouble() {
		return this.type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) this.type).isDouble();
	}

	public boolean isFloat() {
		return this.type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) this.type).isFloat();
	}

	public boolean isBool() {
		return this.type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) this.type).isBool();
	}

	public boolean isInt() {
		return this.type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) this.type).isInt();
	}

	public boolean isSigned() {
		return this.type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) this.type).isSigned();
	}

	@Override
	public String toString() {
		return this.type.toString();
	}

	@Override
	public ImplicitType clone() {
		return new ImplicitType(this.type);
	}

}
