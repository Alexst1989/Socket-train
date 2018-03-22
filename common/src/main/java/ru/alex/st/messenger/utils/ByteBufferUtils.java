package ru.alex.st.messenger.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

public class ByteBufferUtils {

    private static final Logger LOGGER = LogManager.getLogger( ByteBufferUtils.class );

    public static void printByteBuffer( ByteBuffer src ) {
        if ( src.position() > 0 || src.limit() > 0 ) {
            ByteBuffer duplicate = src.duplicate();
            if ( duplicate.limit() == duplicate.capacity() ) {
                //In write mode
                duplicate.flip();
            } else if ( duplicate.limit() < duplicate.capacity() && duplicate.position() > 0 ) {
                //In read mode but not from beginning
                duplicate.rewind();
            }
            byte[] byteArray = new byte[ duplicate.limit() ];
            duplicate.get( byteArray );
            StringBuilder sb = new StringBuilder();
            sb.append( "[" );
            int lastIndex = byteArray.length - 1;
            for ( int i = 0; i < byteArray.length; i++ ) {
                sb.append( byteArray[ i ] );
                if ( i != lastIndex ) {
                    sb.append( ", " );
                }
            }
            sb.append( "]" ).append( System.lineSeparator() );
            LOGGER.info( sb.toString() );
        }
    }

    public static byte[] getLongBytes( long value ) {
        ByteBuffer buffer = ByteBuffer.allocateDirect( 8 );
        buffer.putLong( value );
        byte[] result = new byte[8];
        buffer.flip();
        buffer.get( result );
        return result;
    }


}
