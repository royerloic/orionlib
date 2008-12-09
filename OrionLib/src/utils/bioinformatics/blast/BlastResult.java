package utils.bioinformatics.blast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import utils.io.LineReader;
import utils.structures.map.HashSetMap;

public class BlastResult extends ArrayList<BlastHit>
{
	private static final long serialVersionUID = 1L;

	HashSetMap<String, BlastHit> mQueryId2BlastHitMap = new HashSetMap<String, BlastHit>();
	HashSetMap<String, BlastHit> mSubjectId2BlastHitMap = new HashSetMap<String, BlastHit>();

	
	public BlastResult(InputStream pInputStream) throws IOException
	{
		parseBlastResult(pInputStream);
	}

	public BlastResult(File pFile) throws IOException
	{
		this(new FileInputStream(pFile));
	}

	public void parseBlastResult(InputStream pInputStream) throws IOException
	{
		for (String line : LineReader.getLines(pInputStream))
			if (!line.startsWith("#"))
			{
				BlastHit lBlastHit = new BlastHit(line);
				add(lBlastHit);
			}

		buildIndex();
	};

	private void buildIndex()
	{
		for (BlastHit lBlastHit : this)
		{
			mQueryId2BlastHitMap.put(lBlastHit.getQueryId(), lBlastHit);
			mSubjectId2BlastHitMap.put(lBlastHit.getSubjectId(), lBlastHit);
		}

	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		for (BlastHit lBlastHit : this)
		{
			lStringBuilder.append(lBlastHit);
			lStringBuilder.append("\n");
		}
		return lStringBuilder.toString();
	}

}
