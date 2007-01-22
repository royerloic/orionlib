package org.royerloic.nlp.synparsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class TextTreeIterator<O> implements Iterator<TextTree<O>>
{

	Deque<Iterator<TextTree<O>>>	mIteratorStack	= new ArrayDeque<Iterator<TextTree<O>>>();

	public TextTreeIterator(TextTree pTextTree)
	{
		mIteratorStack.addFirst(pTextTree.getChildTreeList().iterator());
	}

	public boolean hasNext()
	{
		if (mIteratorStack.getFirst().hasNext())
			return true;

		mIteratorStack.removeFirst();
		if (mIteratorStack.isEmpty())
			return false;
		return hasNext();
	}

	public TextTree<O> next()
	{
		TextTree<O> lTextTree = mIteratorStack.getFirst().next();
		mIteratorStack.addFirst(lTextTree.getChildTreeList().iterator());
		return lTextTree;
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Cannot remove while recursing through the TextTree!");
	}

}
