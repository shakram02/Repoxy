package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

public class Dumper<T> {
    private Function<T, byte[]> byteSerializer;
    private String logDirectory;

    public Dumper(Function<T, byte[]> byteSerializer) {
        this.byteSerializer = byteSerializer;
        this.logDirectory = getLogDirectoryPath().toString();
    }

    public void dump(T thing, String fileName) {
        try {
            Files.write(Paths.get(logDirectory, fileName), this.byteSerializer.apply(thing),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't write to file");
        }
    }

    private static Path getLogDirectoryPath() {
        // Current folder /log
        Path tempDir = Paths.get(Paths.get("").toAbsolutePath() + "/log");
        if (!Files.exists(tempDir)) {
            // TODO: monitor file sizes, delete oldest
            try {
                Files.createDirectory(tempDir);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Couldn't create log folder");
            }
        }

        return tempDir;
    }
}
