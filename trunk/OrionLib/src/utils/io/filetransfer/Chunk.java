package utils.io.filetransfer;

import java.io.Serializable;

public class Chunk implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String mFileName;
	public long mFileLength;

	public int mChunkIndex;
	public byte[] mArray;
	public int mChunkStart;
	public int mChunkLength;
	public long mCRC32;
	
	@Override
	public String toString()
	{
		return mFileName+" [idx="+mChunkIndex+", crc32="+mCRC32+"]";
	}

}
