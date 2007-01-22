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
			Class classDefinition = Class.forName(pClassName);
			lObject = classDefinition.newInstance();
		}
		catch (InstantiationException lInstantiationException)
		{
			System.out.println(lInstantiationException);
		}
		catch (IllegalAccessException lIllegalAccessException)
		{
			System.out.println(lIllegalAccessException);
		}
		catch (ClassNotFoundException lClassNotFoundException)
		{
			System.out.println(lClassNotFoundException);
		}
		return lObject;
	}

}