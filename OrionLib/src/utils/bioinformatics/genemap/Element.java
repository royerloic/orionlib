package utils.bioinformatics.genemap;

public class Element
{
	public int mId;
	public String mName;
	public String mDescription;

	public Element(int pGeneId, String pGeneName, String pGeneDescription)
	{
		super();
		mId = pGeneId;
		mName = pGeneName;
		mDescription = pGeneDescription;
	}

	public Element(int pId)
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
		return "(" + mId + ", " + mName + ", " + mDescription + ")";
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;

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
