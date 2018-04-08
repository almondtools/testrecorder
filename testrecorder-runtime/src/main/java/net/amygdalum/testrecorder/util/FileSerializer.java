package net.amygdalum.testrecorder.util;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class FileSerializer {

    private FileSerializer() {
    }

    public static String store(String dir, Object object) throws IOException {
        try {
            byte[] data = serialize(object);
            String string = digest(data);
            Path path = Paths.get(dir, string + ".serialized");
            Files.createDirectories(path.getParent());
            try (OutputStream o = Files.newOutputStream(path, SYNC, CREATE, WRITE)) {
                o.write(data);
                o.flush();

                return path.getFileName().toString();
            }
        } catch (NullPointerException | IOException e) {
            throw new IOException("failed writing object to file", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("failed hashing data to produce unique file name", e);
        }
    }

    private static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream o = new ObjectOutputStream(out)) {
            o.writeObject(object);

            return out.toByteArray();
        }
    }

    private static String digest(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] mdbytes = md.digest(data);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            buffer.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return buffer.toString();
    }

    public static <T> T load(String dir, String fileName, Class<T> type) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(dir, fileName), READ))) {
            Object rawObject = in.readObject();
            T object = type.cast(rawObject);
            return object;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

}
