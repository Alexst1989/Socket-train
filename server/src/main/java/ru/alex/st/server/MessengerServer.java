package ru.alex.st.server;

import ru.alex.st.messenger.common.ProcessorHolder;
import ru.alex.st.server.net.MainServer;
import ru.alex.st.server.net.MessangerServerException;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class MessengerServer {

    private ProcessorHolder processorHolder = new ProcessorHolder();

    public MessengerServer() {
//        Runtime.getRuntime().addShutdownHook( new Thread( processorHolder::interruptAll ) );
        Properties props = new Properties();
        try {
            props.load( getClass().getClassLoader().getResourceAsStream( "messenger.properties" ) );
        } catch ( IOException e ) {
            throw new MessangerServerException( "Can't load properties", e );
        }
        int port = Integer.valueOf( ( String ) props.get( "port" ) );
        processorHolder.addProcessor( new MainServer( port ) );
    }

    public void start() {
        processorHolder.startAll();
    }

    public void stop() {
        processorHolder.interruptAll();
    }


    public static void main( String args[] ) {
        MessengerServer server = new MessengerServer();
        server.start();
        Scanner sc = new Scanner( System.in );
        sc.nextLine();
        server.stop();
    }

}
