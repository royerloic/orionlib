package org.royerloic.ml.bayes.goint;

import java.util.HashMap;
import java.util.Map;

import org.royerloic.structures.Pair;

public class BayesModel
{
	public double											mInteractionLogLikelyhood;
	public Map<Pair<Integer>, Double>	mGoIdPairToLogLikelyhoodMap	= new HashMap<Pair<Integer>, Double>();
}
