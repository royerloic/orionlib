package utils.bioinformatics.blast;

import java.io.Serializable;

import utils.structures.fast.graph.Edge;

public class BlastHit extends  Edge<String> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public double identity;
	public double evalue;
	public double bitscore;
	public int allength;
	public int mismatches;
	public int gapopenings;
	public int qstart;
	public int qend;
	public int sstart;
	public int send;
	
	public BlastHit(String pLine)
	{
		super();
		String[] lArray = pLine.split("\t");
		
		setFirstNode(lArray[0]);
		setSecondNode(lArray[1]);
		
		identity = Double.parseDouble(lArray[2]);
		evalue   = Double.parseDouble(lArray[10]);
		bitscore = Double.parseDouble(lArray[11]);
		
		allength = Integer.parseInt(lArray[3]);
		mismatches = Integer.parseInt(lArray[4]);
		gapopenings = Integer.parseInt(lArray[5]);
		qstart = Integer.parseInt(lArray[6]);
		qend = Integer.parseInt(lArray[7]);
		sstart = Integer.parseInt(lArray[8]);
		send = Integer.parseInt(lArray[9]);
		
	}
	
	public String getQueryId()
	{
		return getFirstNode();
	}
	
	public String getSubjectId()
	{
		return getSecondNode();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + qend;
		result = prime * result + qstart;
		result = prime * result + send;
		result = prime * result + sstart;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		BlastHit other = (BlastHit) obj;
		if (qend != other.qend)
		{
			return false;
		}
		if (qstart != other.qstart)
		{
			return false;
		}
		if (send != other.send)
		{
			return false;
		}
		if (sstart != other.sstart)
		{
			return false;
		}
		return super.asymEquals(obj);
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
		lStringBuilder.append(getQueryId()+"\t");
		lStringBuilder.append(getSubjectId()+"\t");
		lStringBuilder.append(identity+"\t");
		lStringBuilder.append(allength+"\t");
		lStringBuilder.append(mismatches+"\t");
		lStringBuilder.append(gapopenings+"\t");
		lStringBuilder.append(qstart+"\t");
		lStringBuilder.append(qend+"\t");
		lStringBuilder.append(sstart+"\t");
		lStringBuilder.append(send+"\t");
		lStringBuilder.append(evalue+"\t");
		lStringBuilder.append(bitscore+"\t");	
		
		return lStringBuilder.toString();
	}
	
	
	
	

}
