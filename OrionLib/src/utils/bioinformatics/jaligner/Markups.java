/*
 * $Id: Markups.java,v 1.2 2004/11/30 05:10:29 ahmed Exp $
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

package utils.bioinformatics.jaligner;

/**
 * Markups line characters.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public abstract class Markups
{
	/**
	 * Markup line identity character
	 */
	public static final char IDENTITY = '|';

	/**
	 * Markup line similarity character
	 */
	public static final char SIMILARITY = ':';

	/**
	 * Markup line gap character
	 */
	public static final char GAP = ' ';

	/**
	 * Markup line mismatch character
	 */
	public static final char MISMATCH = '.';
}
