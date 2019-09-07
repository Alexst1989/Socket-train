package ru.alex.st.messanger.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.Processor;
import ru.alex.st.messenger.message.Message;
import ru.alex.st.messenger.message.StringMessage;
import ru.alex.st.messenger.utils.ThreadUtils;

import java.util.function.Consumer;

public class StubMessageGenerator extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( StubMessageGenerator.class );

    private Consumer<Message> consumer;

    private int counter;

    private String id;

    public StubMessageGenerator( String id, Consumer<Message> consumer ) {
        super( "stub-message-generator" );
        this.id = id;
        this.consumer = consumer;
    }

    private StringMessage getNextMessage() {
        return new StringMessage( String.format( "Client [%s]: Message number %s", id, counter++ ) );
    }

    @Override
    public void process() throws Exception {
        while (!Thread.currentThread().isInterrupted()) {
            if ( consumer != null ) {
                consumer.accept( getNextMessage() );
            }
            ThreadUtils.sleepFor( 2000 );
        }
    }
}
