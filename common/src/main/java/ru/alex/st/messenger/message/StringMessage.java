package ru.alex.st.messenger.message;

import ru.alex.st.messenger.utils.ArrayUtils;
import ru.alex.st.messenger.utils.ByteBufferUtils;

public class StringMessage implements Message {

    private static final MessageType type = MessageType.STRING;

    private String message;

    private byte[] bytes = new byte[ 0 ];

    public StringMessage( String message ) {
        this.message = message;
        calcBytes();
    }

    public StringMessage( byte[] bytes ) {
        checkMessageType( bytes[ 0 ] );
        this.message = new String( ArrayUtils.getExcaptType( bytes ) );
    }

    public String getMessage() {
        return message;
    }

    public StringMessage setMessage( String message ) {
        this.message = message;
        calcBytes();
        return this;
    }

    public StringMessage setMessage( byte[] bytes ) {
        this.message = new String( bytes );
        calcBytes();
        return this;
    }


    @Override
    public MessageType getMessageType() {
        return type;
    }

    @Override
    public byte[] getMessageBytes() {
        return getMessage().getBytes();
    }

    @Override
    public byte[] getBytes() {
//        byte[] messageBytes = message.getBytes();
//        byte[] result = new byte[ messageBytes.length + 1 ];
//        result[ 0 ] = type.getCode();
//        System.arraycopy( messageBytes, 0, result, 1, messageBytes.length );
//        if ( message != null ) {
//            return result;
//        } else {
//            return new byte[]{ type.getCode() };
//        }
        if ( bytes != null && bytes.length > 0 ) {
            return bytes;
        } else {
            calcBytes();
            return bytes;
        }
    }

    private void calcBytes() {
        if ( this.message != null ) {
            this.bytes = new byte[ this.message.length() + 9 ]; //1 byte - type, 8 long length
            bytes[ 0 ] = type.getCode();
            byte[] sizeBytes = ByteBufferUtils.getLongBytes( this.message.length() );
            System.arraycopy( sizeBytes, 0, this.bytes, 1, sizeBytes.length );
            System.arraycopy( this.message.getBytes(), 0, this.bytes, 9, this.message.getBytes().length );
        }
    }

    @Override
    public long length() {
        return this.bytes.length;
    }


}
