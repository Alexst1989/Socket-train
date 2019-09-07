package ru.alex.st.messenger.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class TestProcessor extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( TestProcessor.class );

    private final TestTask task;

    protected int iterationCounter = 0;

    protected TestProcessor( String name, TestTask task ) {
        super( name );
        this.task = task;
    }

    @Override
    public void process() throws Exception {
        iterationCounter++;
        try {
            task.perform();
        } catch ( InterruptedException ex ) {
            this.interrupt();
        }
        LOGGER.info( "Iteration [{}] has finished", iterationCounter );
    }

    int gerIterationCounter() {
        return this.iterationCounter;
    }

}
