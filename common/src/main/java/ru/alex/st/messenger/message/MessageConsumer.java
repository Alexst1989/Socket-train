package ru.alex.st.messenger.message;

import java.util.function.Consumer;

public interface MessageConsumer<T extends Message> extends Consumer<T> {

    @Override
    void accept( T message );

}
