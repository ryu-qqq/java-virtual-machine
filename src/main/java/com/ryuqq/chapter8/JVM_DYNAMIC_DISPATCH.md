# JVM 동적 디스패치 (Dynamic Dispatch)

## 1. 동적 디스패치 개요
동적 디스패치(Dynamic Dispatch)란 **메서드 호출이 실행 시점(런타임)에 결정되는 방식**을 의미한다. 즉, **객체의 실제 타입을 기반으로 메서드가 선택됨**.

✅ **컴파일 타임에 호출될 메서드를 확정할 수 없음**  
✅ **메서드 오버라이딩(Method Overriding)이 대표적인 예**  
✅ **JVM에서 `invokevirtual`, `invokeinterface`를 사용하여 구현됨**  
✅ **객체의 vtable(가상 메서드 테이블)을 조회하여 실제 메서드를 찾음**

---

## 2. 동적 디스패치의 대표적인 예: 메서드 오버라이딩
### 📌 예제 코드
```java
class Parent {
    void sayHello() { System.out.println("Hello from Parent"); }
}

class Child extends Parent {
    void sayHello() { System.out.println("Hello from Child"); }
}

public class Test {
    public static void main(String[] args) {
        Parent obj = new Child(); // 업캐스팅
        obj.sayHello(); // Child의 메서드 실행 (invokevirtual)
    }
}
```

### 🔹 실행 과정
1. `Parent obj = new Child();` → **업캐스팅(Upcasting) 발생**
2. `obj.sayHello();` 호출 → **객체의 실제 타입(`Child`)을 확인해야 함**
3. `invokevirtual` 명령어 실행 → **JVM이 vtable을 확인하여 `Child.sayHello()`를 호출**

✅ **즉, Parent 타입으로 선언된 `obj`가 실제로 `Child` 객체를 가리키므로, 런타임에 `Child`의 `sayHello()`가 호출됨**

---

## 3. 동적 디스패치의 바이트코드 분석
### 📌 `javap -v Test.class` 실행 결과
```
0: aload_1       // obj 로드
1: invokevirtual #2  // Method Parent.sayHello:()V
```
### 🔹 바이트코드 해석
- `invokevirtual #2` → 런타임 상수 풀에서 `sayHello()`의 참조를 확인
- **JVM이 vtable을 조회하여 실제 실행할 메서드를 결정함**

✅ **즉, 컴파일 타임에는 `Parent.sayHello()`로 보이지만, 런타임에 `Child.sayHello()`를 호출함**

---

## 4. 동적 디스패치의 핵심: vtable(가상 메서드 테이블)
### 🔹 vtable(Virtual Method Table) 개념
- **JVM은 클래스별로 vtable(가상 메서드 테이블)을 생성**
- **각 클래스의 오버라이딩된 메서드의 실제 메모리 주소를 저장**
- **invokevirtual 실행 시, vtable을 조회하여 실제 실행할 메서드를 결정**

### 📌 vtable 구조 예제
```java
class Parent {
    void sayHello() { System.out.println("Parent Hello"); }
}

class Child extends Parent {
    void sayHello() { System.out.println("Child Hello"); }
}
```
#### **JVM 내부에서 생성되는 vtable**
```
Parent vtable:
----------------------------------
| sayHello -> Parent.sayHello()  |
----------------------------------

Child vtable:
----------------------------------
| sayHello -> Child.sayHello()   |
----------------------------------
```
✅ **JVM은 `obj.sayHello();` 호출 시, `obj`의 실제 타입을 기준으로 vtable을 조회하여 실행할 메서드를 결정함**

---

## 5. 인터페이스에서의 동적 디스패치: itable(인터페이스 테이블)
### 📌 예제 코드
```java
interface Speaker {
    void speak();
}

class Human implements Speaker {
    public void speak() { System.out.println("Hello, I'm a human."); }
}
```
### 🔹 실행 과정
1. `Speaker speaker = new Human();` → 업캐스팅 발생
2. `speaker.speak();` 호출 시 `invokeinterface` 실행
3. JVM은 **itable을 검색하여 `Human.speak()`를 호출**

✅ **인터페이스 메서드는 vtable이 아니라 itable(인터페이스 테이블)을 이용해 실제 구현된 메서드를 찾음**

#### **JVM 내부에서 생성되는 itable**
```
Human itable:
----------------------------------
| Speaker.speak() -> Human.speak() |
----------------------------------
```

✅ **JVM은 itable을 조회하여 `Human.speak()`를 실행함**

---

## 6. 동적 디스패치의 성능 최적화
### 🔹 성능 문제
- vtable 조회가 필요하기 때문에 **정적 디스패치보다 성능이 약간 떨어짐**
- JVM의 JIT(Just-In-Time) 컴파일러가 이를 최적화함

### 🔹 JVM의 최적화 기법
1. **인라이닝(Inlining) 최적화**
    - JIT 컴파일러가 **자주 호출되는 메서드를 인라이닝(코드를 복사해서 호출 비용을 줄임)**
    - 동적 디스패치로 인해 발생하는 성능 비용을 줄이는 핵심 기술

2. **CHA(Class Hierarchy Analysis, 클래스 계층 분석)**
    - 실행 중 클래스 계층 구조를 분석하여 **메서드가 오버라이딩되지 않았다고 판단되면 정적 디스패치로 변경**

✅ **즉, JVM은 실행 중에 동적 디스패치 비용을 최소화하기 위해 다양한 최적화 기법을 적용함**

---

## 🎯 동적 디스패치 최종 정리
✅ **메서드 호출이 실행 시점(런타임)에 결정됨**  
✅ **메서드 오버라이딩이 대표적인 예**  
✅ **JVM에서 `invokevirtual`, `invokeinterface`를 사용하여 구현됨**  
✅ **객체의 vtable(가상 메서드 테이블)을 조회하여 실제 메서드를 찾음**  
✅ **JIT 컴파일러는 인라이닝(Inlining) 최적화 등을 통해 성능을 최적화함**

---

