package ru.alex.st.messanger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messanger.net.ServerClient;
import ru.alex.st.messenger.common.ProcessorHolder;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Messenger {

    private static final Logger LOGGER = LogManager.getLogger( Messenger.class );

    private ProcessorHolder processorHolder = new ProcessorHolder();

    public Messenger() {
        Properties props = new Properties();
        try {
            props.load( getClass().getClassLoader().getResourceAsStream( "messenger.properties" ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Properties read error", e );
        }

        int port = Integer.valueOf( ( String ) props.get( "port" ) );

        ServerClient serverClient = this.processorHolder.addProcessor( new ServerClient( port ) );

    }

    public void start() {
        processorHolder.startAll();
    }

    public void stop() {
        processorHolder.interruptAll();
    }

    public static void main( String args[] ) {
        Messenger messanger = new Messenger();
        messanger.start();
        Scanner sc = new Scanner( System.in );
        sc.nextLine();
        messanger.stop();

    }

}
