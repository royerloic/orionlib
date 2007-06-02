package utils.nlp.synparsing;

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

	public TextTree(final String pString)
	{
		this.mOffset = 0;
		this.mLength = pString.length();
		this.mCharArray = new char[this.mLength];
		pString.getChars(this.mOffset, this.mLength, this.mCharArray, 0);
		this.mChildTreeList = new ArrayList<TextTree<O>>();
	}

	public TextTree(final char[] pCharArray, final int pOffset, final int pLength)
	{
		if ((pOffset < 0) || (pLength < 0))
			throw new StringIndexOutOfBoundsException("pOffset and pLength must be positive!");
		this.mOffset = pOffset;
		this.mLength = pLength;
		this.mCharArray = pCharArray;
		this.mChildTreeList = new ArrayList<TextTree<O>>();
	}

	public TextTree(final char[] pCharArray, final int pOffset, final int pLength, final O pAnnotation)
	{
		this(pCharArray, pOffset, pLength);
		this.mAnnotation = pAnnotation;
	}

	public final String getString()
	{
		return new String(this.mCharArray, this.mOffset, this.mLength);
	}

	public final O getAnnotation()
	{
		return this.mAnnotation;
	}

	public void setAnnotation(final O pAnnotation)
	{
		this.mAnnotation = pAnnotation;
	}

	public final List<TextTree<O>> getChildTreeList()
	{
		return Collections.unmodifiableList(this.mChildTreeList);
	}

	public final List<TextTree<O>> getAllDescendents()
	{
		final List<TextTree<O>> lDescendentTreeList = new ArrayList<TextTree<O>>();
		getAllDescendentsRecursive(lDescendentTreeList, this);
		return lDescendentTreeList;

	}

	private final static <O> void getAllDescendentsRecursive(	final List<TextTree<O>> pDescendentTreeList,
																														final TextTree<O> pTextTree)
	{
		final List<TextTree<O>> lChildTreeList = pTextTree.getChildTreeList();
		pDescendentTreeList.addAll(lChildTreeList);
		for (final TextTree<O> lChildTree : lChildTreeList)
			getAllDescendentsRecursive(pDescendentTreeList, lChildTree);
	}

	public final List<TextTree<O>> getAllChildsWithin(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		final List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (final TextTree<O> lTextTree : this.mChildTreeList)
			if (lTextTree.isWithin(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public final List<TextTree<O>> getAllChildsTouching(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		final List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (final TextTree<O> lTextTree : this.mChildTreeList)
			if (lTextTree.isTouching(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public final List<TextTree<O>> getAllDescendentsWithin(final int pRelativeOffset, final int pLength)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		final List<TextTree<O>> lSubTreeList = new ArrayList<TextTree<O>>();
		for (final TextTree<O> lTextTree : getAllDescendents())
			if (lTextTree.isWithin(lAbsoluteOffset, pLength))
				lSubTreeList.add(lTextTree);
		return lSubTreeList;
	}

	public static final class AnnotationResult<O>
	{
		private boolean			mIsAnnotatable;
		private TextTree<O>	mCollidingTextTree;

		public AnnotationResult(final boolean pIsAnnotatable)
		{
			this.mIsAnnotatable = pIsAnnotatable;
		}

		public AnnotationResult(final boolean pIsAnnotatable, final TextTree<O> pCollidingTextTree)
		{
			this.mIsAnnotatable = pIsAnnotatable;
			this.mCollidingTextTree = pCollidingTextTree;
		}

		public TextTree<O> getCollidingTextTree()
		{
			return this.mCollidingTextTree;
		}

		public boolean isAnnotatable()
		{
			return this.mIsAnnotatable;
		}

	}

	public final AnnotationResult annotate(final int pRelativeOffset, final int pLength, final O pAnnotation)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		final TextTree<O> lNewTextTree = new TextTree<O>(this.mCharArray, lAbsoluteOffset, pLength, pAnnotation);

		if (this.equals(lNewTextTree))
			return new AnnotationResult<O>(true);

		if (this.mChildTreeList.isEmpty())
		{
			this.mChildTreeList.add(lNewTextTree);
			fix();
			return new AnnotationResult<O>(true);
		}

		for (final TextTree<O> lTextTree : new ArrayList<TextTree<O>>(this.mChildTreeList))
			if (lNewTextTree.isWithin(lTextTree))
			{
				if (lTextTree.isVoid())
				{
					this.mChildTreeList.remove(lTextTree);
					this.mChildTreeList.add(lNewTextTree);
					fix();
					return new AnnotationResult<O>(true);
				}
				return lTextTree.annotate(lTextTree.absoluteToRelative(lAbsoluteOffset), pLength, pAnnotation);
			}

		final List<TextTree<O>> lTouchedTrees = getAllChildsTouching(pRelativeOffset, pLength);
		for (final TextTree<O> lTree : lTouchedTrees)
			if (!lTree.isWithin(lNewTextTree) && !lTree.isVoid())
				return new AnnotationResult<O>(false, lTree);

		this.mChildTreeList.removeAll(lTouchedTrees);

		final TextTree<O> lTextTreeLeftHitting = lTouchedTrees.get(0);
		final TextTree<O> lTextTreeRightHitting = lTouchedTrees.get(lTouchedTrees.size() - 1);

		if (lTextTreeLeftHitting.isVoid())
			lTouchedTrees.remove(lTextTreeLeftHitting);

		if (lTextTreeRightHitting.isVoid())
			lTouchedTrees.remove(lTextTreeRightHitting);

		lNewTextTree.mChildTreeList.addAll(lTouchedTrees);
		lNewTextTree.fix();

		this.mChildTreeList.add(lNewTextTree);
		fix();

		return new AnnotationResult<O>(true);
	}

	private final void addVoid(final int pAbsoluteOffset, final int pLength)
	{
		addSubTree(pAbsoluteOffset, pLength, null);
	}

	private final void addSubTree(final int pAbsoluteOffset, final int pLength, final O pAnnotation)
	{
		final TextTree<O> lTextTree = new TextTree<O>(this.mCharArray, pAbsoluteOffset, pLength, pAnnotation);
		this.mChildTreeList.add(lTextTree);
		Collections.sort(this.mChildTreeList);
	}

	public final void addChildrenAccordingToCutPoints(final List<Integer> pCutPointsList, final O pAnnotation)
	{
		for (final Integer lInteger : pCutPointsList)
			if ((lInteger < this.mOffset) || (lInteger >= this.mOffset + this.mLength))
				throw new StringIndexOutOfBoundsException("A cut point lies outside of the range of this TextTree! ("
						+ lInteger + ")");

		if (!pCutPointsList.contains(this.mOffset))
			pCutPointsList.add(this.mOffset);
		if (!pCutPointsList.contains(this.mOffset + this.mLength))
			pCutPointsList.add(this.mOffset + this.mLength);
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
		Collections.sort(this.mChildTreeList);
		int lCurrentIndex = this.mOffset;
		for (final TextTree<O> lTextTree : new ArrayList<TextTree<O>>(this.mChildTreeList))
		{
			final int lNextIndex = lTextTree.mOffset;
			final int lGapLength = lNextIndex - lCurrentIndex;
			if (lGapLength != 0)
				addVoid(lCurrentIndex, lGapLength);
			lCurrentIndex = lTextTree.mOffset + lTextTree.mLength;
		}
		{
			final int lNextIndex = this.mOffset + this.mLength;
			final int lGapLength = lNextIndex - lCurrentIndex;
			if (lGapLength != 0)
				addVoid(lCurrentIndex, lGapLength);
		}
		if (this.mChildTreeList.size() == 1)
			if (this.mChildTreeList.get(0).isVoid())
				this.mChildTreeList.remove(0);
	}

	private final boolean isWithin(final int pAbsoluteOffset, final int pLength)
	{
		if ((pLength < 0) || (pAbsoluteOffset < 0))
			throw new StringIndexOutOfBoundsException("pBegin must be positive aswell as pLength!");
		return (this.mOffset >= pAbsoluteOffset) && (this.mOffset + this.mLength <= pAbsoluteOffset + pLength);
	}

	public final boolean isWithin(final TextTree<O> pTextTree)
	{
		return isWithin(pTextTree.mOffset, pTextTree.mLength);
	}

	private final boolean isTouching(final int pAbsoluteOffset, final int pLength)
	{
		if ((pLength < 0) || (pAbsoluteOffset < 0))
			throw new StringIndexOutOfBoundsException("pBegin must be positive aswell as pLength!");
		final int lStart1 = this.mOffset;
		final int lEnd1 = this.mOffset + this.mLength;

		final int lStart2 = pAbsoluteOffset;
		final int lEnd2 = pAbsoluteOffset + pLength;

		return (lEnd2 > lStart1) && (lStart2 < lEnd1);
	}

	private boolean contains(final int pAbsoluteOffset)
	{
		return (this.mOffset <= pAbsoluteOffset) && (pAbsoluteOffset < this.mOffset + this.mLength);
	}

	public final int length()
	{
		return this.mLength;
	}

	public final TextTree<O> getChildAt(final int pRelativeOffset)
	{
		final int lAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		for (final TextTree lTextTree : this.mChildTreeList)
			if (lTextTree.contains(lAbsoluteOffset))
				return lTextTree;
		return null;
	}

	public final boolean isEmpty()
	{
		return this.mLength == 0;
	}

	public final boolean isLeaf()
	{
		return this.mChildTreeList.isEmpty();
	}

	public final boolean isVoid()
	{
		return this.mAnnotation == null;
	}

	public final int relativeToAbsolute(final int pRelativeOffset)
	{
		return this.mOffset + pRelativeOffset;
	}

	public final int absoluteToRelative(final int pAbsoluteOffset)
	{
		return pAbsoluteOffset - this.mOffset;
	}

	public final char charAt(final int pRelativeOffset)
	{
		if ((pRelativeOffset < 0) || (pRelativeOffset >= this.mLength))
			throw new StringIndexOutOfBoundsException(pRelativeOffset);
		final int pAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		return this.mCharArray[pAbsoluteOffset];
	}

	public final void setCharAt(final int pRelativeOffset, final char pChar)
	{
		if ((pRelativeOffset < 0) || (pRelativeOffset >= this.mLength))
			throw new StringIndexOutOfBoundsException(pRelativeOffset);
		final int pAbsoluteOffset = relativeToAbsolute(pRelativeOffset);
		this.mCharArray[pAbsoluteOffset] = pChar;
	}

	public final CharSequence subSequence(final int pStart, final int pEnd)
	{
		final int lStartAbsoluteOffset = relativeToAbsolute(pStart);
		final int lEndAbsoluteOffset = relativeToAbsolute(pEnd);
		if ((lStartAbsoluteOffset < this.mOffset) || (lEndAbsoluteOffset < this.mOffset))
			throw new StringIndexOutOfBoundsException();
		if ((lStartAbsoluteOffset > this.mOffset + this.mLength) || (lEndAbsoluteOffset > this.mOffset + this.mLength))
			throw new StringIndexOutOfBoundsException();
		final int lSubSequenceLength = lEndAbsoluteOffset - lStartAbsoluteOffset;
		return ((lStartAbsoluteOffset == this.mOffset) && (lSubSequenceLength == this.mLength)) ? this : new String(
				this.mCharArray, lStartAbsoluteOffset, lSubSequenceLength);
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
	public boolean equals(final Object pObj)
	{
		final TextTree lTextTree = (TextTree<O>) pObj;
		final boolean isAnnotationEqual = this.mAnnotation == null ? this.mAnnotation == lTextTree.mAnnotation : this.mAnnotation
				.equals(lTextTree.mAnnotation);
		return (this.mOffset == lTextTree.mOffset) && (this.mLength == lTextTree.mLength)
				&& Arrays.equals(this.mCharArray, lTextTree.mCharArray) && isAnnotationEqual;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(this.mCharArray) ^ this.mOffset ^ this.mLength ^ this.mAnnotation.hashCode();
	}

	@Override
	public String toString()
	{
		final String lAnnotationString = this.mAnnotation == null ? "null" : this.mAnnotation.toString();
		return "'" + getString() + "':" + lAnnotationString;
	}

	public String toTreeString()
	{
		final StringBuffer lStringBuffer = new StringBuffer();
		toTreeStringRecursive(lStringBuffer, 0);
		return lStringBuffer.toString();
	}

	private void toTreeStringRecursive(final StringBuffer pStringBuffer, final int pIndentLevel)
	{
		if (pIndentLevel >= 3)
		{
			final char[] lCharArray = new char[pIndentLevel + 1];
			for (int i = 0; i < pIndentLevel + 1; i++)
				lCharArray[i] = ' ';
			lCharArray[pIndentLevel - 3] = '+';
			lCharArray[pIndentLevel - 2] = '-';
			if (!this.mChildTreeList.isEmpty())
				lCharArray[pIndentLevel - 1] = '|';

			final String lIndentSpace = new String(lCharArray);
			pStringBuffer.append(lIndentSpace);
		}
		else if (pIndentLevel == 2)
			pStringBuffer.append(" +- ");

		pStringBuffer.append(toString());
		pStringBuffer.append('\n');

		for (final TextTree<O> lTree : this.mChildTreeList)
			lTree.toTreeStringRecursive(pStringBuffer, pIndentLevel + 2);
	}

	@Override
	protected TextTree<O> clone()
	{
		final char[] lCharArray = new char[this.mCharArray.length];
		System.arraycopy(this.mCharArray, 0, lCharArray, 0, this.mCharArray.length);
		final TextTree<O> lTextTree = new TextTree<O>(lCharArray, this.mOffset, this.mLength);
		lTextTree.setAnnotation(this.mAnnotation);
		return lTextTree;
	}

}
