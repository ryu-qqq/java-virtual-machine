# 스레드 안전성과 락 최적화

## 🔥 1. 스레드 안전성(Thread Safety)이란?
✅ **멀티스레드 환경에서 여러 스레드가 동시에 접근해도 프로그램이 예상대로 동작하는 능력**
✅ **데이터 손상, 레이스 컨디션(Race Condition), 데드락(Deadlock) 등의 문제를 방지하기 위한 개념**
✅ **자바에서 스레드 안전성을 보장하는 방법에는 여러 가지가 있음**

---

## 🔥 2. 스레드 안전성을 구현하는 방법

### **🚀 1. 상호배제 동기화(Mutual Exclusion Synchronization)**
✅ **공유 자원에 하나의 스레드만 접근할 수 있도록 제어하는 방식**
✅ `synchronized`, `ReentrantLock`, `StampedLock`을 활용하여 구현

```java
public class SafeCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}
```
✅ **락을 걸어 하나의 스레드만 접근할 수 있도록 보장**
✅ **하지만 락을 걸면 다른 스레드가 대기해야 하므로 성능 저하가 발생할 수 있음**

---

### **🚀 2. 논블로킹 동기화(Non-Blocking Synchronization)**
✅ **락을 사용하지 않고 동기화 문제를 해결하는 방식**
✅ `CAS(Compare-And-Swap)`, `Atomic` 클래스를 활용하여 구현
✅ **CAS를 이용하면 락 없이도 안전한 동시성 보장이 가능하며, 성능이 더 뛰어남**

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet();
    }
}
```
✅ **CAS를 활용하면 스레드 충돌을 줄일 수 있지만, 경쟁이 심할 경우 성능이 저하될 수도 있음**

---

### **🚀 3. 동기화가 필요 없는 메커니즘(Lock-Free Mechanisms)**
✅ **스레드 간 충돌을 방지하면서도 동기화 없이 안전하게 데이터를 관리하는 방법**
✅ 대표적인 방법: `ThreadLocal`을 사용하여 각 스레드가 독립적인 변수를 가지도록 구현

```java
public class ThreadLocalExample {
    private static final ThreadLocal<Integer> threadLocalCount = ThreadLocal.withInitial(() -> 0);

    public void increment() {
        threadLocalCount.set(threadLocalCount.get() + 1);
    }
}
```
✅ **각 스레드마다 독립적인 변수를 유지하여 동기화 없이 안전하게 사용 가능**
✅ **하지만 ThreadLocal은 메모리 사용량이 증가할 수 있으므로, 사용 후 반드시 정리해야 함**

---

## 🔥 3. 락 최적화 기법 (Lock Optimization)
✅ **락을 최소한으로 사용하여 성능을 극대화하는 기법**

### **🚀 1. 락 확장(Lock Coarsening)**
✅ **짧은 범위의 여러 개의 락을 하나로 확장하여 락 횟수를 줄이는 방식**

```java
public class LockCoarseningExample {
    private final Object lock = new Object();
    private int count;

    public void method() {
        synchronized (lock) {
            count++;
            count++;
            count++;
        }
    }
}
```
✅ **락을 여러 번 거는 대신 한 번만 걸어서 성능 최적화**

---

### **🚀 2. 락 제거(Lock Elision)**
✅ **JIT 컴파일러가 분석하여 불필요한 락을 제거하는 최적화 기법**
✅ **예를 들어, 특정 객체가 한 개의 스레드에서만 사용된다면 락이 필요 없음**

```java
public class LockElisionExample {
    public String concatStrings(String a, String b) {
        StringBuilder sb = new StringBuilder(); // JIT이 락 제거 가능
        sb.append(a);
        sb.append(b);
        return sb.toString();
    }
}
```
✅ **JVM이 내부적으로 락이 필요 없다고 판단하면 제거함**

---

### **🚀 3. 경량 락(Lightweight Locking)**
✅ **락 경쟁이 거의 없는 경우 `synchronized` 블록의 성능을 최적화하기 위해 사용됨**
✅ **객체의 마크워드(Mark Word)를 사용하여 락을 관리하며, 경쟁이 없는 경우 빠르게 락을 획득하고 해제**
✅ **경량 락의 동작 원리**
1. **스레드가 `synchronized` 블록에 진입하면, 객체의 마크워드를 현재 스레드의 스택에 복사**
2. **CAS(Compare-And-Swap) 연산을 사용하여 객체의 마크워드를 변경하며, 다른 스레드가 락을 시도하지 않으면 빠르게 진행**
3. **다른 스레드가 경쟁하면 경량 락이 중단되고 일반 락(Heavyweight Lock)으로 전환됨**

✅ **즉, 락 경쟁이 없는 경우 성능을 높이고, 경쟁이 발생하면 일반 락으로 전환하여 안정성을 유지**

---

### **🚀 4. 편향 락(Biased Locking)**
✅ **한 개의 스레드가 지속적으로 특정 객체에 접근하는 경우 불필요한 동기화 비용을 제거하기 위한 최적화 기법**
✅ **JVM이 특정 객체의 락을 단일 스레드에 "편향(Biased)"시키고, 다른 스레드가 접근할 때까지 락을 유지**
✅ **편향 락의 동작 원리**
1. **JVM이 객체의 마크워드(Mark Word)를 확인하여 특정 스레드에게 편향된 상태인지 판단**
2. **편향된 스레드가 락을 요청하면 락 없이 접근 가능 (락 해제 비용 절감)**
3. **다른 스레드가 해당 객체에 접근하려고 하면 편향 상태를 해제하고 경량 락 또는 일반 락으로 전환**

✅ **즉, 편향 락은 멀티스레드 환경에서도 특정 객체가 주로 하나의 스레드에서 사용된다면 락을 생략하여 성능을 최적화함**

---

## 🔥 4. 최종 정리
✅ **스레드 안전성을 보장하는 3가지 주요 방법:**
1. **상호배제 동기화** → `synchronized`, `ReentrantLock`, `StampedLock`을 사용하여 하나의 스레드만 접근 가능하게 만듦
2. **논블로킹 동기화** → `CAS(Compare-And-Swap)`, `Atomic` 클래스를 활용하여 락 없이 동기화 처리
3. **동기화 없는 메커니즘** → `ThreadLocal`을 사용하여 스레드별 독립적인 변수를 유지

✅ **락 최적화 기법:**
1. **락 확장(Lock Coarsening)** → 여러 개의 작은 락을 하나로 합쳐 락 비용 줄이기
2. **락 제거(Lock Elision)** → JIT 컴파일러가 불필요한 락을 자동으로 제거
3. **경량 락** → 마크워드를 이용한 빠른 락 획득
4. **편향 락** → 특정 스레드가 지속적으로 접근하는 경우 락을 생략하여 성능 향상


