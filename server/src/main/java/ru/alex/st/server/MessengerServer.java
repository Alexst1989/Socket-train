package ru.alex.st.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.ProcessorHolder;
import ru.alex.st.messenger.message.Message;
import ru.alex.st.messenger.message.MessageSplitter;
import ru.alex.st.messenger.message.StringMessagePrinter;
import ru.alex.st.messenger.utils.ResourcesUtils;
import ru.alex.st.messenger.common.protocol.decoders.DecoderHolder;
import ru.alex.st.messenger.common.protocol.decoders.StringMessageDecoder;
import ru.alex.st.server.net.NetworkTcpServer;

import java.util.Properties;

public class MessengerServer {

    private static final Logger LOGGER = LogManager.getLogger( MessengerServer.class );

    private ProcessorHolder processorHolder = new ProcessorHolder();

    public MessengerServer( Properties props ) {
        int port = Integer.valueOf( props.getProperty( "port" ) );
        DecoderHolder dh = new DecoderHolder( new StringMessageDecoder() );
        MessageSplitter splitter = new MessageSplitter().addConsumer( Message.MessageType.STRING, new StringMessagePrinter() );
        processorHolder.addProcessor( new NetworkTcpServer( port, dh, splitter ) );
    }

    public void start() {
        processorHolder.startAll();
    }

    public void stop() {
        processorHolder.interruptAll();
    }

    public static void main( String args[] ) {
        String propertyFileName = args[ 0 ];
        Properties props = ResourcesUtils.loadPropertiesFromResource( propertyFileName );
        LOGGER.info( "Starting messenger server on port [{}]", props.getProperty( "port" ) );
        MessengerServer server = new MessengerServer( props );
        Runtime.getRuntime().addShutdownHook( new Thread( server::stop ) );
        server.start();
    }

}
