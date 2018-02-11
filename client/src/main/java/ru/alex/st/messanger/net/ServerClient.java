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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


/*



 */

public class ServerClient extends Processor {

    private static final Logger LOGGER = LogManager.getLogger( ServerClient.class );

    private int port;

    private static final int BUFFER_SIZE = 8096;

    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    private SocketChannel channel;

    private ByteBuffer writeBuffer;

    private ByteBuffer readBuffer;

    private boolean writeFlipped;

    private ClientClientProtocol clientToClientProtocol;

    private ClientServerProtocol clientToServerProtocol = new ClientServerProtocol();

    private byte connectionCounter;

    private static final byte RECONNECTION_ATTEMTS = 10;

    public ServerClient( int port ) {
        this.port = port;
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

                    return ClientServerProtocolState.IDENTIFICATION;
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
            try {
                LOGGER.info( "Number of attempt: {}", this.connectionCounter );
                Thread.sleep( 1000 );
            } catch ( InterruptedException e ) {
                LOGGER.warn( e );
            }
        }
        this.connectionCounter += 1;
    }


    private ClientServerProtocolState doWrite() {

        Message message = null;
        try {
            //TODO must be sure that connection still alive
            message = queue.take();
        } catch ( InterruptedException e ) {
            LOGGER.error( e );
        }
        if ( message != null ) {
            byte[] bytes = message.getBytes();
            writeToConsummer( bytes, buf -> {
                try {
                    channel.write( buf );
                } catch ( IOException e ) {
                    LOGGER.error( "Exception while writing to channel", e );
                }
            } );
        }

        return ClientServerProtocolState.READY_TO_WRITE;
    }


    private void writeToConsummer( byte[] source, Consumer<ByteBuffer> consumer ) {
        ByteBuffer buffer = writeModeBuffer();
        int count = partCount( source );
        int offset = 0;
        int length = BUFFER_SIZE;
        for ( int i = 0; i < count; i++ ) {
            if ( source.length < length + offset ) {
                length = source.length - offset;
            }
            buffer.put( source, offset, length );
            consumer.accept( readModeBuffer() );
            offset += BUFFER_SIZE;
            buffer.clear();
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

    public void sendMessage( Message message ) {
        queue.offer( message );
    }


    private Message getMessage() {
        return new Message() {
            @Override
            public MessageType getMessageType() {
                return MessageType.BYTES;
            }

            @Override
            public byte[] getBytes() {
                return new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            }
        };
    }

}
