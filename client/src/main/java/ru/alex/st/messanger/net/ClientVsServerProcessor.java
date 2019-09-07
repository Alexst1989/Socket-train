package ru.alex.st.messanger.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.common.NetworkProcessor;
import ru.alex.st.messenger.common.protocol.ClientServerProtocol;
import ru.alex.st.messenger.common.protocol.ClientServerProtocol.ClientVsServerProtocolState;
import ru.alex.st.messenger.common.stat.Counter;
import ru.alex.st.messenger.common.stat.Stat;
import ru.alex.st.messenger.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Function;


public class ClientVsServerProcessor extends NetworkProcessor {

    private static final Logger LOGGER = LogManager.getLogger( ClientVsServerProcessor.class );

    private static final int BUFFER_SIZE = 8096;

    private static final int DEFAULT_SLEEP_MILLIS = 1000;

    private int port;

    private LinkedBlockingDeque<Message> deque = new LinkedBlockingDeque<>();

    private SocketChannel channel;

    private ClientServerProtocol clientToServerProtocol = new ClientServerProtocol();

    private byte connectionCounter;

    private String id;

    private Counter writtenBytes = Stat.getCounter( "bytesToServer" );

    private Counter readBytes = Stat.getCounter( "bytesFromServer" );

    public ClientVsServerProcessor( int port, String id, Function<ByteBuffer, Message[]> decoder, Consumer<Message> messageConsumer ) {
        super( "client-" + id, decoder, messageConsumer );
        this.port = port;
        this.id = id;
    }

    @Override
    public void process() {
        while ( !Thread.currentThread().isInterrupted() ) {
            switch ( clientToServerProtocol.getCurrentState() ) {
                case READY_TO_READ:
                    clientToServerProtocol.setState( doRead( ClientVsServerProtocolState.READY_TO_WRITE, ClientVsServerProtocolState.CLOSED ) );
                    break;
                case READY_TO_WRITE:
                    clientToServerProtocol.setState( doWrite( ClientVsServerProtocolState.READY_TO_READ, ClientVsServerProtocolState.CLOSED ) );
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
    }

    private ClientVsServerProtocolState doAuthenticate() {
        //TODO authentication process

        return ClientVsServerProtocolState.READY_TO_WRITE;
    }

    private ClientVsServerProtocolState doConnect() {
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
                    return doClose();
                }
            }
        } catch ( IOException e ) {
            LOGGER.error( "Connection failed", e );
            return doClose();
        }
        return toIdentification();
    }

    private ClientVsServerProtocolState doClose() {
        try {
            if ( this.channel.isOpen() ) {
                this.channel.close();
            }
        } catch ( IOException e ) {
            LOGGER.error( "", e );
        }
        return ClientVsServerProtocolState.CLOSED;
    }

    private ClientVsServerProtocolState toIdentification() {
        this.connectionCounter = 0;
        return ClientVsServerProtocolState.IDENTIFICATION;
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

    private ClientVsServerProtocolState doWait( ClientVsServerProtocolState nextState ) {
        sleepFor( DEFAULT_SLEEP_MILLIS );
        return nextState;
    }

    private ClientVsServerProtocolState doRead( ClientVsServerProtocolState success, ClientVsServerProtocolState fail ) {
        try {
            ByteBuffer buffer = writeModeBuffer();
            long n = this.channel.read( buffer );
            if ( n > 0 ) {
                readBytes.inc( n );
                Message[] m = this.decoder.apply( buffer );
                for ( Message message : m ) {
                    messageConsumer.accept( message );
                }
            }
        } catch ( IOException e ) {
            LOGGER.error( e );
            return fail;
        } catch ( Throwable e ) {
            LOGGER.error( e );
            return fail;
        } finally {

        }
        return success;
    }

    private ClientVsServerProtocolState doWrite( ClientVsServerProtocolState success, ClientVsServerProtocolState fail ) {
        Message message = null;
        try {
            message = deque.take();
        } catch ( InterruptedException e ) {
            LOGGER.error( e );
        }
        if ( message != null ) {
            return writeToConsumer( message, success, fail );
        }
        return success;
    }

    private ClientVsServerProtocolState writeToConsumer( Message message, ClientVsServerProtocolState success,
                                                         ClientVsServerProtocolState fail ) {
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
                writtenBytes.inc( channel.write( readModeBuffer() ) );
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

    public void sendMessage( Message message ) {
        deque.offer( message );
    }


}
