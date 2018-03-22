package ru.alex.st.messenger.message;

import org.testng.annotations.Test;
import ru.alex.st.messenger.utils.ArrayUtils;

import static org.testng.Assert.assertEquals;

public class StringMessageTest {

    @Test
    public void testMessageToByteConversions() {
        String text = "Some text";
        StringMessage message = new StringMessage( text );

        byte[] bytes = message.getBytes();
        assertEquals( bytes.length, 1 + 8 + text.length() );
        assertEquals( bytes[ 0 ], Message.MessageType.STRING.getCode() );

        long length = ArrayUtils.getLongFromBytes( bytes, 1 );
        assertEquals( length, text.length() );
    }

}