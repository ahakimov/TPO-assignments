package assignment1a;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedFile {

    private static String bigTextFile = "data.txt";

    public static void main(String[] args) throws Exception
    {
        // Create file object
        File file = new File(bigTextFile);

        //Delete the file; we will create a new file
        file.delete();

        int rand = (int)(Math.random());
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw"))
        {
            // Get file channel in read-write mode
            FileChannel fileChannel = randomAccessFile.getChannel();

            // Get direct byte buffer access using channel.map() operation
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096 * 8 * 8);

            //Write the content using put methods
            buffer.put(ByteBuffer.allocateDirect((int) rand));
        }
    }
}
