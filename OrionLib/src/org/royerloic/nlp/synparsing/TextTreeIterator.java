package org.royerloic.nlp.synparsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class TextTreeIterator<O> implements Iterator<TextTree<O>>
{

	Deque<Iterator<TextTree<O>>>	mIteratorStack	= new ArrayDeque<Iterator<TextTree<O>>>();

	public TextTreeIterator(final TextTree pTextTree)
	{
		this.mIteratorStack.addFirst(pTextTree.getChildTreeList().iterator());
	}

	public boolean hasNext()
	{
		if (this.mIteratorStack.getFirst().hasNext())
			return true;

		this.mIteratorStack.removeFirst();
		if (this.mIteratorStack.isEmpty())
			return false;
		return hasNext();
	}

	public TextTree<O> next()
	{
		final TextTree<O> lTextTree = this.mIteratorStack.getFirst().next();
		this.mIteratorStack.addFirst(lTextTree.getChildTreeList().iterator());
		return lTextTree;
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Cannot remove while recursing through the TextTree!");
	}

}
