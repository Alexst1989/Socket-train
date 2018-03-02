package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;
import ru.alex.st.server.net.MessangerServerException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DecoderHolder implements Function<ByteBuffer, Message> {

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
    public Message apply( ByteBuffer buffer ) {
        buffer.flip();
        byte typeByte = buffer.get( 0 );
        Decoder decoder = decoderMap.get( typeByte );
        if ( decoder == null ) {
            throw new MessangerServerException( "Unsupported message type" );
        }
        return decoder.decode( buffer );
    }
}
