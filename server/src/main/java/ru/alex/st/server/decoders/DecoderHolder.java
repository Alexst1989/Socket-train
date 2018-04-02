package ru.alex.st.server.decoders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.message.Message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DecoderHolder implements Function<ByteBuffer, Message[]> {

    private static final Logger LOGGER = LogManager.getLogger( DecoderHolder.class );

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
    public Message[] apply( ByteBuffer buffer ) {
        ArrayList<Message> messageList = new ArrayList<>();
        int currentMessage = 0;
        int bufferOffset = 1;
        buffer.flip();
        while ( buffer.position() < buffer.limit() ) {
            int messageLength = buffer.getInt( bufferOffset ) + 5;
            byte[] message = new byte[ messageLength ];
            buffer.get( message, 0, messageLength );
            messageList.add( decoderMap.get( message[ 0 ] ).decode( message ) );
            bufferOffset += messageLength;
            currentMessage++;
        }
        LOGGER.debug( "Messages received {}", currentMessage );
        return messageList.toArray( new Message[ messageList.size() ] );
    }
}
