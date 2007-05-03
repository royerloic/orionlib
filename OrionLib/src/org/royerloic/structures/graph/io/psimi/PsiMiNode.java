package org.royerloic.structures.graph.io.psimi;

import java.util.ArrayList;
import java.util.List;

import org.royerloic.structures.graph.Node;

public class PsiMiNode extends Node
{
	List<Integer>	mGoIdList				= new ArrayList<Integer>();
	List<Integer>	mInterproIdList	= new ArrayList<Integer>();

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
