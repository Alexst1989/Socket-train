package ru.alex.st.server.net;

public class MessangerServerException extends RuntimeException {

    public MessangerServerException( String mess, Throwable e ) {
        super( mess, e );
    }

    public MessangerServerException( Throwable e ) {
        super( e );
    }

    public MessangerServerException( String mess ) {
        super( mess );
    }

    public MessangerServerException() {
        super();
    }

}
