# 자바 메모리 모델(Java Memory Model, JMM)란

자바 메모리 모델(JMM)은 멀티스레드 환경에서 자바 프로그램이 메모리에 데이터를 읽고 쓰는 방식과 이를 통해 스레드 간의 통신이 이루어지는 규칙을 정의한 모델입니다. JMM은 CPU 캐시, 메인 메모리, 스레드 간의 데이터 일관성을 보장하는 데 중요한 역할을 합니다.



## 1. 왜 자바 메모리 모델이 필요한가?
멀티스레드 환경에서는 각 스레드가 CPU 레지스터나 캐시 같은 로컬 메모리에서 데이터를 읽고 쓰게 됩니다. 이 때문에 다른 스레드가 데이터의 최신 상태를 보장받지 못하는 문제가 발생할 수 있습니다.
JMM은 이러한 문제를 해결하기 위해 스레드 간 데이터 일관성을 보장하는 규칙을 정의합니다.


### 자바 메모리 모델의 핵심 개념:
1. 주요 메모리 영역
   - **주 메모리(Main Memory)**: 모든 스레드가 공유하는 메모리 영역입니다. 모든 객체와 변수는 기본적으로 주 메모리에 저장됩니다.
   - **스레드 로컬 메모리(Thread-local Memory)**: 각 스레드가 캐시처럼 사용하는 메모리 영역으로, 주 메모리에서 값을 읽어오거나 쓰기 전에 사용하는 중간 저장소입니다.

2. 가시성(Visibility)
   - 가시성 문제란 한 스레드가 변경한 값이 다른 스레드에 즉시 보이지 않는 문제를 의미합니다.
   - 예를 들어, 한 스레드가 변수 값을 변경했는데 다른 스레드가 이전 값을 읽는 경우가 발생할 수 있습니다.
   - 해결 방법: volatile, synchronized, final 키워드를 사용하여 가시성을 보장할 수 있습니다.

3. 재정렬(Reordering)
   - JVM과 CPU는 성능 최적화를 위해 명령어 순서를 변경할 수 있습니다. 이를 재정렬이라고 합니다.
   - 재정렬은 프로그램의 실행 결과가 동일하게 유지될 경우에만 허용되지만, 멀티스레드 환경에서는 예기치 않은 동작을 초래할 수 있습니다.
   - 해결 방법: synchronized 블록을 사용하여 명령어 순서를 강제하거나, volatile 키워드를 사용하여 재정렬을 방지합니다.

4. Happens-Before 관계
   - JMM은 Happens-Before 관계를 통해 작업의 실행 순서를 정의합니다.
   - Happens-Before 관계란 두 작업이 있을 때, 첫 번째 작업이 두 번째 작업보다 먼저 실행됨을 보장하는 규칙입니다.
     - 대표적인 Happens-Before 규칙:
        1. 동일 스레드 내의 순서:
           - 한 스레드 내에서 코드의 순서는 프로그램에 작성된 순서를 따릅니다.
        2. 락(lock) 규칙:
           - 락을 해제(unlock())한 동작은 같은 락을 획득(lock())하는 동작보다 먼저 발생합니다.
        3. volatile 변수 규칙:
           - 한 스레드가 volatile 변수를 쓰기한 후 다른 스레드가 읽기를 하면, 쓰기 작업이 먼저 발생합니다.

---

## 2. JMM의 키워드와 관련 동작

1. volatile
   - 용도: 변수의 가시성과 재정렬 방지. volatile로 선언된 변수는 모든 스레드가 최신 값을 읽을 수 있도록 주 메모리에서 직접 값을 읽고 씁니다.

2. synchronized
   - 용도: 스레드 간 동기화를 보장. 한 번에 하나의 스레드만 블록에 접근하도록 하여 데이터 일관성을 유지합니다.

3. final
   - 용도: 객체의 불변성을 보장. 객체 생성 시, 모든 final 필드는 다른 스레드에 안정적으로 보입니다.


예제 코드로 이해하기


가시성 문제

```java
class VisibilityExample {
    private static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (!stop) { // stop 값이 변경되었는지 계속 확인
            }
            System.out.println("Thread stopped.");
        });

        t1.start();
        Thread.sleep(1000);
        stop = true; // 다른 스레드에서 값을 변경
        System.out.println("Main thread updated stop to true.");
    }
}
// 결과: stop 값이 변경되었음에도 불구하고 t1 스레드가 종료되지 않을 수 있습니다. 이는 가시성 문제 때문입니다.
```

---

volatile로 해결

```java
class VisibilityExample {
private static volatile boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (!stop) {
            }
            System.out.println("Thread stopped.");
        });

        t1.start();
        Thread.sleep(1000);
        stop = true; // 다른 스레드에서 값을 변경
        System.out.println("Main thread updated stop to true.");
    }
}
// 결과: stop 값이 즉시 가시성이 보장되므로 t1 스레드가 정상적으로 종료됩니다.

```


## 3. 명령어 재정렬이란? 

명령어 재정렬(Reordering)은 JVM, 컴파일러, CPU가 프로그램의 성능을 최적화하기 위해 명령어 실행 순서를 변경하는 작업입니다.
단, 재정렬은 단일 스레드 관점에서 프로그램의 결과가 동일할 때만 수행됩니다.



```java
//예시: 재정렬 전후의 코드
// 원래 코드
int a = 10; // (1)
int b = a * 2; // (2)
flag = true; // (3)

// 재정렬된 코드
int a = 10; // (1)
flag = true; // (3)
int b = a * 2; // (2)

```

### volatile의 역할
volatile 키워드는 명령어 재정렬 방지를 위해 **메모리 배리어(Memory Barrier)** 라는 메커니즘을 사용합니다.

#### 메모리 배리어란?
- 메모리 배리어는 명령어 재정렬을 방지하는 명령어 경계선입니다.
- volatile 변수 앞뒤로 메모리 배리어를 삽입하여, 특정 명령어의 순서가 보장됩니다.


### volatile로 재정렬 방지 원리
재정렬 없는 volatile의 명령어 순서 보장

- 읽기 연산(volatile read): 이 변수 이후의 연산이 읽기보다 먼저 실행되지 않음.
- 쓰기 연산(volatile write): 이 변수 이전의 연산이 쓰기보다 늦게 실행되지 않음.

```java
volatile boolean flag = false;
int data = 0;

// Thread 1
data = 42;       // (1) 일반 쓰기
flag = true;     // (2) volatile 쓰기

    // Thread 2
    if (flag) {      // (3) volatile 읽기
    System.out.println(data); // (4) 일반 읽기
}
```

- Thread 1에서:
  1. data = 42는 flag = true보다 항상 먼저 실행됩니다.
  2. flag = true는 volatile 쓰기이므로, 이전 명령이 완료된 후 실행됩니다.
- Thread 2에서:
  1. flag를 읽는 순간 volatile 읽기로 Thread 1의 모든 쓰기 작업이 보장됩니다.
  2. 따라서 data = 42를 항상 최신 값으로 읽음.


### volatile로 명령어 재정렬 방지 실험

재정렬 문제 발생 가능 코드
```java
class Example {
    int x = 0;
    boolean flag = false;

    public void writer() {
        x = 42;       // (1)
        flag = true;  // (2)
    }

    public void reader() {
        if (flag) {      // (3)
            System.out.println(x); // (4)
        }
    }
}

```

- Thread 1에서:
    1. (1)에서 x를 42로 설정.
    2. (2)에서 flag를 true로 설정.
- Thread 2에서:
    1. (3)에서 flag를 true로 확인.
    2. (4)에서 x를 읽음.

재정렬 시 문제
재정렬이 발생하면 Thread 1의 실행 순서가 바뀔 수 있습니다:

```java
flag = true;  // (2)
x = 42;       // (1)
```
Thread 2가 flag == true를 확인해도 x는 여전히 0일 수 있습니다.


volatile 추가로 해결

재정렬이 발생하면 Thread 1의 실행 순서가 바뀔 수 있습니다:

```java
class Example {
    int x = 0;
    volatile boolean flag = false; // volatile 추가

    public void writer() {
        x = 42;       // (1)
        flag = true;  // (2) volatile 쓰기
    }

    public void reader() {
        if (flag) {      // (3) volatile 읽기
            System.out.println(x); // (4)
        }
    }
}

```
volatile 키워드를 사용하면 (1)이 항상 (2)보다 먼저 실행됩니다
따라서 Thread 2는 항상 최신 상태의 x 값을 보장받습니다.
