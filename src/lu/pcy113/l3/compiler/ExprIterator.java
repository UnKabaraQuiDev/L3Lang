package lu.pcy113.l3.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import lu.pcy113.l3.parser.ast.Expr;
import lu.pcy113.l3.parser.ast.containers.ExprContainer;

public class ExprIterator implements Iterator<Expr>, Iterable<Expr> {
	
	private Stack<Iterator<Expr>> stack = new Stack<>();

	public ExprIterator(Expr root) {
		reset(root);
	}
	
	protected void reset(Expr root) {
		stack.push(Collections.singleton(root).iterator());
	}

	@Override
	public boolean hasNext() {
		while (!stack.isEmpty()) {
			if (stack.peek().hasNext()) {
				return true;
			} else {
				stack.pop();
			}
		}
		return false;
	}

	@Override
	public Expr next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more elements in the nested set of expressions.");
		}

		Iterator<Expr> currentIterator = stack.peek();
		Expr currentExpr = currentIterator.next();

		if (currentExpr instanceof ExprContainer) {
			stack.push(((ExprContainer) currentExpr).getChildren().iterator());
		}

		return currentExpr;
	}
	
	public boolean hasNextSibling() {
		if (stack.size() < 2) {
			return false; // No siblings at the root level
		}

		Iterator<Expr> currentIterator = stack.pop();
		boolean hasNextSibling = currentIterator.hasNext();
		stack.push(currentIterator);

		return hasNextSibling;
	}

	public Expr findNext(Class<?> clazz) {
		while (hasNext()) {
			Expr currentExpr = next();
			if (clazz.isInstance(currentExpr)) {
				return currentExpr;
			}
		}
		return null;
	}

	public Collection<Expr> findAll(Class<?> clazz) {
		List<Expr> result = new ArrayList<>();
		while (hasNext()) {
			Expr currentExpr = next();
			if (clazz.isInstance(currentExpr)) {
				result.add(currentExpr);
			}
		}
		return result;
	}

	@Override
	public Iterator<Expr> iterator() {
		return this;
	}

	
}