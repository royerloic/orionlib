package utils.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class Graphics2DTextWriter
{

	private static final HashMap<Graphics2DTextWriter, SoftReference<Image>> lWriterToImageMap = new HashMap<Graphics2DTextWriter, SoftReference<Image>>();

	public static boolean mCacheActivated = false;

	private boolean mMultiLine;

	public boolean mAntialiasing = true;

	public boolean mShadow = true;

	private String mString;

	private Font mFont = new Font("courrier", Font.PLAIN, 16);

	private Color mTextColor = Color.BLACK;

	private Color mBackgroundColor = null;

	private double mWrappingWidth = 100;

	private int mMinNumberOfLines = 1;

	private int mMaxNumberOfLines = 10;

	private Image mImage;

	@Override
	public boolean equals(final Object pObj)
	{
		final Graphics2DTextWriter lWriter1 = this;
		final Graphics2DTextWriter lWriter2 = (Graphics2DTextWriter) pObj;

		boolean lEquals = false;

		lEquals = true;
		lEquals &= lWriter1.mMultiLine == lWriter2.mMultiLine;
		lEquals &= lWriter1.mAntialiasing == lWriter2.mAntialiasing;
		lEquals &= lWriter1.mShadow == lWriter2.mShadow;
		lEquals &= lWriter1.mString.equals(lWriter2.mString);
		lEquals &= lWriter1.mFont.equals(lWriter2.mFont);
		lEquals &= lWriter1.mTextColor.equals(lWriter2.mTextColor);
		if (lWriter1.mBackgroundColor != null)
			lEquals &= lWriter1.mBackgroundColor.equals(lWriter2.mBackgroundColor);
		lEquals &= lWriter1.mWrappingWidth == lWriter2.mWrappingWidth;
		lEquals &= lWriter1.mMinNumberOfLines == lWriter2.mMinNumberOfLines;
		lEquals &= lWriter1.mMaxNumberOfLines == lWriter2.mMaxNumberOfLines;

		return lEquals;

	}

	@Override
	public int hashCode()
	{
		int lHashCode = 1;
		lHashCode += mMultiLine ? 1876541 : 1087632;
		lHashCode += mAntialiasing ? 2973430 : 3946279;
		lHashCode += mShadow ? 208764986 : 37840812;
		lHashCode += mString.hashCode();
		lHashCode += mFont.hashCode();
		lHashCode += mTextColor.hashCode();
		lHashCode = (lHashCode += 137951) * 137951;
		lHashCode += mBackgroundColor == null ? 0 : mBackgroundColor.hashCode();
		lHashCode = (lHashCode += 137951) * 137951;
		lHashCode += mWrappingWidth;
		lHashCode += mMinNumberOfLines;
		lHashCode += mMaxNumberOfLines;
		return lHashCode;
	}

	public Graphics2DTextWriter(final String pString)
	{
		super();
		mMultiLine = false;
		mString = pString;
	}

	public Graphics2DTextWriter(final String pString, final Font pFont)
	{
		this(pString);
		mFont = pFont;
	}

	public Graphics2DTextWriter(final String pString,
															final Font pFont,
															final Color pTextColor)
	{
		this(pString, pFont);
		mTextColor = pTextColor;
	}

	public Graphics2DTextWriter(final String pString,
															final Font pFont,
															final Color pTextColor,
															final Color pBackgroundColor)
	{
		this(pString, pFont, pTextColor);
		mBackgroundColor = pBackgroundColor;
	}

	public Graphics2DTextWriter(final String pString,
															final Font pFont,
															final Color pTextColor,
															final Color pBackgroundColor,
															final double pWrappingWidth)
	{
		this(pString, pFont, pTextColor, pBackgroundColor);
		mWrappingWidth = pWrappingWidth;
		mMultiLine = true;
	}

	public Graphics2DTextWriter(final String pString,
															final Font pFont,
															final Color pTextColor,
															final Color pBackgroundColor,
															final double pWrappingWidth,
															final int pMinNumberOfLines,
															final int pMaxNumberOfLines)
	{
		this(pString, pFont, pTextColor, pBackgroundColor, pWrappingWidth);
		mMinNumberOfLines = pMinNumberOfLines;
		mMaxNumberOfLines = pMaxNumberOfLines;
		mMultiLine = true;
	}

	public Image getImage(final BufferedImage pRecycledImage)
	{
		mImage = null;
		final SoftReference<Image> lImageSoftReference = lWriterToImageMap.get(this);
		if (lImageSoftReference != null)
		{
			mImage = lImageSoftReference.get();
			if (mImage != null)
				return mImage;
		}
		displayAvailableVideoMemory();
		mImage = generateImage(pRecycledImage);
		if (mCacheActivated)
		{
			lWriterToImageMap.put(this, new SoftReference<Image>(mImage));
			System.out.println("Graphics2DTextWriter> cached image for text:  '" + mString
													+ "'");

		}
		displayAvailableVideoMemory();

		return mImage;
	}

	public Image getImage()
	{
		return getImage(null);
	}

	private Image generateImage(final BufferedImage pRecycledImage)
	{
		Image lImage = null;
		if (mMultiLine)
			lImage = createMultiLineTextImage(mString,
																				mFont,
																				mTextColor,
																				mBackgroundColor,
																				mWrappingWidth,
																				mMinNumberOfLines,
																				mMaxNumberOfLines,
																				mAntialiasing,
																				mShadow);
		else
			lImage = createTextImage(	pRecycledImage,
																mString,
																mFont,
																mTextColor,
																mBackgroundColor,
																mAntialiasing,
																mShadow);
		return lImage;
	}

	public void flushFromCache()
	{
		displayAvailableVideoMemory();
		if (mImage != null)
		{
			mImage.flush();
			System.out.println("flushed image: " + mImage);
		}
		lWriterToImageMap.remove(this);
		mImage = null;
		displayAvailableVideoMemory();
	}

	private static void displayAvailableVideoMemory()
	{
		System.out.println("displayAvailableVideoMemory()=" + getAvailableVideoMemory()
												/ 1024
												+ "K");
	}

	public static int getAvailableVideoMemory()
	{
		final GraphicsEnvironment lGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice lGraphicsDevice = lGraphicsEnvironment.getDefaultScreenDevice();
		int lTotalMemory = 0;
		for (final GraphicsDevice lDevice : lGraphicsEnvironment.getScreenDevices())
			lTotalMemory += lDevice.getAvailableAcceleratedMemory();
		return lTotalMemory;
	}

	public static BufferedImage createTextImage(final BufferedImage pImage,
																							final String pText,
																							final Font pFont,
																							final Color pColor,
																							final Color pBackgroundColor,
																							final boolean pAntialiasing,
																							final boolean pShadow)
	{
		final boolean isAntiAliased = pAntialiasing;
		final boolean usesFractionalMetrics = true;
		final FontRenderContext lFontRendererContext = new FontRenderContext(	null,
																																					isAntiAliased,
																																					usesFractionalMetrics);
		final TextLayout lTextLayout = new TextLayout(pText,
																									pFont,
																									lFontRendererContext);
		final Rectangle2D lTextBounds = lTextLayout.getBounds();
		final double lTopMargin = lTextLayout.getDescent();
		final double lLeftMargin = lTopMargin;
		final int lTextWidth = (int) (Math.ceil(lTextBounds.getWidth()) + 2 * lLeftMargin);
		final int lTextHeight = (int) (Math.ceil(lTextBounds.getHeight()) + 2 * lTopMargin);

		BufferedImage image = pImage;
		if ((pImage == null) || (!((pImage.getHeight() >= lTextHeight) && (pImage.getWidth() >= lTextWidth))))
		{
			image = new BufferedImage(lTextWidth,
																lTextHeight,
																BufferedImage.TYPE_INT_ARGB);
			System.out.println("Graphics2DTextWriter> created image for text: " + pText);
		}

		final Graphics2D lGraphics = image.createGraphics();
		lGraphics.setBackground(new Color(0, 0, 0, 0));
		lGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());
		if (pBackgroundColor != null)
		{
			lGraphics.setBackground(pBackgroundColor);
			lGraphics.clearRect(0, 0, lTextWidth, lTextHeight);
		}

		lGraphics.setFont(pFont);
		final Object antiAliased = isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
																						: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		lGraphics.setRenderingHint(	RenderingHints.KEY_TEXT_ANTIALIASING,
																antiAliased);
		final Object fractionalMetrics = usesFractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
																													: RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
		lGraphics.setRenderingHint(	RenderingHints.KEY_FRACTIONALMETRICS,
																fractionalMetrics);
		final float lTextX = (float) (-lTextBounds.getX() + lLeftMargin);
		final float lTextY = (float) (-lTextBounds.getY() + lTopMargin);
		if (pShadow)
		{
			final Color lShadowColor = new Color(	pColor.getRed() / 8,
																						pColor.getGreen() / 8,
																						pColor.getBlue() / 8);
			lGraphics.setColor(lShadowColor);
			lGraphics.drawString(pText, lTextX + 1, lTextY + 1);
		}
		lGraphics.setColor(pColor);
		lGraphics.drawString(pText, lTextX, lTextY);
		lGraphics.dispose();
		return image;
	}

	public static BufferedImage createMultiLineTextImage(	final String pText,
																												final Font pFont,
																												final Color pTextColor,
																												final Color pBackgroundColor,
																												double pWrappingWidth,
																												final int pMinNumberOfLines,
																												final int pMaxNumberOfLines,
																												final boolean pAntialiasing,
																												final boolean pShadow)
	{
		final boolean isAntiAliased = true;
		final boolean usesFractionalMetrics = pAntialiasing;

		final FontRenderContext lFontRenderContext = new FontRenderContext(	null,
																																				isAntiAliased,
																																				usesFractionalMetrics);
		final Map<TextAttribute, Font> lAttribute = new HashMap<TextAttribute, Font>();
		lAttribute.put(TextAttribute.FONT, pFont);
		final AttributedString lAttributedString = new AttributedString(pText,
																																		lAttribute);
		AttributedCharacterIterator lAttributedCharacterIterator = lAttributedString.getIterator();
		LineBreakMeasurer lLineBreakMeasurer = new LineBreakMeasurer(	lAttributedCharacterIterator,
																																	lFontRenderContext);

		pWrappingWidth -= 2 * pFont.getSize();

		double w = pWrappingWidth;
		double h = 0;

		TextLayout lTextLayout = null;
		int lLineCounter = 1;

		while (lLineCounter <= pMaxNumberOfLines)
		{
			final TextLayout lNewTextLayout = lLineBreakMeasurer.nextLayout((float) pWrappingWidth);
			if (lNewTextLayout != null)
				lTextLayout = lNewTextLayout;
			if ((lNewTextLayout == null) && (lLineCounter > pMinNumberOfLines))
				break;

			lLineCounter++;
			h += lTextLayout.getAscent() + lTextLayout.getDescent()
						+ lTextLayout.getLeading();

		}
		h += lTextLayout.getDescent() + lTextLayout.getLeading();

		w += 2 * pFont.getSize(); // margin
		h += 2 * pFont.getSize();

		final BufferedImage image = new BufferedImage((int) w,
																									(int) h,
																									BufferedImage.TYPE_INT_ARGB);
		final Graphics2D lImageGraphics = image.createGraphics();
		if (pBackgroundColor != null)
		{
			lImageGraphics.setBackground(pBackgroundColor);
			lImageGraphics.clearRect(0, 0, (int) w, (int) h);
		}

		lImageGraphics.setFont(pFont);
		final Object antiAliased = isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
																						: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		lImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
																		antiAliased);
		final Object fractionalMetrics = usesFractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
																													: RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
		lImageGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
																		fractionalMetrics);

		lAttributedCharacterIterator = lAttributedString.getIterator();
		lLineBreakMeasurer = new LineBreakMeasurer(	lAttributedCharacterIterator,
																								lFontRenderContext);

		final double pX = pFont.getSize();
		double pY = pFont.getSize();
		lLineCounter = 1;
		while (lLineCounter <= pMaxNumberOfLines)
		{
			lTextLayout = lLineBreakMeasurer.nextLayout((float) pWrappingWidth);
			if (lTextLayout == null)
				break;
			// Rectangle2D bounds = lTextLayout.getBounds();
			pY += lTextLayout.getAscent() + lTextLayout.getDescent()
						+ lTextLayout.getLeading();

			if (pShadow)
			{
				final Color lShadowColor = new Color(	pTextColor.getRed() / 8,
																							pTextColor.getGreen() / 8,
																							pTextColor.getBlue() / 8);
				lImageGraphics.setColor(lShadowColor);
				lTextLayout.draw(lImageGraphics, (float) (pX + 1), (float) (pY + 1));
			}
			lImageGraphics.setColor(pTextColor);
			lTextLayout.draw(lImageGraphics, (float) (pX), (float) pY);

			// lImageGraphics.drawRect((int) pX, (int) pY - (int) bounds.getHeight(),
			// (int) bounds.getWidth(), (int) bounds.getHeight());
			// System.out.println(bounds);
			lLineCounter++;
		}

		lImageGraphics.dispose();
		return image;
	}

}
