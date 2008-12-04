package utils.io.filetransfer;

import java.io.Serializable;
import java.util.zip.CRC32;

public class Chunk implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String mFileName;
	public long mFileLength;

	public int mChunkIndex;
	public byte[] mArray;
	public int mChunkStart;
	public int mChunkLength;

	CRC32 mCRC32 = new CRC32();

	public long getCRC32()
	{
		mCRC32.reset();
		mCRC32.update(mArray, 0, mChunkLength);
		return mCRC32.getValue();
	}

	@Override
	public String toString()
	{
		return mFileName + " [idx=" + mChunkIndex + ", crc32=" + mCRC32.getValue() + "]";
	}



}
