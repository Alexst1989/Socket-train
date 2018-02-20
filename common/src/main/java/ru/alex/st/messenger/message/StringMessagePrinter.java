package ru.alex.st.messenger.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class StringMessagePrinter implements Consumer<StringMessage> {

    private static final Logger LOGGER = LogManager.getLogger( StringMessagePrinter.class );

    @Override
    public void accept( StringMessage stringMessage ) {
        LOGGER.info( "Message: {}", stringMessage );
    }

}
