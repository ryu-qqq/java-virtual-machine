# java.util.concurrent.Executor

## 1. Executor 란?
`Executor`는 **작업을 실행하는 방법과 실행할 작업을 분리하는 인터페이스**입니다. 즉, 직접 `Thread`를 생성하지 않고 작업만 제출하면 실행기가 알아서 실행하는 구조를 제공합니다.

### 특징
- **작업 실행을 직접 관리하지 않음**
    - 기존에는 `new Thread(runnable).start();` 방식으로 직접 스레드를 생성해야 했습니다.
    - `Executor`를 사용하면 **스레드 생성 및 관리 로직을 분리**할 수 있습니다.

- **비동기 실행을 강제하지 않음**
    - `Executor`는 반드시 새로운 스레드에서 실행될 필요가 없습니다.
    - 즉시 실행될 수도 있고, 특정 스레드 풀에서 실행될 수도 있습니다.

- **구체적인 실행 전략은 구현체가 결정**
    - `Executor` 자체는 `execute(Runnable command)` 메서드만 제공하며, 실제 실행 방식은 구현 클래스가 결정합니다.
    - 예를 들어, `ThreadPoolExecutor`는 스레드 풀을 활용하고, `ForkJoinPool`은 작업을 분할하여 실행합니다.

## 2. Executor의 동작 방식

### 인터페이스 정의
```java
public interface Executor {
    void execute(Runnable command);
}
```

### 동작 방식
1. `execute(Runnable command)` 메서드를 호출하면 **작업을 실행기에 제출**합니다.
2. 실행기는 작업을 실행할 **스레드를 결정**합니다.
    - 새로운 스레드를 만들거나
    - 기존의 스레드 풀을 사용하거나
    - 현재 실행 중인 스레드에서 즉시 실행할 수도 있습니다.
3. 실제 실행 방식은 실행기(`Executor`)의 구현체에 따라 달라집니다.

## 4. 결론
- `Executor`는 **작업 실행 로직을 관리하는 인터페이스**로, 실행 방식은 구현체가 결정합니다.
- `execute(Runnable command)`는 비동기 실행을 보장하지 않으며, 실행 방식은 다양할 수 있습니다.
- 일반적으로 직접 구현하기보다는 `ExecutorService`나 `ThreadPoolExecutor` 같은 **고급 실행기**를 사용하는 것이 좋습니다.

```java
public interface Executor {

    /**
     * 미래의 어느 시점에 주어진 명령을 실행합니다. 
     * 이 명령은 실행기 구현의 재량에 따라 새 스레드,
     * 풀링된 스레드 또는 호출 스레드에서 실행될 수 있습니다.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```

