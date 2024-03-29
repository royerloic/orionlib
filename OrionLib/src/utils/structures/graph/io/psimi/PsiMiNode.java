package utils.structures.graph.io.psimi;

import java.util.ArrayList;
import java.util.List;

import utils.structures.graph.Node;

public class PsiMiNode extends Node
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Integer> mGoIdList = new ArrayList<Integer>();
	List<Integer> mInterproIdList = new ArrayList<Integer>();

	public PsiMiNode(final String pName)
	{
		super(pName);

	}

	public final List<Integer> getGoIdList()
	{
		return mGoIdList;
	};

	public final List<Integer> getInterproIdList()
	{
		return mInterproIdList;
	}

	public void addGoId(final Integer pGoId)
	{
		mGoIdList.add(pGoId);
	}

	public void addInterproId(final Integer pInterproId)
	{
		mInterproIdList.add(pInterproId);
	};

}
