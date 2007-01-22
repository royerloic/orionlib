package org.royerloic.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class OrionSoundIn
{

	AudioFormat			audioFormat;

	TargetDataLine	targetDataLine;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public OrionSoundIn()
	{
		super();
	}

	public void start()
	{
		try
		{
			audioFormat = getAudioFormat();
			/*************************************************************************
			 * DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,
			 * audioFormat); targetDataLine = (TargetDataLine)
			 * AudioSystem.getLine(dataLineInfo);/
			 ************************************************************************/

			targetDataLine = (TargetDataLine) AudioSystem.getTargetDataLine(getAudioFormat());
			targetDataLine.open(audioFormat);
			targetDataLine.start();

		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}

	public void stop()
	{
		try
		{
			targetDataLine.flush();
			targetDataLine.stop();
			targetDataLine.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}

	public int record(final byte[] pBuffer)
	{
		return targetDataLine.read(pBuffer, 0, pBuffer.length);
	}

	private AudioFormat getAudioFormat()
	{
		float sampleRate = 44100.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}