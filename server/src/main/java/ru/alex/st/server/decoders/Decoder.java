package ru.alex.st.server.decoders;

import ru.alex.st.messenger.message.Message;

public interface Decoder<T extends Message> {

    byte getDecodedMessageCode();

    T decode( Message message );

}
