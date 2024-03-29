/*
 * $Id: Directions.java,v 1.10 2004/11/30 05:10:29 ahmed Exp $
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
 * Traceback directions.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public abstract class Directions
{
	/**
	 * Traceback direction stop
	 */
	public static final byte STOP = 0;
	/**
	 * Traceback direction left
	 */
	public static final byte LEFT = 1;
	/**
	 * Traceback direction diagonal
	 */
	public static final byte DIAGONAL = 2;
	/**
	 * Traceback direction up
	 */
	public static final byte UP = 3;
}