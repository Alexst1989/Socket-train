package ru.alex.st.messenger.common.protocol.auth;

import ru.alex.st.messenger.common.protocol.Protocol;
import ru.alex.st.messenger.common.protocol.ProtocolStateName;

public class AuthenticationProtocol implements Protocol {

    public static final String PREFIX = "";

    private String id;

    private ProtocolStateName currentState;

    public AuthenticationProtocol( String id ) {
        this.id = id;
    }

    @Override
    public String getName() {
        return PREFIX + this.id;
    }

    @Override
    public ProtocolStateName getState() {
        return null;
    }

    @Override
    public void setState( ProtocolStateName state ) {

    }


}
