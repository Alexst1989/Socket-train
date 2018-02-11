package ru.alex.st.messenger.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ProcessorHolder {
    private static final Logger LOGGER = LogManager.getLogger( ProcessorHolder.class );


    private List<Processor> processorList = new LinkedList<>();

    private static long AWAIT_TIME = 5000;

    public <T extends Processor> T addProcessor( T proc ) {
        this.processorList.add( proc );
        return proc;
    }

    public void startAll() {
        for ( Processor proc : processorList ) {
            proc.start();
        }
    }

    public void interruptAll() {
        for ( Processor proc : processorList ) {
            proc.stopProcessor();
        }
    }

}
