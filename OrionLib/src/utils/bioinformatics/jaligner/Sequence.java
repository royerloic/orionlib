/*
 * $Id: Sequence.java,v 1.12 2005/04/14 14:44:45 ahmed Exp $
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

import java.io.Serializable;

/**
 * A basic (nucleic or protein) sequence. It's a wrapper to
 * {@link java.lang.String}.
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public class Sequence implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3256721801357898297L;

	/**
	 * Sequence type nucleic.
	 */
	public static final int NUCLEIC = 0;

	/**
	 * Sequence type protein.
	 */
	public static final int PROTEIN = 1;

	/**
	 * Sequence
	 */
	private String sequence;

	/**
	 * Sequence id.
	 */
	private String id = null;

	/**
	 * Sequence description.
	 */
	private String description = null;

	/**
	 * Sequence type.
	 */
	private int type = PROTEIN;

	/**
	 * Constructor
	 */
	public Sequence()
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param sequence
	 */
	public Sequence(final String sequence)
	{
		super();
		this.sequence = sequence;
	}

	/**
	 * Constructor
	 * 
	 * @param sequence
	 * @param id
	 * @param description
	 * @param type
	 */
	public Sequence(final String sequence,
									final String id,
									final String description,
									final int type)
	{
		super();
		this.sequence = sequence;
		this.id = id;
		this.description = description;
		this.type = type;
	}

	/**
	 * Returns the sequence string
	 * 
	 * @return Returns the sequence
	 */
	public String getSequence()
	{
		return sequence;
	}

	/**
	 * Sets the sequence string
	 * 
	 * @param sequence
	 *          The sequence to set
	 */
	public void setSequence(final String sequence)
	{
		this.sequence = sequence;
	}

	/**
	 * Returns the sequence id
	 * 
	 * @return Returns the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the sequence id
	 * 
	 * @param id
	 *          The id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Returns the sequence description
	 * 
	 * @return Returns the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the sequence description
	 * 
	 * @param description
	 *          The description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * Returns the sequence type (nucleic or protein)
	 * 
	 * @return Returns the type
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the sequence type (nucleic or protein)
	 * 
	 * @param type
	 *          The type to set
	 */
	public void setType(final int type)
	{
		this.type = type;
	}

	/**
	 * Returns the length of the sequence
	 * 
	 * @return sequence length
	 */
	public int length()
	{
		return this.sequence.length();
	}

	/**
	 * Returns a subsequence
	 * 
	 * @param index
	 *          start index
	 * @param length
	 *          length of subsequence
	 * @return subsequence
	 */
	public String subsequence(final int index, final int length)
	{
		return this.sequence.substring(index, index + length);
	}

	/**
	 * Returns the acid at specific location in the sequence
	 * 
	 * @param index
	 * @return acid at index
	 */
	public char acidAt(final int index)
	{
		return this.sequence.charAt(index);
	}

	/**
	 * Returns the sequence as an array of characters.
	 * 
	 * @return array of chars.
	 */
	public char[] toArray()
	{
		return this.sequence.toCharArray();
	}
}