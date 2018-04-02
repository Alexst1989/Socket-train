package ru.alex.st.messenger.message;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.alex.st.messenger.utils.ArrayUtils;

import static org.testng.Assert.assertEquals;

public class StringMessageTest {

    String text = "Some text";

    @Test
    public void testMessageToByteConversions() {
        StringMessage message = new StringMessage( text );

        byte[] bytes = message.getBytes();
        assertEquals( bytes.length, 1 + 4 + text.length() );
        assertEquals( bytes[ 0 ], Message.MessageType.STRING.getCode() );

        long length = ArrayUtils.getIntFromBytes( bytes, 1 );
        assertEquals( length, text.length() );
    }

    @Test
    public void testSecondConstructor() {
        StringMessage message1 = new StringMessage( text );

        StringMessage message = new StringMessage( message1.getBytes() );

        Assert.assertEquals( message.getMessage(), text );
        Assert.assertEquals( message.getBytes().length, 1 + 4 + text.length() );

    }

}