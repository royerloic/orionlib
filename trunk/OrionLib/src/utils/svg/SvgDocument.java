package utils.svg;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Loic Royer, Copyright (c) 2005, Some Rights Reserved.
 * 
 */
public class SvgDocument
{

	private SVGGraphics2D mSvgGenerator;

	public SvgDocument()
	{
		super();
		final DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		final String svgNS = "http://www.w3.org/2000/svg";
		final Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		mSvgGenerator = new SVGGraphics2D(document);
	}

	public Graphics2D getGraphics()
	{
		return mSvgGenerator;
	}

	public void writeToFile(File pFile) throws IOException
	{
		// UTF-8 encoding.
		final boolean useCSS = true; // we want to use CSS style attributes
		Writer out;
		out = new FileWriter(pFile);
		mSvgGenerator.stream(out, useCSS);
	}

	public void writeToStream(OutputStream pOutputStream) throws SVGGraphics2DIOException
	{
		// UTF-8 encoding.
		final boolean useCSS = true; // we want to use CSS style attributes
		Writer out;
		out = new OutputStreamWriter(pOutputStream);
		mSvgGenerator.stream(out, useCSS);
	}

}
