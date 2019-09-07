package ru.alex.st.messenger;

public class MessangerException extends RuntimeException {

    public MessangerException( String msg, Throwable e ) {
        super( msg, e );
    }

    public MessangerException( Throwable e ) {
        super( e );
    }

    public MessangerException( String msg ) {
        super( msg );
    }

    public MessangerException() {
        super();
    }

}
