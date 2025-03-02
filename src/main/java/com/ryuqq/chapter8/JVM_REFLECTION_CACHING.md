# JVM 리플렉션 캐싱과 스프링 프록시 최적화

## 1. JVM에서 스프링의 리플렉션 캐싱이 저장되는 위치

### 🔹 JVM 메모리 구조에서 캐시어 목록은 Heap에 저장된다
| **메모리 영역** | **설명** | **모든 캐시 데이터의 저장 위치** |
|--------------|------------------|----------------------|
| **Method Area (메서드 영역)** | 클래스의 배열이나 메서드 정보 저장 | ❌ (메서드 정보만 저장) |
| **Heap (히프 영역)** | 엔트리토트 및 캐시 저장 | ✅ **스프링의 모든 캐시 데이터가 저장된다.** |
| **Stack (스탑 영역)** | 실행 중인 메서드의 직업 변수 저장 | ❌ (캐시는 저장되지 않음) |

🔹 이렇\ac8c **스프링의 캐시에 저장된 데이터는 한 개의 JVM 에서만 유지되며, 같은 구현이 호출되지 않은 서버에서는 캐시가 저장되지 않는다.**

---

## 2. 스프링의 프록시 생성 방식과 차이점

### 🔹 JDK 동적 프록시 vs CGLIB 프록시
| **프록시 방식** | **사용 조건** | **기술 방식** | **JVM 동작 방식** |
|--------------|------------|-----------|--------------|
| **JDK 동적 프록시** | 인터페이스가 존재할 경우 | `java.lang.reflect.Proxy` | 리플렉션(`Method.invoke()`) 사용 |
| **CGLIB 프록시** | 클래스 기반(인터페이스가 없는 경우) | 바이트코드 조작 | `invokestatic`으로 직접 메서드 호출 |

✅ **인터페이스 기반이면 JDK 동적 프록시를 사용하고, 클래스 기반이면 CGLIB을 사용한다.**

✅ **JDK 동적 프록시는 `Method.invoke()`를 사용하기 때문에 성능이 낮으며, 스프링은 이를 캐싱하여 최적화한다.**

✅ **CGLIB 프록시는 바이트코드를 조작하여 실행하기 때문에 리플렉션 없이 동작하여 성능이 뛰어나다.**

---

## 3. JDK 동적 프록시의 성능 최적화 방법
### 🔹 메서드 캐싱을 통한 최적화
✅ **스프링은 한 번 찾은 메서드를 캐싱하여 `Method.invoke()` 호출을 최소화한다.**

#### **LRU(Least Recently Used) 캐시 적용**
```java
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```
✅ **캐싱된 메서드는 LRU 방식으로 오래된 데이터를 자동으로 삭제하여 메모리 사용량을 관리한다.**

#### **SoftReference를 이용한 GC 우선 제거**
✅ **JVM의 가비지 컬렉터가 메모리가 부족하면 캐싱된 메서드를 우선 삭제하도록 설정 가능**
```java
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

class SoftReferenceCache {
    private static final ConcurrentHashMap<String, SoftReference<Method>> methodCache = new ConcurrentHashMap<>();

    public static Object invokeMethod(Object target, String methodName, Object... args) throws Exception {
        SoftReference<Method> methodRef = methodCache.get(methodName);
        Method method = (methodRef != null) ? methodRef.get() : null;

        if (method == null) {
            method = target.getClass().getMethod(methodName);
            methodCache.put(methodName, new SoftReference<>(method));
        }

        return method.invoke(target, args);
    }
}
```

✅ **이 방법을 사용하면, GC가 필요할 때 캐싱된 데이터를 자동으로 제거하여 메모리 부하를 방지할 수 있다.**

---

## 4. Prometheus + Grafana를 이용한 JVM 캐시 모니터링
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```
✅ **Prometheus가 JVM 메트릭을 수집하면, Grafana에서 캐싱된 메서드 정보 및 메모리 사용량을 실시간으로 모니터링할 수 있다.**

