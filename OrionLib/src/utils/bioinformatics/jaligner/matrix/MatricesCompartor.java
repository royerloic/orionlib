/*
 * $Id: MatricesCompartor.java,v 1.1 2005/01/11 16:48:25 ahmed Exp $
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package utils.bioinformatics.jaligner.matrix;

import java.util.Comparator;

/**
 * Comparator to sort the scoring matrices by their names.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public class MatricesCompartor implements Comparator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(final Object o1, final Object o2)
	{
		final String s1 = (String) o1;
		final String s2 = (String) o2;

		final int index1 = firstDigitIndex(s1);
		final int index2 = firstDigitIndex(s2);

		if (index1 == -1 || index2 == -1)
		{
			return s1.compareToIgnoreCase(s2);
		}
		else
		{
			final String s3 = s1.substring(0, index1);
			final String s4 = s2.substring(0, index2);
			if (s3.equalsIgnoreCase(s4))
			{
				return new Integer(s1.substring(index1)).compareTo(new Integer(s2.substring(index2)));
			}
			else
			{
				return s1.compareToIgnoreCase(s2);
			}
		}
	}

	/**
	 * Returns the index of the first digit in a String. If there are no digits,
	 * returns -1.
	 * 
	 * @param s
	 *          String to be searched for the digits in
	 * @return int
	 */
	private int firstDigitIndex(final String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) >= '0' && s.charAt(i) <= '9')
			{
				return i;
			}
		}
		return -1;
	}
}