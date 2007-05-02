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
		return this.mGoIdList;
	};

	public final List<Integer> getInterproIdList()
	{
		return this.mInterproIdList;
	}

	public void addGoId(final Integer pGoId)
	{
		this.mGoIdList.add(pGoId);
	}

	public void addInterproId(final Integer pInterproId)
	{
		this.mInterproIdList.add(pInterproId);
	};

}
