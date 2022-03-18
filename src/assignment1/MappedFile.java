package assignment1;

import java.io.File;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Random;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public final class MappedFile {
    private static final int SleepTime = 1000;
    private static final File FILE = new File("data.txt");
    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    private static final int BUFFER_SIZE = 3;
    private static final int ITERATION_COUNT = 10;
    private static final int STOP = -1;
    private static final int CONTINUE = 1;
    private final MappedByteBuffer buffer;

    public MappedFile(File file, OperationMode mode) throws Exception_as1 {
        try {
            if (mode == OperationMode.Write) file.delete();
            else {
                if (!file.exists() && !file.isFile() && !file.canRead()) {
                    throw new Exception_as1(
                            "file " + file + " no data");
                }
            }
            FileChannel channel = FileChannel.open(Paths.get("data.txt"), READ, WRITE);
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_SIZE);
        } catch (Throwable e) {
            throw new Exception_as1(e);
        }

    }

    public void write(int iterationCount) {
        boolean runInfinitely = (iterationCount <= 0);
        int iterationsLeftCount = iterationCount;
        while (runInfinitely || iterationsLeftCount > 0) {
            buffer.rewind();
            buffer.getInt();
            int lastOperationMark = buffer.getInt();
            if (lastOperationMark == OperationMode.Read.getMark()) {
                int num1 = RANDOM.nextInt();
                int num2 = RANDOM.nextInt();
                buffer.rewind();
                buffer.putInt(CONTINUE).putInt(OperationMode.Write.getMark()).putInt(num1).putInt(num2);
                if (!runInfinitely) {
                    iterationsLeftCount--;
                }
            }
            sleep();
        }
        buffer.rewind();
        buffer.putInt(STOP).putInt(OperationMode.Write.getMark());
    }

    public void write() {
        write(ITERATION_COUNT);
    }

    public void read() {

        while (true) {
            buffer.rewind();
            int stopOrContinue = buffer.getInt();
            int lastOperationMark = buffer.getInt();
            if (stopOrContinue == STOP && lastOperationMark == OperationMode.Write.getMark()) {
                break;
            } else {
                if (lastOperationMark == OperationMode.Write.getMark()) {
                    int num1 = buffer.getInt();
                    int num2 = buffer.getInt();
                    buffer.rewind();
                    buffer.putInt(CONTINUE).putInt(OperationMode.Read.getMark());
                    int sum = num1 + num2;
                    System.out.println("Sum: " + sum);
                }
            }
            sleep();
        }
    }

    public static void main(String... args) {
        try {
            if (args.length < 1) {
                System.out.println("read or write should be clarified as argument");
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