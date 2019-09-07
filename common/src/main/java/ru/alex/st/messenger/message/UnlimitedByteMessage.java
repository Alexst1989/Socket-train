package ru.alex.st.messenger.message;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class UnlimitedByteMessage implements Message {

    private DataHolder dataHolder;

    public UnlimitedByteMessage() {

    }

    @Override
    public byte[] getBytes() {
        return new byte[ 0 ];
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public byte[] getMessageBytes() {
        return new byte[ 0 ];
    }

    //Integer max value 2 147 483 647 ~ 2 Gb
    public class DataHolder {


        LinkedList<ByteBuffer> data = new LinkedList<>();


    }

    public static void main( String args[] ) {
        System.out.println( Integer.MAX_VALUE );
    }


}
