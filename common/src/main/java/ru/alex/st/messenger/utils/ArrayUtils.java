package ru.alex.st.messenger.utils;

import java.nio.ByteBuffer;

public class ArrayUtils {

    public static byte[] getExcaptType( byte[] src ) {
        byte[] dst = new byte[ src.length - 1 ];
        System.arraycopy( src, 1, dst, 0, dst.length );
        return dst;
    }

    public static long getLongFromBytes( byte[] src, int pos ) {
        ByteBuffer buffer = ByteBuffer.allocateDirect( 8 );
        buffer.put( src, pos, 8 );
        buffer.flip();
        return buffer.getLong();
    }

}
