package ru.alex.st.messenger.common.stat;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {

    private AtomicLong value = new AtomicLong();

    private String name;

    public Counter( String name ) {
        this.name = name;
    }

    public long inc() {
        return this.value.incrementAndGet();
    }

    public long inc( long value ) {
        return value > 0 ? this.value.addAndGet( value ) : this.value.get();
    }

    public long getValue() {
        return this.value.get();
    }

    public String getName() {
        return name;
    }
}
