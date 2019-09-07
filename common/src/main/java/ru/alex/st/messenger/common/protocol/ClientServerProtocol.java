package ru.alex.st.messenger.common.protocol;

public class ClientServerProtocol {

    private ClientVsServerProtocolState currentState;

    public enum ClientVsServerProtocolState {
        CONNECTED, IDENTIFICATION, READY_TO_READ, READY_TO_WRITE, CLOSED, WAIT
    }

    public ClientServerProtocol() {
        currentState = ClientVsServerProtocolState.CLOSED;
    }

    public ClientVsServerProtocolState getCurrentState(){
        return currentState;
    }

    public ClientVsServerProtocolState setState( ClientVsServerProtocolState state) {
        currentState = state;
        return currentState;
    }
}
