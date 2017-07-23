package utils.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a given open non-empty input stream into partitions of given length
 * until the stream exhausted and not reset
 */
public class PartitionReader implements Closeable {
    private final int partitionLength;
    private final InputStream stream;

    /**
     * Partitions a stream into chunks of given length
     *
     * @param partitionLength length of partition
     * @param stream          open stream
     */
    public PartitionReader(int partitionLength, InputStream stream) {
        if (partitionLength <= 0) {
            throw new IllegalArgumentException("Partition length must be positive");
        }

        this.partitionLength = partitionLength;
        this.stream = stream;
    }

    /**
     * Gets the next partition of the stream
     *
     * @return Returns the next partition of the stream, or the remaining bytes
     * if the stream has less than {@link PartitionReader#partitionLength}
     * number of bytes available
     * @throws IOException If the stream is closed or empty
     */
    public byte[] getNextPartition() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream(this.partitionLength);
        this.streamedRead(out, this.partitionLength);

        return out.toByteArray();
    }

    /**
     * Reads a bulk of bytes from the underlying non-empty stream
     *
     * @param len positive length of the bulk which should be less than the whole stream
     * @return Returns a maximal munch up to len of the underlying stream
     */
    public byte[] getBulk(int len) throws IOException {
        assert len > 0;

        ByteArrayOutputStream out =
                new ByteArrayOutputStream(Math.min(this.stream.available(), len));

        int read = this.streamedRead(out, len);
        if (read == 0) {
            throw new IOException("Stream is empty");
        }

        return out.toByteArray();
    }

    /**
     * Reads bufferSize bytes of the underlying
     * stream, writes them to the given output stream
     * and returns the actual count of the read bytes
     *
     * @param out        OutputStream open for writing
     * @param bufferSize maximum number of desired bytes
     * @return number of bytes actually read
     * @throws IOException stream is closed
     */
    private int streamedRead(ByteArrayOutputStream out, int bufferSize) throws IOException {
        assert this.stream.available() > 0;

        byte[] buffer = new byte[bufferSize];

        int read = this.stream.read(buffer);

        if (read == -1) {
            return 0;
        }

        out.write(buffer, 0, read);
        return read;
    }

    public List<byte[]> getAllPartitions() throws IOException {
        ArrayList<byte[]> finalResult = new ArrayList<>();

        // Using Do while to throw if the stream was empty
        do {
            byte[] result = getNextPartition();
            finalResult.add(result);
        }
        while (this.hasAny());

        return finalResult;
    }


    /**
     * Queries the underlying stream for one available partition
     *
     * @return whether the underlying stream has at least one partition
     */
    public boolean hasPartition() {
        try {
            return this.stream.available() > this.partitionLength;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Queries the underlying stream for Any available bytes ( stream > 0 )
     *
     * @return whether the underlying stream has any remaining bytes
     */
    public boolean hasAny() {
        try {
            return this.stream.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public void reset() throws IOException {
        stream.reset();
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
