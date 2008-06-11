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
	public static void main(final String[] args)
	{
		try
		{
			final URL lURL = new URL("http://www.genome.org/cgi/reprint/gr.7187808v1.pdf");
			final URLConnection lURLConnection = lURL.openConnection();

			final PDFParser lPDFParser = new PDFParser(lURLConnection.getInputStream());

			lPDFParser.parse();

			final COSDocument lDocument = lPDFParser.getDocument();
			final FDFDocument lDocument2 = lPDFParser.getFDFDocument();
			final PDDocument lDocument3 = lPDFParser.getPDDocument();

			System.out.println(lDocument);
		}
		catch (final MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
