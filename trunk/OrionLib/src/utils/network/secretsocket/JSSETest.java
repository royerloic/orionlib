package utils.network.secretsocket;

import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;



public class JSSETest
{
	public static void main(String[] args)
	{
		try
		{
			Class.forName("com.sun.net.ssl.internal.ssl.Provider");
		}
		catch (Exception e)
		{
			System.out.println("JSSE is NOT installed correctly!");
			System.exit(1);
		}
		System.out.println("JSSE is installed correctly!");

		String trustStore = System.getProperty("javax.net.ssl.trustStore");
		if (trustStore == null)
			System.out.println("javax.net.ssl.trustStore is not defined");
		else
			System.out.println("javax.net.ssl.trustStore = " + trustStore);

		try
		{
			Cipher lCipher = Cipher.getInstance("DESede");
			System.out.println(lCipher);
			//lCipher.
		}
		catch (Exception e)
		{
			// An exception here probably means the JCE provider hasn't
			// been permanently installed on this system by listing it
			// in the $JAVA_HOME/jre/lib/security/java.security file.
			// Therefore, we have to install the JCE provider explicitly.
			System.err.println("Installing SunJCE provider.");
			Provider sunjce = new com.sun.crypto.provider.SunJCE();
			Security.addProvider(sunjce);
		}
		
		
		//CipherOutputStream lCipherOutputStream = new CipherOutputStream();
		
	

	}
}
