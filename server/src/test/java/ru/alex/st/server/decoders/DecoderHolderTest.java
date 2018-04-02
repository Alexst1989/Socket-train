package ru.alex.st.server.decoders;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.alex.st.messenger.message.Message;

import java.nio.ByteBuffer;

public class DecoderHolderTest {

    private DecoderHolder decoderHolder = new DecoderHolder();

    @BeforeClass
    public void beforeClass() {
        decoderHolder.addDecoder( new StringMessageDecoder() );
    }

    @Test( dataProvider = "decoderHolderTestData" )
    public void testDecoderHolder( ByteBuffer buffer ) throws Exception {
        Message[] messageArray = decoderHolder.apply( buffer );
        Assert.assertEquals( messageArray.length, 1 );
    }

    @DataProvider( name = "decoderHolderTestData" )
    public Object[][] getData() {
        byte c = Character.getDirectionality( 1 );
        ByteBuffer buffer = ByteBuffer.allocateDirect( 10 + 4 + 1 );
        buffer.put( Message.MessageType.STRING.getCode() );
        buffer.putInt( 10 );
        buffer.put( "1234567890".getBytes() );
        buffer.flip();
        return new Object[][]{
                { buffer }
        };
    }
}