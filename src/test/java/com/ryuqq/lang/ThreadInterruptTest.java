package com.ryuqq.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ThreadInterruptTest {


    @Test
    @DisplayName("1초마다 작업 수행중 그런데 3초후 인터럽트 호출 하니 3번 작업 수행해야한다.")
    void interrupt() throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("작업 수행중.. 난 인터럽트 아니야.. ");
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println("아 슈발 나 인터럽트 안대..");
                    System.out.println(Thread.currentThread().isInterrupted());
                    return;

                }
            }
        });

        thread.start();
        Thread.sleep(3000);
        thread.interrupt();
    }

    @Test
    @DisplayName("isInterrupted() 를 호출하면 초기화 된다 ")
    void interrupt_check() throws InterruptedException {

        Thread thread = new Thread(() -> {
            System.out.println("초기 상태: " + Thread.currentThread().isInterrupted()); // false
            Thread.currentThread().interrupt(); // 스레드 인터럽트 발생
            System.out.println("isInterrupted(): " + Thread.currentThread().isInterrupted()); // true
            System.out.println("interrupted(): " + Thread.interrupted()); // interrupted 에 getAndClearInterrupt() 머시기 메서드가 연결되어있음
            System.out.println("다시 isInterrupted(): " + Thread.currentThread().isInterrupted()); // false (초기화됨)
        });

        /**
         *
         *
         *     boolean getAndClearInterrupt() {
         *         boolean oldValue = interrupted;
         *         // We may have been interrupted the moment after we read the field,
         *         // so only clear the field if we saw that it was set and will return
         *         // true; otherwise we could lose an interrupt.
         *         if (oldValue) {
         *             interrupted = false;
         *             clearInterruptEvent();
         *         }
         *         return oldValue;
         *     }
         *
         */

        thread.start();
    }

    @Test
    @DisplayName("실행하지 마시오, 인터럽트는 요청일뿐 즉시 해당 스레드를 멈추지 않는다")
    void infinity_not_working_interrupt_when_call_interrupt() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("작업 수행 중...");
            }
            System.out.println("인터럽트 감지! 종료");
        });

        thread.start();
        Thread.sleep(3000);
        thread.interrupt();
    }


}
