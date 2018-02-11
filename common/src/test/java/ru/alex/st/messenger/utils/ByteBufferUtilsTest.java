package ru.alex.st.messenger.utils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

public class ByteBufferUtilsTest {

    private static final int CAPACITY = 1024;

    @Test( dataProvider = "buffers" )
    public void testPrintByteBufferAndReturnSamePosMode( ByteBuffer src, int pos, int limit, int capacity ) {
        System.out.println( src );
        ByteBufferUtils.printByteBuffer( src );
        System.out.println( src );
        Assert.assertEquals( src.position(), pos );
        Assert.assertEquals( src.limit(), limit );
        Assert.assertEquals( src.capacity(), capacity );

    }

    @DataProvider( name = "buffers" )
    public Object[][] getBuffers() {
        //TODO fix middle variant
        ByteBuffer readModeBegin = ByteBuffer.allocateDirect( CAPACITY );
        ByteBuffer readModeMiddle = ByteBuffer.allocateDirect( CAPACITY );
        ByteBuffer readModeEnd = ByteBuffer.allocateDirect( CAPACITY );
        ByteBuffer writeModeBegin = ByteBuffer.allocateDirect( CAPACITY );
        ByteBuffer writeModeMiddle = ByteBuffer.allocateDirect( CAPACITY );
        ByteBuffer writeModeEnd = ByteBuffer.allocateDirect( CAPACITY );

        readModeBegin.flip();

        readModeMiddle.clear();
        readModeMiddle.put( getNewArray( CAPACITY, 3 ) );
        readModeMiddle.flip();

        readModeEnd.clear();
        readModeEnd.put( getNewArray( CAPACITY, CAPACITY ) );
        readModeEnd.flip();

        writeModeBegin.clear();

        writeModeMiddle.clear();
        writeModeMiddle.put( getNewArray( CAPACITY, CAPACITY / 2 ) );

        writeModeEnd.clear();
        writeModeEnd.put( getNewArray( CAPACITY, CAPACITY ) );


        return new Object[][]{
                { readModeBegin, 0, 0, CAPACITY },
                { readModeMiddle, 0, 3, CAPACITY },
                { readModeEnd, 0, CAPACITY, CAPACITY },
                { writeModeBegin, 0, CAPACITY, CAPACITY },
                { writeModeMiddle, CAPACITY / 2, CAPACITY, CAPACITY },
                { writeModeEnd, CAPACITY, CAPACITY, CAPACITY }
        };
    }

    private byte[] getNewArray( int capacity, int filledTill ) {
        byte[] array = new byte[ filledTill ];
        for ( int i = 0; i < filledTill; i++ ) {
            array[ i ] = ( byte ) i;
        }
        return array;
    }

}