package ru.alex.st.messenger.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadUtils {

    private static final Logger LOGGER = LogManager.getLogger( ThreadUtils.class );

    public static void sleepFor( long millis ) {
        try {
            Thread.sleep( millis );
        } catch ( InterruptedException e ) {
            LOGGER.warn( e );
        }
    }

}
