package ru.alex.st.messenger.common;

import org.testng.annotations.Test;

public class ProcessorTest {

    @Test( timeOut = 1000 )
    public void testProcessorStartStop() {
        Processor testProcessor = new TestProcessor( "test-processor", () -> {
            Thread.sleep( 2000 );
        } );
        testProcessor.start();
        testProcessor.stopProcessor();
    }

    @Test
    public void testThrowException() throws InterruptedException {
        Processor testProcessor = new TestProcessor( "test-processor", () -> {
            Thread.sleep( 250 );
            throw new Exception( "TEST EXCEPTION" );
        } );
        testProcessor.start();
        Thread.sleep( 500 );
        testProcessor.stopProcessor();
    }

}