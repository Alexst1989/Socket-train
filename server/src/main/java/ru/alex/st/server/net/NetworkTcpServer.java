package ru.alex.st.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.NetworkProcessor;
import ru.alex.st.messenger.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkTcpServer extends NetworkProcessor {

    private static final Logger LOGGER = LogManager.getLogger( NetworkTcpServer.class );

    private int port;

    private ServerSocket socket;

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private List<ClientProcessor> clientProcessorsList = new ArrayList<>();

    public NetworkTcpServer( int port, Function<ByteBuffer, Message[]> decoder, Consumer<Message> messegeConsumer ) {
        super( "NetworkTcpServer-" + port, decoder, messegeConsumer );
        this.port = port;
    }

    protected void init() {
        try {
            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind( new InetSocketAddress( this.port ) );
            serverSocketChannel.configureBlocking( false );
            this.socket = serverSocketChannel.socket();
            LOGGER.info( "Server socket has opened port {}", port );

            serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT );
        } catch ( IOException e ) {
            throw new MessangerServerException( "Can't open port " + port, e );
        }
    }

    @Override
    public void process() {
        init();
        while ( !Thread.currentThread().isInterrupted() ) {
            try {
                //Return 0 after connection establishment
                int readyKeys = this.selector.select();
                if ( readyKeys == 0 ) {
                    LOGGER.debug( "ready channels == 0" );
                    return;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while ( keyIterator.hasNext() ) {

                    SelectionKey key = keyIterator.next();
                    try {
                        if ( key.isAcceptable() ) {
                            registerClient( key );
                            // a connection was accepted by a ServerSocketChannel.
                            LOGGER.info( "Key [{}] is Acceptable", key );

                        } else if ( key.isConnectable() ) {
                            // a connection was established with a remote server.
                            LOGGER.info( "Key [{}] is Connectable", key );
                        } else if ( key.isReadable() ) {
                            LOGGER.info( "Key [{}] is Readable", key );
//                        doRead( key );
                        } else if ( key.isWritable() ) {
                            // a channel is ready for writing
                            LOGGER.info( "Key [{}] is Writable", key );
                        }

                        keyIterator.remove();
                        LOGGER.info( "Key [{}] is removed", key );
                    } catch ( IOException ex ) {
                        key.channel().close();
                        keyIterator.remove();
                    }
                }
            } catch ( IOException e ) {
                LOGGER.error( e );
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


    private void registerClient( SelectionKey selectedKey ) throws IOException {
        ServerSocketChannel channel = ( ServerSocketChannel ) selectedKey.channel();
        SocketChannel clientChannel = channel.accept();
        clientChannel.configureBlocking( false );
        ClientProcessor clientProcessor = new ClientProcessor( clientChannel, messageConsumer, decoder );
        clientProcessorsList.add( clientProcessor );
        clientProcessor.start();
    }

}
