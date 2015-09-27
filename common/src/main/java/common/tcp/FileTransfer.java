package common.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Class for transferring files between client & server
 * 
 * @author Juhandré Knoetze
 */
public class FileTransfer {
    private static final int BUFFER_SIZE = 1024;
    private static final int LONG_BUFFER_SIZE = Long.SIZE / Byte.SIZE;

    public void send(Path path, OutputStream outputStream) throws IOException {
        final long size = Files.size(path);
        byte[] bytes = ByteBuffer.allocate(LONG_BUFFER_SIZE).putLong(size).array();
        outputStream.write(bytes);
        outputStream.flush();

        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            copy(inputStream, outputStream, -1);
        }
    }

    public void receive(Path path, InputStream inputStream) throws UnsupportedEncodingException, IOException, ClassNotFoundException {
        byte[] buffer = new byte[LONG_BUFFER_SIZE];
        inputStream.read(buffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length).put(buffer);
        byteBuffer.flip();

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            copy(inputStream, outputStream, byteBuffer.getLong());
        }
    }

    private static void copy(InputStream inputStream, OutputStream outputStream, long size) throws IOException {
        byte[] byteArray = new byte[BUFFER_SIZE];
        for (int count = inputStream.read(byteArray), total = 0; count > -1; count = inputStream.read(byteArray)) {
            outputStream.write(byteArray, 0, count);
            if (size >= 0 && (total += count) >= size) {
                break;
            }
        }
        outputStream.flush();
    }
}
