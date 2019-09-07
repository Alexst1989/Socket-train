package ru.alex.st.messenger.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alex.st.messenger.MessangerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourcesUtils {

    private static final Logger LOGGER = LogManager.getLogger( ResourcesUtils.class );

    public static InputStream loadResource( String name ) {
        return ResourcesUtils.class.getClassLoader().getResourceAsStream( name );
    }

    public static Properties loadPropertiesFromResource( String name ) {
        Properties props = new Properties();
        try {
            props.load( loadResource( name ) );
            return props;
        } catch ( IOException e ) {
            throw new MessangerException( e );
        }
    }
}
