package utils.pareto;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class ParetoRankingTest
{
	@Test
	public void testParetoRanking1()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0.1, 0.1);
		lParetoRanking.addVector("2", 0.2, 0.01);
		lParetoRanking.addVector("3", 0.01, 0.2);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("1") == 0.0);
		assertTrue(lParetoRanking.getRanking("2") == 0.0);
		assertTrue(lParetoRanking.getRanking("3") == 0.0);
	}

	@Test
	public void testParetoRanking2()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0.1, 0.1);
		lParetoRanking.addVector("2", 0.2, 0.2);
		lParetoRanking.addVector("3", 0.3, 0.3);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("1") == 2.0);
		assertTrue(lParetoRanking.getRanking("2") == 1.0);
		assertTrue(lParetoRanking.getRanking("3") == 0.0);
	}

	@Test
	public void testParetoRanking3()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0.3, 0.1);
		lParetoRanking.addVector("2", 0.1, 0.2);
		lParetoRanking.addVector("3", 0.2, 0.3);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("1") == 0.0);
		assertTrue(lParetoRanking.getRanking("2") == 1.0);
		assertTrue(lParetoRanking.getRanking("3") == 0.0);
	}

	@Test
	public void testParetoRanking4()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0.3, 0.1);
		lParetoRanking.addVector("2", 0.1, 0.2);
		lParetoRanking.addVector("3", 0.2, 0.3);
		lParetoRanking.addVector("4", 0.3, 0.1);
		lParetoRanking.addVector("5", 0.1, 0.2);
		lParetoRanking.addVector("6", 0.2, 0.3);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("1") == 0.0);
		assertTrue(lParetoRanking.getRanking("2") == 1.0);
		assertTrue(lParetoRanking.getRanking("3") == 0.0);
		assertTrue(lParetoRanking.getRanking("4") == 0.0);
		assertTrue(lParetoRanking.getRanking("5") == 1.0);
		assertTrue(lParetoRanking.getRanking("6") == 0.0);
	}

	@Test
	public void testParetoRanking5()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("6", 0.0, 0.5);
		lParetoRanking.addVector("1", 0.1, 0.4);
		lParetoRanking.addVector("2", 0.2, 0.3);
		lParetoRanking.addVector("3", 0.3, 0.2);
		lParetoRanking.addVector("4", 0.4, 0.1);
		lParetoRanking.addVector("5", 0.5, 0);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("1") == 0.0);
		assertTrue(lParetoRanking.getRanking("2") == 0.0);
		assertTrue(lParetoRanking.getRanking("4") == 0.0);
	}

	@Test
	public void testParetoRanking6()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0, 0.1);
		lParetoRanking.addVector("2", 0, 0.2);
		lParetoRanking.addVector("3", 0, 0.3);
		lParetoRanking.addVector("4", 0, 0.4);
		lParetoRanking.addVector("5", 0, 0.5);
		lParetoRanking.addVector("6", 0, 0.6);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking);
		assertTrue(lParetoRanking.getRanking("6") == 0.0);
		assertTrue(lParetoRanking.getRanking("5") == 1.0);
		assertTrue(lParetoRanking.getRanking("4") == 2.0);
	}

	@Test
	public void testParetoRankingToTabDel()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		lParetoRanking.addVector("1", 0, 0.1);
		lParetoRanking.addVector("2", 0, 0.2);
		lParetoRanking.addVector("3", 0, 0.3);
		lParetoRanking.addVector("4", 0, 0.4);
		lParetoRanking.addVector("5", 0, 0.5);
		lParetoRanking.addVector("6", 0, 0.6);

		lParetoRanking.computeRanking();
		System.out.println(lParetoRanking.toTabDel());

	}

	@Test
	public void testParetoRankingAddvectorAsList()
	{
		ParetoRanking<String> lParetoRanking = new ParetoRanking<String>();

		ArrayList<Double> v1 = new ArrayList<Double>();
		v1.add(0.1);
		v1.add(0.2);
		lParetoRanking.addVector("1", v1);
		ArrayList<Double> v2 = new ArrayList<Double>();
		v2.add(0.2);
		v2.add(0.1);
		lParetoRanking.addVector("2", v2);

		lParetoRanking.computeRanking();

	}
}
