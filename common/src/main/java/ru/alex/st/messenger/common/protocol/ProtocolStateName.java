package ru.alex.st.messenger.common.protocol;

public enum ProtocolStateName {

    CONNECTED( "CONNECTED" ),
    AUTHENTICATION("AUTHENTICATION"),
    READY_TO_READ("READY_TO_READ"),
    READY_TO_WRITE("READY_TO_WRITE"),
    CLOSED("CLOSED"),
    WAIT("WAIT");

    private String name;

    ProtocolStateName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
