package ru.alex.st.messenger.message;

public class StringMessage implements Message {

    private String message;

    public StringMessage( String message ) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public StringMessage setMessage( String message ) {
        this.message = message;
        return this;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.STRING;
    }

    @Override
    public byte[] getBytes() {
        if ( message != null ) {
            return message.getBytes();
        } else {
            return new byte[ 0 ];
        }
    }
}
