package chapter6;

import org.junit.jupiter.api.Test;

public class ClassLoadingTest {

    static {
        System.out.println("ClassLoadingTest loaded!");
    }

    @Test
    void shouldLoadClassAndPrintStaticBlock() {
        new ClassLoadingTest();
    }


}
