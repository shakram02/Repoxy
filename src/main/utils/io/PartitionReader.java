package com.company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PartitionReader implements Iterator {
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
     * Returns the next partition of the stream, or the remaining bytes
     * if the stream doesn't have {@link PartitionReader#partitionLength}
     * number of bytes available
     *
     * @return An {@link Optional} of byte array if partitioning is valid
     * <p>
     * {@link Optional#EMPTY} If the stream is empty
     * @throws IOException If the stream is closed
     */
    public Optional<byte[]> getNextPartition() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream(this.partitionLength);

        int read = this.streamedRead(out, this.partitionLength);
        if (read == 0) {
            return Optional.empty();
        }

        return Optional.of(out.toByteArray());
    }

    /**
     * Returns a byte[] from the underlying stream.
     * <p>
     * If len is more than the available bytes in stream, the
     * available bytes will be read and returned
     *
     * @param len length of the bulk which should be less than the whole stream
     * @return {@link Optional} of byte[] if the bulk is available
     * <p>
     * {@link Optional#EMPTY} if nothing was read
     */
    public Optional<byte[]> getBulk(int len) throws IOException {
        ByteArrayOutputStream out =
                new ByteArrayOutputStream(Math.max(this.stream.available(), len));

        int read = this.streamedRead(out, len);
        if (read == 0) {
            return Optional.empty();
        }

        return Optional.of(out.toByteArray());
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
        byte[] buffer = new byte[bufferSize];

        int read = this.stream.read(buffer);

        if (read == -1) {
            this.stream.close();
            return 0;
        }

        out.write(buffer, 0, read);
        return read;
    }

    public Optional<List<byte[]>> getAllPartitions() throws IOException {
        ArrayList<byte[]> finalResult = new ArrayList<>();
        Optional<byte[]> result = getNextPartition();

        while (result.isPresent()) {
            finalResult.add(result.get());
        }

        if (finalResult.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(finalResult);
    }

    @Override
    public boolean hasNext() {
        try {
            return this.stream.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public byte[] next() {
        try {
            //noinspection ConstantConditions
            return this.getNextPartition().get();
        } catch (IOException e) {
            return null;
        }
    }

    public void reset() throws IOException {
        stream.reset();
    }
}
