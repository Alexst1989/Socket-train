package ru.alex.st.messenger.common.protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProtocol implements Protocol {

    public Map<String, ProtocolStateName> availableStates = new HashMap<>();

    public AbstractProtocol( ProtocolStateName... states ) {
        for ( ProtocolStateName state : states ) {
            this.availableStates.put( state.getName(), state );
        }
    }

    @Override
    public abstract String getName();

    @Override
    public ProtocolStateName getState() {
        return null;
    }

    @Override
    public void setState( ProtocolStateName state ) {

    }
}
