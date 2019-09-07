package ru.alex.st.messenger.common.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Stat {

    private static Map<String, Counter> counterMap = new ConcurrentHashMap<>();

    public static Counter getCounter( String name ) {
        return counterMap.computeIfAbsent( name, k -> new Counter( k ) );
    }

    public static void report( Consumer<Map<String, Counter>> counterMapConsumer ) {
        counterMapConsumer.accept( Stat.counterMap );
    }

}
