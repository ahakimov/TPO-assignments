package assignment1;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Random;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public final class MappedFile {
    private static final int SleepTime = 1000;
    private static final File FILE = new File("data.txt");
    private static final Random RANDOM = new Random();
    private static final int BUFFER_SIZE = 16;
    private static final int ITERATION_COUNT = 10;
    private static final int STOP = -1;
    private static final int CONTINUE = 1;
//    private final FileChannel file_;
    private final RandomAccessFile file_;
    private final MappedByteBuffer buffer_;

    public MappedFile(File file, OperationMode mode) throws ExceptionAS1 {
        try {
            if (mode == OperationMode.Write) {
                file.delete();
            } else {
                if (!file.exists() && !file.isFile() && !file.canRead()) {
                    throw new ExceptionAS1(
                            "file " + file + " doesn't exist or cannot be read");
                }
            }
            file_ = new RandomAccessFile(file, "rw");
            FileChannel channel = file_.getChannel();
            buffer_ = channel.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_SIZE);
//            file_ = FileChannel.open(Paths.get("data.txt"), READ, WRITE);
//            FileChannel channel = file_.
//            buffer_ = file_.map(FileChannel.MapMode.READ_WRITE,0, BUFFER_SIZE);
        } catch (Throwable e) {
            throw new ExceptionAS1(e);
        }
    }

    public void write(int iterationCount) {
        boolean runInfinitely = (iterationCount <= 0);
        int iterationsLeftCount = iterationCount;
        while (runInfinitely || iterationsLeftCount > 0) {
            buffer_.rewind();
            buffer_.getInt();
            int lastOperationMark = buffer_.getInt();
            if (lastOperationMark == OperationMode.Read.getMark()) {
                int val1 = RANDOM.nextInt();
                int val2 = RANDOM.nextInt();
                buffer_.rewind();
                buffer_.putInt(CONTINUE).putInt(OperationMode.Write.getMark()).putInt(val1).putInt(val2);
                if (!runInfinitely) {
                    iterationsLeftCount--;
                }
            }
            sleep();
        }
        buffer_.rewind();
        buffer_.putInt(STOP).putInt(OperationMode.Write.getMark());
    }

    public void write() {
        write(ITERATION_COUNT);
    }

    public void read() {
        main:
        while (true) {
            buffer_.rewind();
            int stopOrContinue = buffer_.getInt();
            int lastOperationMark = buffer_.getInt();
            if (stopOrContinue == STOP && lastOperationMark == OperationMode.Write.getMark()) {
                break main;
            } else {
                if (lastOperationMark == OperationMode.Write.getMark()) {
                    int val1 = buffer_.getInt();
                    int val2 = buffer_.getInt();
                    buffer_.rewind();
                    buffer_.putInt(CONTINUE).putInt(OperationMode.Read.getMark());
                    int sum = val1 + val2;
                    System.out.println("sum: " + sum);
                }
            }
            sleep();
        }
    }

    public static void main(String... args) {
        try {
            if (args.length < 1) {
                System.out.println("read or write should be specified as argument");
                return;
            }
            OperationMode mode = OperationMode.getMode(args[0]);
            if (mode != null) {
                MappedFile file = new MappedFile(FILE, mode);
                switch (mode) {
                    case Write:
                        file.write();
                        break;
                    case Read:
                        file.read();
                        break;
                    default:
                        System.out.println("invalid argument: " + args[0]);
                        return;
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(SleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
