/*
 * Created by IntelliJ IDEA.
 * User: Joe
 * Date: Jun 28, 2002
 * Time: 11:47:44 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package utils.web.dyndns;

public class JddUpdateException extends Exception
{
	public JddUpdateException(String message, Exception e)
	{
		super(message, e);
	}

	public JddUpdateException(String message)
	{
		super(message);
	}

	public JddUpdateException(Exception e)
	{
		super(e);
	}
}
