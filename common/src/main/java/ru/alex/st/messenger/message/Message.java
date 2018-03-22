package ru.alex.st.messenger.message;


public interface Message {

    enum MessageType {
        BYTES( ( byte ) 0 ),
        STRING( ( byte ) 1 ),
        UNKNOWN( ( byte ) -128 );

        private byte code;

        MessageType( byte code ) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }

        public static MessageType getByCode( byte code ) {
            for ( MessageType type : values() ) {
                if ( type.code == code ) {
                    return type;
                }
            }
            return MessageType.UNKNOWN;
        }

    }

    byte[] getBytes();

    long length();

    default MessageType getMessageType() {
        return MessageType.BYTES;
    }

    default void checkMessageType( byte code ) {
        MessageType type = MessageType.getByCode( code );
        if ( this.getMessageType() != type ) {
            throw new IncorrectMessageTypeException( String.format( "Message type does not equal %s", type ) );
        }
    }

    byte[] getMessageBytes();

}
