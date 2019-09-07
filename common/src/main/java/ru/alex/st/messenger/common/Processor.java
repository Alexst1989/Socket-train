package ru.alex.st.messenger.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Processor extends Thread {

    private static final Logger LOGGER = LogManager.getLogger( Processor.class );

    private String name;

    protected Processor( String name ) {
        this.name = name;
    }

    @Override
    public void run() {
        Thread.currentThread().setName( this.name );
        try {
            this.process();
        } catch ( InterruptedException ex ) {
            LOGGER.info( "Thread [{}] was interuppted.", this.name );
            Thread.currentThread().interrupt();
        } catch ( Throwable ex ) {
            LOGGER.error( "Unexpected exception occurred", ex );
        }
        LOGGER.info( String.format( "Processor [%s] is stopped", this.name ) );
    }

    public void stopProcessor() {
        this.interrupt();
    }

    public abstract void process() throws Exception;

}
