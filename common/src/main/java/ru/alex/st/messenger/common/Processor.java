package ru.alex.st.messenger.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Processor extends Thread {

    private static final Logger LOGGER = LogManager.getLogger( Processor.class );

    private AtomicBoolean runFlag = new AtomicBoolean( true );

    private String name;

    private long iterationInteval;

    protected Processor() {
    }

    protected Processor( String name ) {
        this( name, 0 );
    }

    protected Processor( String name, long iterationInteval ) {
        this.name = name;
        this.iterationInteval = iterationInteval;
    }

    @Override
    public void run() {
        if ( name != null ) {
            Thread.currentThread().setName( this.name );
        }
        while ( runFlag.get() ) {
            try {
                this.process();
                if ( iterationInteval > 0 ) {
                    Thread.sleep( iterationInteval );
                }
            } catch ( Throwable ex ) {
                LOGGER.error( "Unexpected exception occurred", ex );
            }
        }
        LOGGER.info( String.format( "Thread [%s] is stopped", Thread.currentThread().getName() ) );
    }

    public void stopProcessor() {
        runFlag.compareAndSet( true, false );
        this.interrupt();
    }

    public abstract void process() throws Exception;

}
