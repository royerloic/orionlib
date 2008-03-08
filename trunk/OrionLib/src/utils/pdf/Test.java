package utils.pdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.fdf.FDFDocument;

public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			URL lURL = new URL("http://www.genome.org/cgi/reprint/gr.7187808v1.pdf");
			URLConnection lURLConnection = lURL.openConnection();
			
			PDFParser lPDFParser = new PDFParser(lURLConnection.getInputStream());
			
			lPDFParser.parse();
			
			COSDocument lDocument = lPDFParser.getDocument();
			FDFDocument lDocument2 = lPDFParser.getFDFDocument();
			PDDocument lDocument3 = lPDFParser.getPDDocument();
			
			System.out.println(lDocument);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
