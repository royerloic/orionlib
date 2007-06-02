/*
 * Created on 08.11.2004 by Dipl.-Inf. MSc. Ing Loic Royer
 */
package utils.java;

/**
 * @author Dipl.-Inf. MSc. Ing. Loic Royer
 * 
 */
public interface IObject
{
	public Object clone();

	public void copyFrom(Object pObject);

	public int hashCode();

	public boolean equals(Object pObject);

	public String toString();

}