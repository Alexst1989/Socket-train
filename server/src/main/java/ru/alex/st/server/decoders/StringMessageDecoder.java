package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;
import ru.alex.st.messenger.message.StringMessage;

public class StringMessageDecoder implements Decoder<StringMessage> {

    @Override
    public byte getDecodedMessageCode() {
        return Message.MessageType.STRING.getCode();
    }

    @Override
    public StringMessage decode( Message message ) {
        return new StringMessage( message.getMessageBytes() );
    }

}
