package ru.alex.st.messenger.message;

public class IncorrectMessageTypeException extends IllegalArgumentException {

    public IncorrectMessageTypeException( String msg ) {
        super( msg );
    }
}
