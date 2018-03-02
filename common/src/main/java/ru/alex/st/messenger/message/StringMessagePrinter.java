package ru.alex.st.messenger.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class StringMessagePrinter implements Consumer<Message> {

    private static final Logger LOGGER = LogManager.getLogger( StringMessagePrinter.class );

    @Override
    public void accept( Message stringMessage ) {
        if ( stringMessage instanceof StringMessage ) {
            LOGGER.info( "Message: {}", ( ( StringMessage ) stringMessage ).getMessage() );
        } else {
            throw new IllegalArgumentException( "Incorrect message type" );
        }
    }

}
