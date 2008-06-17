package utils.structures.fast.set.test;

import java.util.Set;

import utils.structures.fast.set.FastIntegerSet;

public interface IntegerSetFactory
{
	FastIntegerSet createEmptySet();
	
	FastIntegerSet createSet(int...pListOfIntegers);
	
	String getSetTypeName();
	
}
