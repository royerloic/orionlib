package utils.io.filetransfer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

import org.junit.Test;

import utils.io.FileToString;
import utils.io.StreamToFile;
import utils.io.filetransfer.Chunk;
import utils.io.filetransfer.FileReceiver;
import utils.io.filetransfer.FileSender;
import utils.io.serialization.SerializationUtils;
import utils.network.groovyserver.GroovyClient;
import utils.network.groovyserver.GroovyServer;
import utils.utils.Arrays;

public class FileTransferTest
{

	@Test
	public void test()
	{
		File lOriginalFile;
		try
		{
			lOriginalFile = File.createTempFile("FileTransferTest", "lOriginalFile");

			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < 250000; i++)
				buffer.append("helloworld");
			FileToString.write(buffer.toString(), lOriginalFile);

			FileSender lFileSender = new FileSender(lOriginalFile);

			// System.out.println(lFileSender.getFileName());
			// System.out.println(lFileSender.getFileLength());
			// System.out.println(lFileSender.getNumberOfChunks());
			assertEquals(3, lFileSender.getNumberOfChunks());
			// System.out.println(Arrays.toString(lFileSender.getCRC32Array()));

			Chunk lChunk = lFileSender.sendChunk(0);
			// System.out.println(lChunk);
			assertTrue(lChunk.mChunkIndex == 0);

			File lFolder = File.createTempFile("FileTransferTest", "lFolder");
			lFolder.delete();
			lFolder.mkdir();
			FileReceiver lFileReceiver = new FileReceiver(lFolder, lFileSender);

			assertEquals(0, lFileReceiver.searchFirstMissingChunk());

			int i = 0;
			while ((i = lFileReceiver.searchFirstMissingChunk()) >= 0)
			{
				lFileReceiver.getChunk(lFileSender.sendChunk(i));
			}

			String lStringRead = FileToString.read(lFileReceiver.getReceivedFile());
			assertTrue(lStringRead.startsWith(buffer.toString()));
			// System.out.println(lFileReceiver.getReceivedFile().getPath());

			assertTrue(lFileReceiver.checkfile());

			lFileSender.close();
			lFileReceiver.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
