package ru.alex.st.messenger.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MessageSplitter implements Consumer<Message> {

    private Map<Message.MessageType, List<Consumer<Message>>> consumerMap = new HashMap<>();

    public MessageSplitter addConsumer( Message.MessageType type, Consumer<Message> consumer ) {
        List<Consumer<Message>> consumerList = consumerMap.computeIfAbsent( type, type1 -> new ArrayList() );
        consumerList.add( consumer );
        return this;
    }


    @Override
    public void accept( Message message ) {
        Message.MessageType messageType = message.getMessageType();
        consumerMap.get( messageType ).forEach( item -> item.accept( message ) );

    }
}
