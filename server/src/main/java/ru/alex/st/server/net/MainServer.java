package ru.alex.st.server.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.Processor;
import ru.alex.st.messenger.utils.ByteBufferUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MainServer extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( MainServer.class );

    private int port;

    private ServerSocket socket;

    private Selector selector;

    private static final long ITERATION_INTERVAL = 1000;

    public MainServer( int port ) {
        super( "ServerThread", 0 );
        this.port = port;
        try {
            this.selector = Selector.open();
            ServerSocketChannel channel = ServerSocketChannel.open();

            channel.bind( new InetSocketAddress( this.port ) );
            channel.configureBlocking( false );
            this.socket = channel.socket();
            LOGGER.info( "Server socket has opened port {}", port );

            SelectionKey key = channel.register( selector, SelectionKey.OP_ACCEPT );
            LOGGER.info( "KEY = [{}]", key.toString() );
        } catch ( IOException e ) {
            throw new MessangerServerException( "Can't open port " + port, e );
        }
    }

    @Override
    public void process() throws InterruptedException {
        try {
            //Return 0 after connection establishment
            int readyChannels = this.selector.select();
            if ( readyChannels == 0 ) {
                LOGGER.debug( "ready channels == 0" );
                Thread.sleep( ITERATION_INTERVAL );
                return;
            }


            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while ( keyIterator.hasNext() ) {
                SelectionKey key = keyIterator.next();
                if ( key.isAcceptable() ) {
                    registerClient( key );
                    // a connection was accepted by a ServerSocketChannel.
                    LOGGER.info( "Key [{}] is Acceptable", key );
                } else if ( key.isConnectable() ) {
                    // a connection was established with a remote server.
                    LOGGER.info( "Key [{}] is Connectable", key );
                } else if ( key.isReadable() ) {
                    SocketChannel clientChannel = ( SocketChannel ) key.channel();
                    clientChannel.configureBlocking( false );


                    ByteBuffer buf = ByteBuffer.allocateDirect( 8096 );
                    clientChannel.read( buf );

                    ByteBufferUtils.printByteBuffer( buf );

                    // a channel is ready for reading
                    LOGGER.info( "Key [{}] is Readable", key );

                } else if ( key.isWritable() ) {
                    // a channel is ready for writing
                    LOGGER.info( "Key [{}] is Writable", key );
                }

                keyIterator.remove();
                LOGGER.info( "Key [{}] is removed", key );
            }
        } catch ( IOException e ) {
            LOGGER.error( e );
        }


    }

    private void registerClient( SelectionKey selectedKey ) throws IOException {
        ServerSocketChannel channel = ( ServerSocketChannel ) selectedKey.channel();
        SocketChannel clientChannel = channel.accept();
        clientChannel.configureBlocking( false );
        clientChannel.register( this.selector, SelectionKey.OP_READ );

    }


}