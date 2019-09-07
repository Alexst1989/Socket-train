package ru.alex.st.messanger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messanger.example.StubMessageGenerator;
import ru.alex.st.messanger.net.ClientVsServerProcessor;
import ru.alex.st.messenger.common.ProcessorHolder;
import ru.alex.st.messenger.common.protocol.decoders.DecoderHolder;
import ru.alex.st.messenger.message.StringMessagePrinter;
import ru.alex.st.messenger.utils.ResourcesUtils;

import java.util.Properties;

public class Messenger {

    private static final Logger LOGGER = LogManager.getLogger( Messenger.class );

    private ProcessorHolder processorHolder = new ProcessorHolder();

    public Messenger( Properties props ) {
        int port = Integer.valueOf( props.getProperty( "port" ) );
        String id = props.getProperty( "id" );
        ClientVsServerProcessor clientVsServerProcessor = this.processorHolder
                .addProcessor( new ClientVsServerProcessor( port, id, DecoderHolder.getInstance(),
                        StringMessagePrinter.getInstance() ) );
        this.processorHolder.addProcessor(
                new StubMessageGenerator( id, clientVsServerProcessor::sendMessage ) );
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
        LOGGER.info( "Starting messenger with id: [{}]", props.getProperty( "id" ) );
        Messenger messenger = new Messenger( props );
        Runtime.getRuntime().addShutdownHook( new Thread( messenger::stop ) );
        messenger.start();
    }

}
