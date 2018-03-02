package ru.alex.st.messenger.utils;

public class ArrayUtils {

    public static byte[] getExcaptType( byte[] src ) {
        byte[] dst = new byte[ src.length - 1 ];
        System.arraycopy( src, 1, dst, 0, dst.length );
        return dst;
    }

}
