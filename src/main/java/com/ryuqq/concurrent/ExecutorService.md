# java.util.concurrent.ExecutorService

## 1. ExecutorService 란?

# ExecutorService 인터페이스 명세 정리

## 1. ExecutorService란?
`ExecutorService`는 `Executor` 인터페이스를 확장하여, 실행 중인 작업을 보다 효과적으로 **관리 및 제어**할 수 있도록 설계된 인터페이스이다.

### 특징
- **비동기 작업의 실행과 관리 기능 제공**
- **작업 종료 및 취소 기능 포함**
- **작업 결과를 추적할 수 있는 `Future` 객체 반환 가능**
- **여러 개의 작업을 동시에 실행하고 관리할 수 있는 메서드 제공**

## 2. 주요 메서드 명세

### 🟢 1) 실행 관련 메서드
| 메서드 | 설명 |
|--------|------|
| `submit(Runnable task)` | 주어진 `Runnable`을 실행하고, 완료 상태를 추적하는 `Future<?>`를 반환 (결과값 없음) |
| `submit(Callable<T> task)` | 주어진 `Callable<T>`을 실행하고, 결과를 반환하는 `Future<T>` 객체를 반환 |
| `submit(Runnable task, T result)` | `Runnable`을 실행한 후, 미리 지정한 `result`를 반환하는 `Future<T>`를 반환 |
| `invokeAll(Collection<? extends Callable<T>> tasks)` | 여러 개의 `Callable` 작업을 실행하고 **모든 작업이 끝날 때까지 대기**, `Future<T>` 리스트 반환 |
| `invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)` | 지정된 시간 내에 실행이 완료된 작업의 결과만 반환하며, 시간이 초과되면 나머지 작업을 취소 |
| `invokeAny(Collection<? extends Callable<T>> tasks)` | 여러 개의 `Callable` 중 가장 먼저 끝난 작업의 결과만 반환하고, 나머지는 취소 |
| `invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)` | 지정된 시간 내에 가장 먼저 끝난 작업의 결과만 반환, 시간이 초과되면 `TimeoutException` 발생 |

### 🟠 2) 종료 관련 메서드
| 메서드 | 설명 |
|--------|------|
| `shutdown()` | **새로운 작업을 받지 않지만, 대기 중인 작업은 계속 실행됨** (Graceful Shutdown) |
| `shutdownNow()` | **대기 중인 작업을 취소하고, 실행 중인 작업을 강제 종료** (Interrupt 발생) |
| `isShutdown()` | `shutdown()` 또는 `shutdownNow()`가 호출되었는지 여부를 반환 |
| `isTerminated()` | 모든 작업이 완료되고, 실행기가 종료되었는지 여부를 반환 |
| `awaitTermination(long timeout, TimeUnit unit)` | 모든 작업이 종료될 때까지 대기, 시간 초과 시 `false` 반환 |

### 🟡 3) `Future`를 활용한 결과 관리 메서드
| 메서드 | 설명 |
|--------|------|
| `submit(Runnable task)` | 실행 후, `Future<?>` 반환 (결과값 없음) |
| `submit(Callable<T> task)` | 실행 후, `Future<T>` 반환 (결과값 있음) |


## 3. `ExecutorService`의 주요 특징 정리
1. **비동기 작업 관리** → `submit()`을 통해 실행한 작업의 상태를 `Future`로 추적 가능
2. **병렬 작업 실행** → `invokeAll()`, `invokeAny()`를 통해 다중 작업 실행 가능
3. **우아한 종료(Graceful Shutdown) 지원** → `shutdown()`을 호출하여 정상 종료
4. **강제 종료 지원** → `shutdownNow()`를 호출하여 강제 종료 (단, 즉시 종료는 보장되지 않음)

👉 `ExecutorService`는 단순한 작업 실행기가 아닌, **스레드 풀을 기반으로 동작하는 고급 실행기 인터페이스**이다.




