/*
 * Created by IntelliJ IDEA.
 * User: Joe
 * Date: Jun 12, 2002
 * Time: 8:44:24 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package utils.web.dyndns;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class HttpAuthRetrieve {
	
	/*
	 * This is pretty ugly, but there needs to be a way to use different
	 * credentials for the same site during the same session.
	 */
	private static final class InternalAuthenticator extends Authenticator {
		public PasswordAuthentication pauth;
		protected PasswordAuthentication getPasswordAuthentication() {
			return pauth;
		}
	}
	private static final InternalAuthenticator auth = new InternalAuthenticator();
	
	static {
		Authenticator.setDefault(auth);
	}

  public static String request(String url) throws IOException, MalformedURLException {
    return request(url, "", "");
  }

	private static final Object lock = new Object();
  public static String request(String url, final String username, final String passwd)
      throws MalformedURLException, IOException {
    final char[] password = passwd.toCharArray();

    synchronized (auth) {
    	PasswordAuthentication newAuth = new PasswordAuthentication(username, password);
    	auth.pauth = newAuth;

      URLConnection conn = null;
      InputStream stream = null;
      Writer out = null;
      try {
        URL uUrl = new URL(url);
        conn = uUrl.openConnection();
        conn.setRequestProperty("User-Agent", "org.jdd.JddClient/0.1");
        stream = new BufferedInputStream(conn.getInputStream());
        Reader in = new InputStreamReader(stream);
        out = new StringWriter();
        for (int b; (b = in.read()) != -1;) {
          out.write(b);
        }
      } finally {
        try {
          if (stream != null) stream.close();
        } catch (IOException e) {
        }
      }
      
      for (int i = 0; i < password.length; i++) {
      	password[i] = (char)0;
      }
      Arrays.fill(password, (char)0);

      return ((StringWriter) out).getBuffer().toString();
    }
  }
}
