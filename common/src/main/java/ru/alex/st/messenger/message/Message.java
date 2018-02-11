package ru.alex.st.messenger.message;


public interface Message {

    MessageType getMessageType();

    byte[] getBytes();

    enum MessageType {
        STRING, BYTES;
    }

}
