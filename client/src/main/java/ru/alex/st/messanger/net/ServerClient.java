package ru.alex.st.messanger.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.ClientServerProtocol;
import ru.alex.st.messenger.common.ClientServerProtocol.ClientServerProtocolState;
import ru.alex.st.messenger.common.Processor;
import ru.alex.st.messenger.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;


/*



 */

public class ServerClient extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( ServerClient.class );

    private static final int BUFFER_SIZE = 8096;

    private static final int DEFAULT_SLEEP_MILLIS = 1000;

    private int port;

    private LinkedBlockingDeque<Message> deque = new LinkedBlockingDeque<>();

    private SocketChannel channel;

    private ByteBuffer writeBuffer;

    private ByteBuffer readBuffer;

    private boolean writeFlipped;

    private ClientClientProtocol clientToClientProtocol;

    private ClientServerProtocol clientToServerProtocol = new ClientServerProtocol();

    private byte connectionCounter;

    private String id;

    private static final byte RECONNECTION_ATTEMTS = 10;

    public ServerClient( int port, String id ) {
        this.port = port;
        this.id = id;
        this.writeBuffer = ByteBuffer.allocateDirect( BUFFER_SIZE );
        this.readBuffer = ByteBuffer.allocateDirect( BUFFER_SIZE );
    }

    @Override
    public void process() {
        switch ( clientToServerProtocol.getCurrentState() ) {
            case READY_TO_WRITE:
                clientToServerProtocol.setState( doWrite() );
                break;
            case IDENTIFICATION:
                clientToServerProtocol.setState( doAuthenticate() );
                break;
            case CLOSED:
                clientToServerProtocol.setState( doConnect() );
                break;
            case WAIT:
                clientToServerProtocol.setState( doWait( clientToServerProtocol.getCurrentState() ) );
                break;
        }
    }

    private ClientServerProtocolState doAuthenticate() {


        return ClientServerProtocolState.READY_TO_WRITE;
    }

    private ClientServerProtocolState doConnect() {
        onConnectionNextAttempt();
        try {
            channel = SocketChannel.open();
            channel.configureBlocking( false );
            boolean connected = channel.connect( new InetSocketAddress( port ) );
            if ( !connected ) {
                boolean established = channel.finishConnect();
                if ( established ) {
                    LOGGER.info( "Connection to [{}] established", port );

                    return toIdentification();
                } else {
                    LOGGER.info( "Connection failed" );
                    return ClientServerProtocolState.CLOSED;
                }
            }
        } catch ( IOException e ) {
            LOGGER.error( "Connection failed", e );
            return ClientServerProtocolState.CLOSED;
        }
        return toIdentification();
    }

    private ClientServerProtocolState toIdentification() {
        this.connectionCounter = 0;
        return ClientServerProtocolState.IDENTIFICATION;
    }

    private void onConnectionNextAttempt() {
        if ( this.connectionCounter > 0 ) {
            LOGGER.info( "Number of attempt: {}", this.connectionCounter );
            sleepFor( DEFAULT_SLEEP_MILLIS );
        }
        this.connectionCounter += 1;
    }

    private void sleepFor( long millis ) {
        try {
            LOGGER.info( "Sleeping {} millis...", millis );
            Thread.sleep( millis );
        } catch ( InterruptedException e ) {
            LOGGER.warn( e );
        }
    }

    private ClientServerProtocolState doWait( ClientServerProtocolState nextState ) {
        sleepFor( DEFAULT_SLEEP_MILLIS );
        return nextState;
    }

    private ClientServerProtocolState doWrite() {

        Message message = null;
        try {
            //TODO must be sure that connection still alive
            message = deque.take();
        } catch ( InterruptedException e ) {
            LOGGER.error( e );
        }
        if ( message != null ) {
            return writeToConsummer( message, ClientServerProtocolState.READY_TO_WRITE, ClientServerProtocolState.CLOSED );
        }

        return ClientServerProtocolState.READY_TO_WRITE;
    }


    private ClientServerProtocolState writeToConsummer( Message message, ClientServerProtocolState success,
                                                        ClientServerProtocolState fail ) {
        try {
            byte[] source = message.getBytes();
            int count = partCount( source );
            int offset = 0;
            int length = BUFFER_SIZE;
            for ( int i = 0; i < count; i++ ) {
                if ( source.length < length + offset ) {
                    length = source.length - offset;
                }
                ByteBuffer buffer = writeModeBuffer();
                buffer.put( source, offset, length );
                channel.write( readModeBuffer() );
                offset += BUFFER_SIZE;
                this.clearBuffer();
            }
            return success;
        } catch ( IOException e ) {
            LOGGER.error( "Exception while writing to channel", e );
            deque.offerFirst( message );
            return fail;
        } finally {
            this.clearBuffer();
        }
    }

    private int partCount( byte[] array ) {
        return array.length / BUFFER_SIZE + 1;
    }

    private ByteBuffer writeModeBuffer() {
        if ( writeFlipped ) {
            this.writeBuffer.compact();
            writeFlipped = false;
        }
        return this.writeBuffer;
    }

    private ByteBuffer readModeBuffer() {
        if ( !writeFlipped ) {
            this.writeBuffer.flip();
            writeFlipped = true;
        }
        return this.writeBuffer;
    }

    private ByteBuffer clearBuffer() {
        this.writeBuffer.clear();
        writeFlipped = false;
        return this.writeBuffer;
    }

    public void sendMessage( Message message ) {
        deque.offer( message );
    }


}
