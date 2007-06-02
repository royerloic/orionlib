package utils.ml.bayes.goint;

import java.util.HashMap;
import java.util.Map;

import utils.structures.Pair;

public class BayesModel
{
	public double											mInteractionLogLikelyhood;
	public Map<Pair<Integer>, Double>	mGoIdPairToLogLikelyhoodMap	= new HashMap<Pair<Integer>, Double>();
}
