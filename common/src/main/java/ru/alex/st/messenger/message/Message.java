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

    default MessageType getMessageType() {
        return MessageType.BYTES;
    }

    byte[] getMessageBytes();

}
