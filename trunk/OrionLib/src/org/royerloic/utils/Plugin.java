/*
 * Optimal Project
 * 
 * M.Sc. Ing. Loic Royer
 * 
 * Fraunhofer IWU Dresden
 *  
 */

package org.royerloic.utils;

/**
 * Plugin
 * 
 * @author MSc. Ing. Loic Royer
 */
public final class Plugin
{

	/**
	 * Hides the default constructor.
	 */
	private Plugin()
	{
	};

	/**
	 * Returns a reference to an instance of class <code>pClassName</code>
	 * 
	 * @param pClassName
	 *          class name.
	 * @return class instance reference reference
	 */
	public static Object load(final String pClassName)
	{
		Object lObject = null;
		try
		{
			final Class classDefinition = Class.forName(pClassName);
			lObject = classDefinition.newInstance();
		}
		catch (final InstantiationException lInstantiationException)
		{
			System.out.println(lInstantiationException);
		}
		catch (final IllegalAccessException lIllegalAccessException)
		{
			System.out.println(lIllegalAccessException);
		}
		catch (final ClassNotFoundException lClassNotFoundException)
		{
			System.out.println(lClassNotFoundException);
		}
		return lObject;
	}

}