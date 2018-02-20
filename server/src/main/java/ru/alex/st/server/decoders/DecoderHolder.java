package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DecoderHolder implements Function<Message, Message> {

    private Map<Byte, Decoder> decoderMap = new HashMap<>();

    public DecoderHolder( Decoder... decoders ) {
        for ( Decoder decoder : decoders ) {
            addDecoder( decoder );
        }
    }

    public void addDecoder( Decoder decoder ) {
        decoderMap.put( decoder.getDecodedMessageCode(), decoder );
    }

    @Override
    public Message apply( Message message ) {
        Decoder decoder = decoderMap.get( message.getMessageType().getCode() );
        return decoder.decode( message );
    }
}
