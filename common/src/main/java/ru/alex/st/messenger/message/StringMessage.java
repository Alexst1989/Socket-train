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
        this.message = new String( ArrayUtils.getMessageBytes( bytes ) );
        calcBytes();
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
        if ( message == null || message.isEmpty() ) {
            return new byte[ 0 ];
        } else {
            if ( bytes != null && bytes.length > 0 ) {
                return bytes;
            } else {
                calcBytes();
                return bytes;
            }
        }
    }

    private void calcBytes() {
        if ( this.message != null ) {
            this.bytes = new byte[ this.message.length() + 5 ]; //1 byte - type, 4 int length
            bytes[ 0 ] = type.getCode();
            byte[] sizeBytes = ByteBufferUtils.getIntBytes( this.message.length() );
            System.arraycopy( sizeBytes, 0, this.bytes, 1, sizeBytes.length );
            System.arraycopy( this.message.getBytes(), 0, this.bytes, 5, this.message.getBytes().length );
        }
    }

    @Override
    public long length() {
        return this.bytes.length;
    }


}
