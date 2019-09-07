package ru.alex.st.messenger.common;

import ru.alex.st.messenger.message.Message;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class NetworkProcessor extends Processor {

    private static final int BUFFER_SIZE=1*1024*1024;

    protected Consumer<Message> messageConsumer;

    protected Function<ByteBuffer, Message[]> decoder;

    private ByteBuffer buffer;

    private boolean isFlipped;

    protected NetworkProcessor( String name, Function<ByteBuffer, Message[]> decoder, Consumer<Message> messageConsumer ) {
        super( name );
        this.decoder = decoder;
        this.messageConsumer = messageConsumer;
        this.buffer = ByteBuffer.allocate( BUFFER_SIZE );
    }

    protected ByteBuffer writeModeBuffer() {
        if ( isFlipped ) {
            this.buffer.compact();
            isFlipped = false;
        }
        return this.buffer;
    }

    protected ByteBuffer readModeBuffer() {
        if ( !isFlipped ) {
            this.buffer.flip();
            isFlipped = true;
        }
        return this.buffer;
    }

    protected ByteBuffer clearBuffer() {
        this.buffer.clear();
        isFlipped = false;
        return this.buffer;
    }


}
