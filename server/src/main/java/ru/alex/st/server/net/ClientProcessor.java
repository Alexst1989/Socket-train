package ru.alex.st.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.Processor;
import ru.alex.st.messenger.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClientProcessor extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( ClientProcessor.class );

    private static int counter;

    private Selector selector;

    private Consumer<Message> messageConsumer;

    private Function<ByteBuffer, Message[]> decoder;

    private SocketChannel channel;

    private SelectionKey key;

    public ClientProcessor( SocketChannel channel, Consumer<Message> messageConsumer, Function<ByteBuffer, Message[]> decoder ) {
        super( "client-processor-" + ++counter );
        this.messageConsumer = messageConsumer;
        this.decoder = decoder;
        this.channel = channel;

    }

    protected void init() {
        try {
            selector = Selector.open();
            this.key = channel.register( selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
        } catch ( IOException e ) {
            LOGGER.error( "Error", e );
        }
    }

    @Override
    public void process() throws Exception {
        init();
        while ( !Thread.currentThread().isInterrupted() ) {
            int numSelectedKeys = this.selector.select();
            if ( numSelectedKeys <= 0 ) {
                LOGGER.trace( "Number of selected keys = [{}]", numSelectedKeys );
                return;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while ( keyIterator.hasNext() ) {
                SelectionKey key = keyIterator.next();
                if ( key.isReadable() ) {
                    doRead( key );
                }
                keyIterator.remove();
                LOGGER.trace( "Key [{}] is removed", key );
            }
        }
    }

    private void doRead( SelectionKey key ) throws IOException {
        SocketChannel clientChannel = ( SocketChannel ) key.channel();
        clientChannel.configureBlocking( false );
        ByteBuffer buf = ByteBuffer.allocateDirect( 8096 );
        //TODO count read bytes
        int readBytes = clientChannel.read( buf );
        Message[] messageArray = decoder.apply( buf );
        for ( Message message : messageArray ) {
            messageConsumer.accept( message );
        }
    }

    @Override
    public void stopProcessor() {
        try {
            if ( this.selector.isOpen() ) {
                this.selector.close();
            }
            if ( this.channel.isOpen() ) {
                this.channel.close();
            }
        } catch ( IOException e ) {
            LOGGER.error( "Error during stopping client processor", e );
        }
        super.stopProcessor();
    }

}
