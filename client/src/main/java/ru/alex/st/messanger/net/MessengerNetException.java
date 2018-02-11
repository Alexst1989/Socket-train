package ru.alex.st.messanger.net;

public class MessengerNetException extends Exception {

    public MessengerNetException( String mess, Throwable e ) {
        super( mess, e );
    }

    public MessengerNetException( String mess ) {
        super( mess );
    }

    public MessengerNetException( Throwable e ) {
        super( e );
    }

    public MessengerNetException() {
    }

}
