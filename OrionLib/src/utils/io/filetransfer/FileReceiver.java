package utils.io.filetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReceiver
{
	FileSender mFileSender;
	private final File mReceivingFolder;
	private File mReceivedFile;
	private RandomAccessFile mRandomAccessFile;

	private File mReceivedChunkFile;
	private RandomAccessFile mChunkReceivedArray;

	public FileReceiver(File pReceivingFolder, FileSender pFileSender) throws IOException
	{
		super();
		mReceivingFolder = pReceivingFolder;
		mFileSender = pFileSender;

		mReceivedFile = new File(mReceivingFolder, mFileSender.getFileName());
		mRandomAccessFile = new RandomAccessFile(mReceivedFile, "rw");

		mReceivedChunkFile = new File(mReceivingFolder,
																	mFileSender.getFileName() + ".transferinfo.tmp");
		mChunkReceivedArray = new RandomAccessFile(mReceivedChunkFile, "rw");
		if (mChunkReceivedArray.length() != mFileSender.getNumberOfChunks())
		{
			mChunkReceivedArray.seek(mFileSender.getNumberOfChunks() - 1);
			mChunkReceivedArray.write(0);
		}

	}

	public File getReceivedFile()
	{
		return mReceivedFile;
	}

	public int searchFirstMissingChunk() throws IOException
	{
		mChunkReceivedArray.seek(0);
		int lNumberOfChunks = mFileSender.getNumberOfChunks();
		int i = 0;
		for (; mChunkReceivedArray.read() > 0; i++)
			;

		return i == lNumberOfChunks ? -1 : i;
	}

	public boolean hasChunk(final int pChunkIndex) throws IOException
	{
		mChunkReceivedArray.seek(pChunkIndex);
		return mChunkReceivedArray.read() != 0;
	}

	public boolean getChunk(Chunk pChunk) throws IOException
	{
		int lChunkIndex = pChunk.mChunkIndex;
		long[] lCRC32Array = mFileSender.getCRC32Array();

		boolean correctFileName = mFileSender	.getFileName()
																					.equals(pChunk.mFileName);
		boolean correctFileSize = mFileSender.getFileLength() == pChunk.mFileLength;
		boolean correctCRC32 = lCRC32Array[lChunkIndex] == pChunk.getCRC32();
		
		if (correctFileName && correctFileSize && correctCRC32)
		{
			try
			{
				mRandomAccessFile.seek(pChunk.mChunkStart);
				mRandomAccessFile.write(pChunk.mArray, 0, pChunk.mChunkLength);
				mChunkReceivedArray.seek(lChunkIndex);
				mChunkReceivedArray.writeByte(1);
				return true;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		return false;
	}

	public void close() throws IOException
	{
		mRandomAccessFile.close();
		mChunkReceivedArray.close();
	}

	public boolean checkfile()
	{
		try
		{
			mChunkReceivedArray.seek(0);
			for (int i = 0; i < mFileSender.getNumberOfChunks(); i++)
			{
				if (mChunkReceivedArray.read() != 1)
				{
					return false;
				}
			}
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
