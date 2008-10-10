package utils.io.filetransfer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.zip.CRC32;

public class FileSender implements Serializable
{

	final private int mChunkSize = 1024 * 1024;

	private File mFile;
	private int mNumberOfChunks = -1;
	private long[] mCRC32Array = null;

	private transient RandomAccessFile mRandomAccessFile;
	final transient private byte[] array = new byte[mChunkSize];

	public FileSender(File file) throws IOException
	{
		super();
		mFile = file;
		mRandomAccessFile = new RandomAccessFile(mFile, "rw");
		mNumberOfChunks = getNumberOfChunks();
		mCRC32Array = getCRC32Array();
	}

	public String getFileName()
	{
		return mFile.getName();
	}

	public long getFileLength()
	{
		return mFile.length();
	}

	public int getNumberOfChunks() throws IOException
	{
		if (mNumberOfChunks < 0)
		{
			long lLength = mRandomAccessFile.length();
			mNumberOfChunks = (int) Math.ceil(((double) lLength) / mChunkSize);
		}
		return mNumberOfChunks;
	}

	public long[] getCRC32Array() throws IOException
	{
		if (mCRC32Array == null)
		{
			mCRC32Array = new long[mNumberOfChunks];
			for (int i = 0; i < mNumberOfChunks; i++)
			{
				int length = mRandomAccessFile.read(array);
				CRC32 lCRC32 = new CRC32();
				lCRC32.update(array, 0, length);
				mCRC32Array[i] = lCRC32.getValue();
			}
		}
		return mCRC32Array;
	}

	public Chunk sendChunk(int pChunkIndex) throws IOException
	{
		Chunk lChunk = new Chunk();

		lChunk.mFileName = getFileName();
		lChunk.mFileLength = mFile.length();

		lChunk.mChunkIndex = pChunkIndex;
		lChunk.mChunkStart = pChunkIndex * mChunkSize;

		mRandomAccessFile.seek(lChunk.mChunkStart);
		lChunk.mChunkLength = mRandomAccessFile.read(array);
		lChunk.mArray = array;
		CRC32 lCRC32 = new CRC32();
		lCRC32.update(array, 0, lChunk.mChunkLength);
		lChunk.mCRC32 = lCRC32.getValue();

		return lChunk;
	}

	public void close() throws IOException
	{
		mRandomAccessFile.close();
	}

}
