/*
 * $Id: RecordFormatter.java,v 1.6 2005/02/19 14:58:10 ahmed Exp $
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

package utils.bioinformatics.jaligner.ui.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logging record formatter
 * 
 * @author Ahmed Moustafa (ahmed@users.sf.net)
 */

public class RecordFormatter extends Formatter
{

	/**
	 * This method is called for every log records
	 * 
	 * @param record
	 */
	@Override
	public String format(final LogRecord record)
	{
		final StringBuffer buffer = new StringBuffer();
		buffer.append(new Date().toString());
		buffer.append(" ");
		buffer.append(record.getLevel());
		buffer.append("\t");
		buffer.append(record.getMessage());
		buffer.append("\n");

		return buffer.toString();
	}

	/**
	 * This method is called just after the handler using this formatter is
	 * created
	 * 
	 * @param handler
	 */
	@Override
	public String getHead(final Handler handler)
	{
		return "";
	}

	/**
	 * This method is called just after the handler using this formatter is closed
	 * 
	 * @param handler
	 */
	@Override
	public String getTail(final Handler handler)
	{
		return "\n";
	}
}