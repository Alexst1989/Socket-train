package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;

import java.nio.ByteBuffer;

public interface Decoder<T extends Message> {

    byte getDecodedMessageCode();

    T decode( ByteBuffer message );


    /**
     * Buffer must be ready for read
     * @param buffer
     * @return
     */
    default byte[] getBytesFromBuffer( ByteBuffer buffer ) {
        byte[] byteArray = new byte[ buffer.limit()];
        buffer.get( byteArray );
        return byteArray;
    }

    default Message.MessageType getType( ByteBuffer buffer ) {
        buffer.flip();
        return Message.MessageType.getByCode( buffer.get( 0 ) );
    }

}
