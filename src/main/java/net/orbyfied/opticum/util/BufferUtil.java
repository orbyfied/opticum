package net.orbyfied.opticum.util;

import java.nio.ByteBuffer;

public class BufferUtil {

    /**
     * Will read a string following the null-terminated format,
     * also often called a C-string. Will exit with the buffer
     * positioned after the null character or at the end of the
     * buffer.
     * @param buffer The buffer to read from.
     * @return The read string.
     */
    public static String readCString(ByteBuffer buffer) {
        StringBuilder b = new StringBuilder();
        char c = 0xFFFF; // 0xFFFF means nothing read
        while ((buffer.position() < buffer.capacity()) &&
                (c = (char)buffer.get()) != 0) {
            b.append(c);
        }

        if (c == 0)
            buffer.get(); // skip one forward

        // return
        return b.toString();
    }

}
