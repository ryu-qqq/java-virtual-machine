import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BasicThreadTest {

    @Test
    @DisplayName("단순 러너블 함수 실행")
    void shouldCreateMultipleThreads() {
        Thread thread = new Thread(() -> System.out.println("Hello from thread: " + Thread.currentThread()));
        thread.start();
    }


    @Test
    void shouldCreateMultipleThreadsAndCheckStackSize() {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName());
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    static class NotThreadSafeCounter {
        private int count = 0;

        public void increment() {
            System.out.println("Thread " + Thread.currentThread().getName() + " entered increment()");
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    static class Counter {
        private int count = 0;

        public synchronized void increment() {
            System.out.println("Thread " + Thread.currentThread().getName() + " entered increment()");
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    static class AtomicIntegerCounter {
        private final AtomicInteger count = new AtomicInteger();

        public void increment() {
            System.out.println("Thread " + Thread.currentThread().getName() + " entered increment()");
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
    }


    static class ReentrantLockCounter {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();

        public void increment() {
            lock.lock();
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + " entered increment()");
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            return count;
        }
    }


    @Test
    void shouldNotWorkWellInMultiThreadedEnvironment() throws InterruptedException {
        Counter counter = new Counter();

        long startTime = System.nanoTime();

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10000; i++) {
                executor.submit(counter::increment);
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("스레드 풀이 정상적으로 종료되지 않았습니다.");
                executor.shutdownNow(); // 강제 종료
            }

        }

        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1000000;

        System.out.println("Final count: " + counter.getCount());
        System.out.println("Elapsed time (ms): " + elapsedTimeMs);
    }


    @Test
    void shouldNotWorkWellInMultiThreadedEnvironment2() throws InterruptedException {
        NotThreadSafeCounter counter = new NotThreadSafeCounter();

        long startTime = System.nanoTime();

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10000; i++) {
                executor.submit(counter::increment);
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("스레드 풀이 정상적으로 종료되지 않았습니다.");
                executor.shutdownNow(); // 강제 종료
            }

        }

        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1000000;

        System.out.println("Final count: " + counter.getCount());
        System.out.println("Elapsed time (ms): " + elapsedTimeMs);
    }



    @Test
    void shouldWorkWellInMultiThreadedEnvironment() throws InterruptedException {
        AtomicIntegerCounter counter = new AtomicIntegerCounter();

        long startTime = System.nanoTime();

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10000; i++) {
                executor.submit(counter::increment);
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("스레드 풀이 정상적으로 종료되지 않았습니다.");
                executor.shutdownNow(); // 강제 종료
            }

        }

        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1000000;

        System.out.println("Final count: " + counter.getCount());
        System.out.println("Elapsed time (ms): " + elapsedTimeMs);
    }

    @Test
    void shouldWorkWellInMultiThreadedEnvironment2() throws InterruptedException {
        ReentrantLockCounter counter = new ReentrantLockCounter();

        long startTime = System.nanoTime();

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10000; i++) {
                executor.submit(counter::increment);
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("스레드 풀이 정상적으로 종료되지 않았습니다.");
                executor.shutdownNow(); // 강제 종료
            }

        }

        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1000000;

        System.out.println("Final count: " + counter.getCount());
        System.out.println("Elapsed time (ms): " + elapsedTimeMs);
    }




}
