package ru.alex.st.messenger.utils;

import java.nio.ByteBuffer;

public class ArrayUtils {

    public static byte[] getMessageBytes( byte[] src ) {
        byte[] dst = new byte[ src.length - 5 ];
        System.arraycopy( src, 5, dst, 0, dst.length );
        return dst;
    }

    public static long getLongFromBytes( byte[] src, int pos ) {
        ByteBuffer buffer = ByteBuffer.allocateDirect( 8 );
        buffer.put( src, pos, 8 );
        buffer.flip();
        return buffer.getLong();
    }

    public static int getIntFromBytes( byte[] src, int pos ) {
        ByteBuffer buffer = ByteBuffer.allocateDirect( 4 );
        buffer.put( src, pos, 4 );
        buffer.flip();
        return buffer.getInt();
    }

}
