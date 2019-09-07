package ru.alex.st.test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * https://habr.com/post/349914/
 */

@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@State( Scope.Benchmark )
@Fork( value = 2, jvmArgs = { "-Xms2G", "-Xmx2G" } )
@Warmup( iterations = 4 )
@Measurement( iterations = 2 )
public class BenchmarkTest {

    private static final int N = 1_000_000;
    private static List<Integer> LIST;

    @Setup
    public void setup() {
        LIST = new ArrayList<>( N );
        for ( int i = 0; i < N; i++ ) {
            LIST.add( i );
        }
    }

    @Benchmark
    public void forSum( Blackhole blackhole ) {
        int sum = 0;
        for ( Integer integer : LIST ) {
            sum += integer;
        }
        blackhole.consume( sum );
    }

    @Benchmark
    public void streamSum( Blackhole blackhole ) {
        int sum = LIST.stream().mapToInt( i -> i ).sum();
        blackhole.consume( sum );
    }

    @Benchmark
    public void parallelStreamSum( Blackhole blackhole ) {
        int sum = LIST.stream().parallel().mapToInt( i -> i ).sum();
        blackhole.consume( sum );
    }

}
