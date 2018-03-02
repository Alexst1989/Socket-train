package ru.alex.st.messenger.message;

import ru.alex.st.messenger.utils.ArrayUtils;

public class StringMessage implements Message {

    private static final MessageType type = MessageType.STRING;

    private String message;

    public StringMessage( String message ) {
        this.message = message;
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
        return this;
    }

    public StringMessage setMessage( byte[] bytes ) {
        this.message = new String( bytes );
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
        byte[] messageBytes = message.getBytes();
        byte[] result = new byte[ messageBytes.length + 1 ];
        result[ 0 ] = type.getCode();
        System.arraycopy( messageBytes, 0, result, 1, messageBytes.length );
        if ( message != null ) {
            return result;
        } else {
            return new byte[]{ type.getCode() };
        }
    }


}
