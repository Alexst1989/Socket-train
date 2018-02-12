package ru.alex.st.messenger.common;

public class ClientServerProtocol {

    private ClientServerProtocolState currentState;

    public enum ClientServerProtocolState {
        CONNECTED, IDENTIFICATION, READY_TO_WRITE, CLOSED, WAIT
    }

    public ClientServerProtocol() {
        currentState = ClientServerProtocolState.CLOSED;
    }

    public ClientServerProtocolState getCurrentState(){
        return currentState;
    }

    public ClientServerProtocolState setState(ClientServerProtocolState state) {
        currentState = state;
        return currentState;
    }
}
