import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class AtomicIntegerTest {


    @Test
    void testAtomicInteger(){

        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.incrementAndGet();

    }
}
