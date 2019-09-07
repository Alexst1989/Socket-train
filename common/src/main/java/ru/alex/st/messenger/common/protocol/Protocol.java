package ru.alex.st.messenger.common.protocol;

public interface Protocol {

    String getName();

    ProtocolStateName getState();

    void setState(ProtocolStateName state);

}
