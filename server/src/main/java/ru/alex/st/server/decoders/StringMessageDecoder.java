package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;
import ru.alex.st.messenger.message.StringMessage;

import java.nio.ByteBuffer;

public class StringMessageDecoder implements Decoder<StringMessage> {

    @Override
    public byte getDecodedMessageCode() {
        return Message.MessageType.STRING.getCode();
    }

    @Override
    public StringMessage decode( ByteBuffer buffer ) {
        
        return new StringMessage( getBytesFromBuffer( buffer ) );
    }

    @Override
    public StringMessage decode( byte[] byteArray ) {
        return new StringMessage( byteArray );
    }


}
