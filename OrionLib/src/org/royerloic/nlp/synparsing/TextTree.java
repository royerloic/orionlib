package org.royerloic.nlp.synparsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class TextTree<O> implements CharSequence, Iterable<TextTree<O>>, Comparable<TextTree<O>>
{
	private final char[]			mCharArray;

	private final int					mOffset;

	private final int					mLength;

	private O									mAnnotation	= null;

	private List<TextTree<O>>	mChildTreeList;

	public TextTree(String pString)
	{
		this.mOffset = 0;
		this.mLength = pString.length();
		mCharArray = new char[mLength];
		pString.getChars(mOffset, mLength, mCharArray, 0);
		mChildTreeList = new ArrayList<TextTree<O>>();
	}

	public TextTree(final char[] pCharArray, final int pOffset, final int pLength)
	{
		if (pOffset < 0 || pLength < 0)
		{
			throw new StringIndexOutOfBoundsException("pOffset and pLength must be positive!");
		}
		this.mOffset = pOffset;
		this.mLength = pLength;
		mCharArray = pCharArray;
		mChildTreeList = new ArrayList<TextTree<O>>();
	}

	public TextTree(final char[] pCharArray, final int pOffset, final int pLength, final O pAnnotation)
	{
		this(pCharArray, pOffset, pLength);
		mAnnotation = pAnnotation;
	}

	public final String getString()
	{
		return new String(mCharArray, mOffset, mLength);
	}

	public final O getAnnotation()
	{
		return mAnnotation;
	}

	public void setAnnotation(O pAnnotation)
	{
		mAnnotation = pAnnotation;
	}

	public final List<TextTree<O>> getChildTreeList()
	{
		return Collections.unmodifiableList(mChildTreeList);
	}

	public final List<TextTree<O>> getAllDescendents()
	{
		List<TextTree<O>> lDescendentTreeList = new ArrayList<TextTree<O>>();
		getAllDescendentsRecursive(lDescendentTreeList, this);
		return lDescendentTreeList;

	}

	private final static <O> void getAllDescendentsRecursive(	final List<TextTree<O>> pDescendentTreeList,
																														final TextTree<O> pTextTree)
	{
		List<TextTree<O>> lChildTreeList = pTextTree.getChildTreeList();
		pDescendentTreeList.addAll(lChildTreeList);
		for (TextTree<O> lChildTree : lChildTreeList)
		{
			getAllDescendentsRecursive(pDescendentTreeList, lChildTree);
		}
	}

	public final List<TextTree<O>> getAllChildsWithin(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (TextTree<O> lTextTree : mChildTreeList)
			if (lTextTree.isWithin(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public final List<TextTree<O>> getAllChildsTouching(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (TextTree<O> lTextTree : mChildTreeList)
			if (lTextTree.isTouching(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public final List<TextTree<O>> getAllDescendentsWithin(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (TextTree<O> lTextTree : getAllDescendents())
			if (lTextTree.isWithin(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public static final class AnnotationResult<O>
	{
		private boolean			mIsAnnotatable;
		private TextTree<O>	mCollidingTextTree;

		public AnnotationResult(boolean pIsAnnotatable)
		{
			mIsAnnotatable = pIsAnnotatable;
		}

		public AnnotationResult(boolean pIsAnnotatable, TextTree<O> pCollidingTextTree)
		{
			mIsAnnotatable = pIsAnnotatable;
			mCollidingTextTree = pCollidingTextTree;
		}

		public TextTree<O> getCollidingTextTree()
		{
			return mCollidingTextTree;
		}

		public boolean isAnnotatable()
		{
			return mIsAnnotatable;
		}

	}

	public final AnnotationResult annotate(final int pRelativeOffset, final int pLength, final O pAnnotation)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		TextTree<O> lNewTextTree = new TextTree<O>(this.mCharArray, lAbsoluteOffset, pLength, pAnnotation);

		if (this.equals(lNewTextTree))
			return new AnnotationResult<O>(true);

		if (mChildTreeList.isEmpty())
		{
			mChildTreeList.add(lNewTextTree);
			fix();
			return new AnnotationResult<O>(true);
		}

		for (TextTree<O> lTextTree : new ArrayList<TextTree<O>>(mChildTreeList))
			if (lNewTextTree.isWithin(lTextTree))
			{
				if (lTextTree.isVoid())
				{
					mChildTreeList.remove(lTextTree);
					mChildTreeList.add(lNewTextTree);
					fix();
					return new AnnotationResult<O>(true);
				}
				return lTextTree.annotate(lTextTree.absoluteToRelative(lAbsoluteOffset), pLength, pAnnotation);
			}

		List<TextTree<O>> lTouchedTrees = getAllChildsTouching(pRelativeOffset, pLength);
		for (TextTree<O> lTree : lTouchedTrees)
			if (!lTree.isWithin(lNewTextTree) && !lTree.isVoid())
				return new AnnotationResult<O>(false, lTree);

		mChildTreeList.removeAll(lTouchedTrees);

		TextTree<O> lTextTreeLeftHitting = lTouchedTrees.get(0);
		TextTree<O> lTextTreeRightHitting = lTouchedTrees.get(lTouchedTrees.size() - 1);

		if (lTextTreeLeftHitting.isVoid())
			lTouchedTrees.remove(lTextTreeLeftHitting);

		if (lTextTreeRightHitting.isVoid())
			lTouchedTrees.remove(lTextTreeRightHitting);

		lNewTextTree.mChildTreeList.addAll(lTouchedTrees);
		lNewTextTree.fix();

		mChildTreeList.add(lNewTextTree);
		fix();

		return new AnnotationResult<O>(true);
	}

	private final void addVoid(final int pAbsoluteOffset, final int pLength)
	{
		addSubTree(pAbsoluteOffset, pLength, null);
	}

	private final void addSubTree(final int pAbsoluteOffset, final int pLength, final O pAnnotation)
	{
		TextTree<O> lTextTree = new TextTree<O>(this.mCharArray, pAbsoluteOffset, pLength, pAnnotation);
		mChildTreeList.add(lTextTree);
		Collections.sort(mChildTreeList);
	}

	public final void addChildrenAccordingToCutPoints(final List<Integer> pCutPointsList, final O pAnnotation)
	{
		for (Integer lInteger : pCutPointsList)
			if ((lInteger < mOffset) || (lInteger >= mOffset + mLength))
			{
				throw new StringIndexOutOfBoundsException("A cut point lies outside of the range of this TextTree! ("
						+ lInteger + ")");
			}

		if (!pCutPointsList.contains(mOffset))
			pCutPointsList.add(mOffset);
		if (!pCutPointsList.contains(mOffset + mLength))
			pCutPointsList.add(mOffset + mLength);
		Collections.sort(pCutPointsList);

		for (int i = 0; i < pCutPointsList.size() - 1; i++)
		{
			final int lOffset = pCutPointsList.get(i);
			final int lLength = pCutPointsList.get(i + 1) - lOffset;
			addSubTree(lOffset, lLength, pAnnotation);
		}
	}

	private final void fix()
	{
		Collections.sort(mChildTreeList);
		int lCurrentIndex = mOffset;
		for (TextTree<O> lTextTree : new ArrayList<TextTree<O>>(mChildTreeList))
		{
			final int lNextIndex = lTextTree.mOffset;
			final int lGapLength = lNextIndex - lCurrentIndex;
			if (lGapLength != 0)
			{
				addVoid(lCurrentIndex, lGapLength);
			}
			lCurrentIndex = lTextTree.mOffset + lTextTree.mLength;
		}
		{
			final int lNextIndex = mOffset + mLength;
			final int lGapLength = lNextIndex - lCurrentIndex;
			if (lGapLength != 0)
			{
				addVoid(lCurrentIndex, lGapLength);
			}
		}
		if (mChildTreeList.size() == 1)
			if (mChildTreeList.get(0).isVoid())
				mChildTreeList.remove(0);
	}

	private final boolean isWithin(final int pAbsoluteOffset, final int pLength)
	{
		if ((pLength < 0) || (pAbsoluteOffset < 0))
		{
			throw new StringIndexOutOfBoundsException("pBegin must be positive aswell as pLength!");
		}
		return (mOffset >= pAbsoluteOffset) && (mOffset + mLength <= pAbsoluteOffset + pLength);
	}

	public final boolean isWithin(final TextTree<O> pTextTree)
	{
		return isWithin(pTextTree.mOffset, pTextTree.mLength);
	}

	private final boolean isTouching(final TextTree<O> pTextTree)
	{
		return isTouching(pTextTree.mOffset, pTextTree.mLength);
	}

	private final boolean isTouching(final int pAbsoluteOffset, final int pLength)
	{
		if ((pLength < 0) || (pAbsoluteOffset < 0))
		{
			throw new StringIndexOutOfBoundsException("pBegin must be positive aswell as pLength!");
		}
		final int lStart1 = mOffset;
		final int lEnd1 = mOffset + mLength;

		final int lStart2 = pAbsoluteOffset;
		final int lEnd2 = pAbsoluteOffset + pLength;

		return (lEnd2 > lStart1) && (lStart2 < lEnd1);
	}

	private boolean contains(final int pAbsoluteOffset)
	{
		return (mOffset <= pAbsoluteOffset) && (pAbsoluteOffset < mOffset + mLength);
	}

	public final int length()
	{
		return mLength;
	}

	public final TextTree<O> getChildAt(final int pRelativeOffset)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		for (TextTree lTextTree : mChildTreeList)
			if (lTextTree.contains(lAbsoluteOffset))
				return lTextTree;
		return null;
	}

	public final boolean isEmpty()
	{
		return mLength == 0;
	}

	public final boolean isLeaf()
	{
		return mChildTreeList.isEmpty();
	}

	public final boolean isVoid()
	{
		return mAnnotation == null;
	}

	public final int relativeToAbsolute(final int pRelativeOffset)
	{
		return mOffset + pRelativeOffset;
	}

	public final int absoluteToRelative(final int pAbsoluteOffset)
	{
		return pAbsoluteOffset - mOffset;
	}

	public final char charAt(final int pRelativeOffset)
	{
		if ((pRelativeOffset < 0) || (pRelativeOffset >= mLength))
		{
			throw new StringIndexOutOfBoundsException(pRelativeOffset);
		}
		final int pAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		return mCharArray[pAbsoluteOffset];
	}

	public final void setCharAt(final int pRelativeOffset, final char pChar)
	{
		if ((pRelativeOffset < 0) || (pRelativeOffset >= mLength))
		{
			throw new StringIndexOutOfBoundsException(pRelativeOffset);
		}
		final int pAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		mCharArray[pAbsoluteOffset] = pChar;
	}

	public final CharSequence subSequence(final int pStart, final int pEnd)
	{
		final int lStartAbsoluteOffset = relativeToAbsolute(pStart);
		final int lEndAbsoluteOffset = relativeToAbsolute(pEnd);
		if ((lStartAbsoluteOffset < mOffset) || (lEndAbsoluteOffset < mOffset))
		{
			throw new StringIndexOutOfBoundsException();
		}
		if ((lStartAbsoluteOffset > mOffset + mLength) || (lEndAbsoluteOffset > mOffset + mLength))
		{
			throw new StringIndexOutOfBoundsException();
		}
		final int lSubSequenceLength = lEndAbsoluteOffset - lStartAbsoluteOffset;
		return ((lStartAbsoluteOffset == mOffset) && (lSubSequenceLength == mLength)) ? this : new String(
				mCharArray, lStartAbsoluteOffset, lSubSequenceLength);
	}

	public final Iterator<TextTree<O>> iterator()
	{
		return new TextTreeIterator<O>(this);
	}

	public final int compareTo(final TextTree pO)
	{
		if (!this.mCharArray.equals(pO.mCharArray))
			throw new UnsupportedOperationException(
					"Cannot compare two TextTree that do not share the same char array!");

		if (this.mOffset == pO.mOffset)
			return pO.mLength - this.mLength;

		return this.mOffset - pO.mOffset;
	}

	@Override
	public boolean equals(Object pObj)
	{
		TextTree lTextTree = (TextTree<O>) pObj;
		boolean isAnnotationEqual = mAnnotation == null ? mAnnotation == lTextTree.mAnnotation : mAnnotation
				.equals(lTextTree.mAnnotation);
		return mOffset == lTextTree.mOffset && mLength == lTextTree.mLength
				&& Arrays.equals(mCharArray, lTextTree.mCharArray) && isAnnotationEqual;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(mCharArray) ^ mOffset ^ mLength ^ mAnnotation.hashCode();
	}

	@Override
	public String toString()
	{
		final String lAnnotationString = mAnnotation == null ? "null" : mAnnotation.toString();
		return "'" + getString() + "':" + lAnnotationString;
	}

	public String toTreeString()
	{
		StringBuffer lStringBuffer = new StringBuffer();
		toTreeStringRecursive(lStringBuffer, 0);
		return lStringBuffer.toString();
	}

	private void toTreeStringRecursive(final StringBuffer pStringBuffer, final int pIndentLevel)
	{
		if (pIndentLevel >= 3)
		{
			char[] lCharArray = new char[pIndentLevel + 1];
			for (int i = 0; i < pIndentLevel + 1; i++)
				lCharArray[i] = ' ';
			lCharArray[pIndentLevel - 3] = '+';
			lCharArray[pIndentLevel - 2] = '-';
			if (!mChildTreeList.isEmpty())
				lCharArray[pIndentLevel - 1] = '|';

			String lIndentSpace = new String(lCharArray);
			pStringBuffer.append(lIndentSpace);
		}
		else if (pIndentLevel == 2)
		{
			pStringBuffer.append(" +- ");
		}

		pStringBuffer.append(toString());
		pStringBuffer.append('\n');

		for (TextTree<O> lTree : mChildTreeList)
		{
			lTree.toTreeStringRecursive(pStringBuffer, pIndentLevel + 2);
		}
	}

	protected TextTree<O> clone()
	{
		char[] lCharArray = new char[mCharArray.length];
		System.arraycopy(mCharArray, 0, lCharArray, 0, mCharArray.length);
		TextTree<O> lTextTree = new TextTree<O>(lCharArray, mOffset, mLength);
		lTextTree.setAnnotation(mAnnotation);
		return lTextTree;
	}

}
