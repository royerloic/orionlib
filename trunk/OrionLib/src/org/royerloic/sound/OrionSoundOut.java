package org.royerloic.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class OrionSoundOut
{

	AudioFormat			audioFormat;

	SourceDataLine	sourceDataLine;

	public OrionSoundOut()
	{
		super();
	}

	public void start()
	{
		try
		{
			this.audioFormat = getAudioFormat();
			/*************************************************************************
			 * DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
			 * audioFormat); sourceDataLine = (SourceDataLine)
			 * AudioSystem.getLine(dataLineInfo);/
			 ************************************************************************/

			this.sourceDataLine = AudioSystem.getSourceDataLine(getAudioFormat());

			this.sourceDataLine.open(this.audioFormat);
			this.sourceDataLine.start();

		}
		catch (final Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
			System.exit(0);
		}
	}

	public void stop()
	{
		try
		{
			this.sourceDataLine.flush();
			this.sourceDataLine.stop();
			this.sourceDataLine.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
			System.exit(0);
		}
	}

	public void play(final byte[] pBuffer, final int pLength)
	{
		int lLength;
		if (pLength > pBuffer.length)
			lLength = pBuffer.length;
		else
			lLength = pLength;
		this.sourceDataLine.write(pBuffer, 0, lLength);
	}

	public static byte[] intArrayToByte(final int[] pIntArray, final byte[] pByteArray)
	{
		if (2 * pIntArray.length > pByteArray.length)
			return null;
		for (int i = 0; i < pIntArray.length; ++i)
		{
			pByteArray[2 * i] = (byte) ((pIntArray[i]) % 0xFF);
			pByteArray[2 * i + 1] = (byte) ((pIntArray[i] >> 8) % 0xFF);
		}

		return pByteArray;
	}

	public AudioFormat getAudioFormat()
	{
		final float sampleRate = 44100.0F;
		// 8000,11025,16000,22050,44100
		final int sampleSizeInBits = 16;
		// 8,16
		final int channels = 1;
		// 1,2
		final boolean signed = true;
		// true,false
		final boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}