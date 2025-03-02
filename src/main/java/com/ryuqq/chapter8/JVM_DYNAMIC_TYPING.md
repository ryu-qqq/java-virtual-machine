# JVM 동적 타입 언어 지원과 실행 엔진 분석

## 1. 동적 타입 언어 지원을 위한 `invokedynamic` 및 `java.lang.invoke`

### 🔹 기존 JVM의 정적 타입 한계

Java는 원래 정적 타입(Static Typing) 기반 언어로 설계되었음. 즉, **메서드 호출이 컴파일 타임에 결정**되고, 이후 변경이 불가능함.
하지만 **JavaScript, Groovy, Kotlin과 같은 동적 타입 언어**에서는 메서드 호출이 런타임에 결정될 수 있어야 함.

### 🔹 `invokedynamic`의 등장

🔥 `invokedynamic`은 **런타임에 메서드 참조를 변경할 수 있도록 설계된 JVM 명령어**이다.

#### ✅ 기존 메서드 호출 방식 (`invokevirtual` vs `invokedynamic` 비교)
| 호출 방식 | 호출 결정 시점 | 예제 | 동작 방식 |
|-----------|--------------|------|----------------|
| `invokevirtual` | **컴파일 타임** | `obj.method()` | vtable(가상 메서드 테이블) 조회 |
| `invokeinterface` | **컴파일 타임** | `List.add()` | itable(인터페이스 테이블) 조회 |
| `invokestatic` | **컴파일 타임** | `Math.abs()` | 바로 실행 |
| **`invokedynamic`** | **런타임** | `() -> System.out.println("Hello")` | 런타임에 메서드 결정 |

🔥 **즉, `invokedynamic`은 실행 중에 심벌 참조(Symbolic Reference)를 바꿀 수 있도록 설계됨.**

### 🔹 심벌 참조(Symbolic Reference)와 런타임 바인딩

✅ 기존 JVM 방식에서는 **컴파일 타임에 심벌 참조를 결정**했음 → 동적 변경이 불가능.
✅ `invokedynamic`을 사용하면 **런타임에서 동적으로 참조를 변경 가능**.

```java
import java.lang.invoke.*;

public class InvokeDynamicExample {
    public static void main(String[] args) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType type = MethodType.methodType(void.class);
        CallSite callSite = Bootstrap.bootstrap(lookup, "dynamicMethod", type);

        MethodHandle dynamicMethod = callSite.dynamicInvoker();
        dynamicMethod.invoke();
    }
}

class Bootstrap {
    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle target = lookup.findStatic(Target.class, "targetMethod", type);
        return new ConstantCallSite(target);
    }
}

class Target {
    public static void targetMethod() {
        System.out.println("Dynamic Method Executed!");
    }
}
```
✅ `Bootstrap.bootstrap()`이 실행될 때 `invokedynamic`이 실행되면서 **런타임에 메서드 참조를 결정**!

---

## 2. `MethodHandle`을 활용한 최적화

### 🔹 `MethodHandle`이란?
✅ `MethodHandle`은 기존의 리플렉션(`Reflection`)보다 **더 빠르고 최적화된 방식으로 메서드를 참조하고 실행할 수 있는 구조**이다.
✅ 기존의 `java.lang.reflect.Method`를 사용한 리플렉션 방식보다 **JIT 컴파일러의 최적화가 가능**하여 성능이 뛰어남.

```java
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleExample {
    public static void main(String[] args) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(void.class);
        MethodHandle methodHandle = lookup.findVirtual(Hello.class, "sayHello", methodType);

        Hello hello = new Hello();
        methodHandle.invoke(hello); // 런타임에 동적으로 메서드 실행
    }
}

class Hello {
    public void sayHello() {
        System.out.println("Hello, MethodHandle!");
    }
}
```

✅ **`MethodHandle`이 리플렉션보다 빠른 이유**
1. **JIT(Just-In-Time) 컴파일러 최적화 가능** → `MethodHandle`은 직접적인 메서드 참조를 사용하므로, `invokevirtual`과 유사한 방식으로 최적화 가능.
2. **동적 디스패치와 연계 가능** → `invokedynamic`과 함께 사용하면, 런타임에서 더 효율적인 메서드 참조를 유지할 수 있음.
3. **리플렉션보다 호출 오버헤드가 적음** → `Method.invoke()`는 내부적으로 보안 검사를 수행하지만, `MethodHandle`은 더 직접적인 실행 방식.

✅ **JIT 최적화가 적용될 수 있어 `invokedynamic`과 함께 활용될 경우, 성능 이점을 극대화할 수 있음.**

---

## 3. JVM 실행 엔진 및 스택 기반 아키텍처

### 🔹 JVM의 스택 기반 실행 방식

✅ JVM은 **스택 기반(Stack-Based) 아키텍처를 사용하여 바이트코드를 실행**
✅ **레지스터 기반(Register-Based) 방식보다 유연하지만 성능이 떨어질 수 있음**

```java
// 스택 기반 바이트코드 예제 (3 + 5 연산)
iconst_3   // 스택에 3 푸시
    iconst_5   // 스택에 5 푸시
iadd       // 스택에서 두 개의 값을 꺼내 더한 후 결과를 다시 푸시
    istore_1   // 결과를 로컬 변수에 저장
```

✅ **스택 기반 방식의 장점:**
- **플랫폼 독립성** → JVM이 어떤 환경에서도 실행 가능
- **바이트코드가 간결하고, 명령어 집합이 단순함**

✅ **단점:**
- **모든 연산을 스택에서 수행해야 하므로, 추가적인 명령어가 필요** (ex. `push`, `pop`)
- **스택 접근이 많아 성능이 낮아질 수 있음**

🔥 **JVM은 JIT(Just-In-Time) 컴파일러를 통해 스택 기반 코드를 레지스터 기반으로 변환하여 최적화!**

---

## 4. 정리: JVM의 동적 타입 언어 지원과 실행 엔진

- ✅ **`invokedynamic`을 통해 런타임에 메서드 참조를 변경 가능 → 동적 타입 언어 지원 강화**
- ✅ **`MethodHandle`을 통해 기존 리플렉션보다 빠른 메서드 실행 가능 → JIT 최적화 가능**
- ✅ **JVM은 기본적으로 스택 기반 아키텍처를 사용하지만, 실행 속도를 높이기 위해 JIT 컴파일러가 레지스터 기반으로 변환하여 실행**
- ✅ **JIT(Just-In-Time) 컴파일러가 `invokedynamic`으로 호출된 메서드도 최적화 가능 → 인라이닝을 적용할 수 있음!**
- ✅ **즉, 동적 타입 언어처럼 유연하면서도, 정적 타입 언어처럼 빠르게 실행 가능!**


