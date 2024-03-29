package utils.bioinformatics.genemap;

import java.io.Serializable;

public class Element implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int mId;
	public String mName;
	public String mDescription;

	public Element(	final int pGeneId,
									final String pGeneName,
									final String pGeneDescription)
	{
		super();
		mId = pGeneId;
		mName = pGeneName;
		mDescription = pGeneDescription;
	}

	public Element(final int pId)
	{
		mId = pId;
	}

	public String toTabDel()
	{
		return mId + "\t" + mName + "\t" + mDescription;
	}

	@Override
	public String toString()
	{
		return "(" + mId + "\t" + mName + "\t" + mDescription + ")";
	}

	public int getId()
	{
		return mId;
	}

	@Override
	public int hashCode()
	{
		return mId;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}

		if (obj instanceof Element)
		{
			final Element other = (Element) obj;
			return mId == other.mId;
		}
		else if (obj instanceof Integer)
		{
			final Integer other = (Integer) obj;
			return other.equals(mId);
		}
		return false;
	}

}
