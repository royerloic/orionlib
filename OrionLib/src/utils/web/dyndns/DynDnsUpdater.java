/*
 * Created by IntelliJ IDEA.
 * User: Joe
 * Date: Jun 5, 2002
 * Time: 1:03:33 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package utils.web.dyndns;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

public class DynDnsUpdater
{
	public static final String URL_DYNDNS = "http://members.dyndns.org/nic/update";

	public static final String WILDCARD_ON = "ON";
	public static final String WILDCARD_OFF = "OFF";
	public static final String WILDCARD_NOCHG = "NOCHG";

	protected String system;
	protected String username;
	protected String password;
	//protected String[] hostnames;
	protected String wildcard;
	protected String mx;
	protected Boolean backmx;
	protected Boolean offline;

	public static class ReturnCode
	{
		private int success;
		private int repeat;
		private String detail;

		public static final int STATUS_SUCCESS = 0;
		public static final int STATUS_WARN = 1;
		public static final int STATUS_FAIL = 2;

		public static final int REPEAT_NEVER = 0;
		public static final int REPEAT_NO = 1;
		public static final int REPEAT_YES = 2;

		public ReturnCode(int success, int repeat, String detail)
		{
			this.success = success;
			this.repeat = repeat;
			this.detail = detail;
		}

		public int getStatus()
		{
			return success;
		}

		public int getRepeat()
		{
			return repeat;
		}

		public String getDetail()
		{
			return detail;
		}
	}

	public DynDnsUpdater(	String system,
												String username,
												String password)
	{
		this(system, username, password,  null, null, null, null);
	}

	public DynDnsUpdater(	String system,
												String username,
												String password,
												String wildcard,
												String mx,
												Boolean backmx,
												Boolean offline)
	{
		this.system = system;
		this.username = username;
		this.password = password;
		this.wildcard = wildcard;
		this.mx = mx;
		this.backmx = backmx;
		this.offline = offline;
	}
	
	public ReturnCode update(String hostname) throws JddUpdateException
	{
		try
		{
			String[] hostnamesArray = new String[] {hostname};
			return DynDnsUpdater.update(system,
																	username,
																	password,
																	hostnamesArray,
																	null,
																	wildcard,
																	mx,
																	backmx,
																	offline);
		}
		catch (IOException e)
		{
			throw new JddUpdateException(e);
		}
	}

	public ReturnCode update(String hostname, String ipAddress) throws JddUpdateException
	{
		try
		{
			String[] hostnamesArray = new String[] {hostname};
			return DynDnsUpdater.update(system,
																	username,
																	password,
																	hostnamesArray,
																	ipAddress,
																	wildcard,
																	mx,
																	backmx,
																	offline);
		}
		catch (IOException e)
		{
			throw new JddUpdateException(e);
		}
	}
	
	public ReturnCode update(Collection hostnames, String ipAddress) throws JddUpdateException
	{
		try
		{
			String[] hostnamesArray = (String[]) hostnames.toArray(new String[0]);
			return DynDnsUpdater.update(system,
																	username,
																	password,
																	hostnamesArray,
																	ipAddress,
																	wildcard,
																	mx,
																	backmx,
																	offline);
		}
		catch (IOException e)
		{
			throw new JddUpdateException(e);
		}
	}

	public static ReturnCode update(String system,
																	String username,
																	String password,
																	String[] hostnames,
																	String ipAddress) throws IOException
	{
		return update(system,
									username,
									password,
									hostnames,
									ipAddress,
									null,
									null,
									null,
									null);
	}

	public static ReturnCode update(String system,
																	String username,
																	String password,
																	String[] hostnames,
																	String ipAddress,
																	String wildcard,
																	String mx,
																	Boolean backmx,
																	Boolean offline) throws IOException
	{
		// this will hold the command querystring
		StringBuffer cmd = new StringBuffer();

		// turn hostnames array into CSV string
		StringBuffer hosts = new StringBuffer();
		for (int i = 0; i < hostnames.length; i++)
		{
			String hostname = hostnames[i];
			hosts.append(hostname);
			if (i < hostnames.length - 1)
				hosts.append(",");
		}

		// build it
		cmd.append("system=" + URLEncoder.encode(system) + "&");
		cmd.append("hostname=" + hosts.toString() + "&");
		if(ipAddress != null)
			cmd.append("myip=" + ipAddress + "&");
		if (wildcard != null)
			cmd.append("wildcard=" + wildcard + "&");
		if (mx != null)
			cmd.append("mx=" + mx + "&");
		if (backmx != null)
			cmd.append("backmx=" + (backmx.booleanValue() ? "YES" : "NO") + "&");
		if (offline != null)
			cmd.append("offline=" + (offline.booleanValue() ? "YES" : "NO") + "&");

		/*System.out.println("DynDnsUpdater making request: " + URL_DYNDNS
												+ "?"
												+ cmd.toString());))/**/

		String retVal = HttpAuthRetrieve.request(	URL_DYNDNS + "?" + cmd.toString(),
																							username,
																							password);
		retVal = retVal.trim();

		//System.out.println("Return val: " + retVal);

		return DynDnsReturnCodeFactory.getInstance().getReturnCode(retVal);
	}

	public static final class DynDnsReturnCodeFactory
	{
		private static final DynDnsReturnCodeFactory singleton = new DynDnsReturnCodeFactory();

		public static DynDnsReturnCodeFactory getInstance()
		{
			return singleton;
		}

		private HashMap data;

		private DynDnsReturnCodeFactory()
		{
			data = new HashMap();

			data.put(	"badauth",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"Bad authorization (username or password)"));
			data.put(	"badsys",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"The system parameter given was not valid"));
			data.put(	"badagent",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"The useragent your client sent has been blocked at the access level."));

			data.put("good", new ReturnCode(ReturnCode.STATUS_SUCCESS,
																			ReturnCode.REPEAT_NO,
																			"Update good and successful, IP updated"));
			data.put(	"nochg",
								new ReturnCode(	ReturnCode.STATUS_WARN,
																ReturnCode.REPEAT_YES,
																"No changes, update considered abusive"));

			data.put(	"notfqdn",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"A Fully-Qualified Domain Name was not provided."));
			data.put(	"nohost",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"The hostname specified does not exist"));
			data.put(	"!donator",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"The offline setting was set, when the user is not a donator"));
			data.put(	"!yours",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NEVER,
																"The hostname specified exists, but not under the username currently being used"));
			data.put(	"!active",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NO,
																"The hostname specified is in a Custom DNS domain which has not yet been activated"));
			data.put(	"abuse",
								new ReturnCode(	ReturnCode.STATUS_FAIL,
																ReturnCode.REPEAT_NO,
																"The hostname specified is blocked for abuse; contact support to be unblocked"));
		}

		public ReturnCode getReturnCode(String code)
		{
			String origCode = code;
			code = code.trim();
			StringTokenizer st = new StringTokenizer(code, " ");
			code = st.nextToken();

			ReturnCode rc = (ReturnCode) data.get(code);
			if (rc == null)
				throw new IllegalArgumentException("Return code \"" + code
																						+ "\" is not recognized");
			return rc;
		}
	}

}
