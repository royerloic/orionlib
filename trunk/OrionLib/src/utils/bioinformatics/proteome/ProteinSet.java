package utils.bioinformatics.proteome;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class ProteinSet implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap<String, Protein> mIdToProteinMap = new HashMap<String, Protein>();

	public ProteinSet()
	{
		super();
	}

	public void add(final Protein pProtein)
	{
		mIdToProteinMap.put(pProtein.getId(), pProtein);
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		for (final Protein lProtein : mIdToProteinMap.values())
		{
			lStringBuilder.append(lProtein);
			lStringBuilder.append("\n");
		}
		return lStringBuilder.toString();
	}

	public int getNumberOfProteins()
	{
		return mIdToProteinMap.size();
	}

	public Protein getProteinById(final String pId)
	{
		return mIdToProteinMap.get(pId);
	}

	public Collection<Protein> getSet()
	{
		return mIdToProteinMap.values();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ (mIdToProteinMap == null ? 0 : mIdToProteinMap.hashCode());
		return result;
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
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final ProteinSet other = (ProteinSet) obj;
		if (mIdToProteinMap == null)
		{
			if (other.mIdToProteinMap != null)
			{
				return false;
			}
		}
		else if (!mIdToProteinMap.equals(other.mIdToProteinMap))
		{
			return false;
		}
		return true;
	}

}
