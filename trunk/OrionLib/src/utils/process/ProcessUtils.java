package utils.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtils
{
	public static final StringBuilder readProcessOutput(Process pProcess) throws IOException
	{
		InputStream istr = pProcess.getInputStream(); 
		BufferedReader br = new BufferedReader(new InputStreamReader(istr)); 
		String str; // Temporary String variable
		StringBuilder lStringBuilder = new StringBuilder();
		while ((str = br.readLine()) != null)
		{
			lStringBuilder.append(str);
			lStringBuilder.append("\n");
		}
		return lStringBuilder;
	}
	
}
