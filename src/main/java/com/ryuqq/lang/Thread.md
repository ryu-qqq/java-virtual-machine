# java.lang.Thread
# Thread 주요 메서드 분석

## 1. `interrupt()`란?
`interrupt()` 메서드는 **스레드를 강제 종료하는 것이 아니라, 인터럽트 신호를 보내는 역할**을 한다.
즉, 현재 실행 중인 스레드를 방해하거나, 특정 블로킹 작업을 깨우는 기능을 한다.

### 📌 인터럽트의 주요 동작 원리
1. `wait()`, `sleep()`, `join()` 같은 **블로킹 메서드 실행 중 인터럽트가 발생하면**, 즉시 `InterruptedException`이 발생하고 블로킹 상태에서 해제된다.
2. `Selector.select()`, `InterruptibleChannel` 같은 **NIO 관련 작업 중이면**, 즉시 해제되고 인터럽트 상태가 설정된다.
3. 만약 **인터럽트할 수 없는 상태**라면, 단순히 `interrupted` 플래그만 `true`로 설정된다.

---

## 2. `Thread.interrupt()` 코드 분석
```java
public void interrupt() {
    if (this != Thread.currentThread()) {
        checkAccess(); // 보안 검사
        
        // NIO 작업 중인지 확인
        synchronized (interruptLock) {
            Interruptible b = nioBlocker;
            if (b != null) { // NIO 작업 중이면
                interrupted = true;
                interrupt0();  // 가상 머신(VM)에 인터럽트 요청 전달
                b.interrupt(this);  // NIO 차단 해제
                return;
            }
        }
    }
    
    // 일반적인 경우 인터럽트 플래그 설정
    interrupted = true;
    interrupt0();  // 가상 머신(VM)에 인터럽트 요청 전달
}
```

### 📌 주요 동작 정리
1. **자기 자신을 인터럽트하는 경우 (`this == Thread.currentThread()`)**
    - 보안 검사 없이 바로 `interrupted = true;` 설정 후 종료.
    - `interrupt0();`를 호출하여 VM에 인터럽트 요청 전달.

2. **다른 스레드를 인터럽트하는 경우 (`this != Thread.currentThread()`)**
    - `checkAccess();` 호출하여 보안 검사 수행.
    - 현재 스레드가 NIO 차단 작업 중인지 확인.
    - `nioBlocker != null`이라면, NIO 작업 해제 후 즉시 종료.
    - 그렇지 않다면, `interrupted = true;` 설정 후 `interrupt0();` 호출.

---

## 3. `interrupt()`의 실제 동작 예제
```java
public class InterruptExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("작업 수행 중...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("인터럽트 감지됨! 종료.");
                    return;
                }
            }
        });

        thread.start();
        Thread.sleep(3000); // 3초 후 인터럽트 실행
        thread.interrupt();
    }
}
```

### ✅ 실행 결과
```
작업 수행 중...
작업 수행 중...
작업 수행 중...
인터럽트 감지됨! 종료.
```
- `sleep()` 호출 중 `interrupt()`가 실행되면서 `InterruptedException` 발생 → 스레드 종료됨.

---

## 4. `interrupt()` 동작 방식 요약
| 상황 | 동작 |
|------|------|
| `sleep()`, `wait()`, `join()` 중 인터럽트 발생 | `InterruptedException` 발생 후 종료 |
| `Selector.select()`, `InterruptibleChannel` 중 인터럽트 발생 | 작업 즉시 해제됨 |
| 그 외의 경우 | `interrupted` 플래그만 `true`로 설정 |

✅ `interrupt()`는 **강제 종료가 아니라, 인터럽트 신호를 보내는 역할**만 수행한다.

👉 **다음 학습 추천:** `Thread.isInterrupted()`와 `Thread.interrupted()`의 차이를 살펴보자! 🚀

