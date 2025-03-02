# JVM 정적 디스패치 (Static Dispatch)

## 1. 정적 디스패치 개요
정적 디스패치(Static Dispatch)란 **컴파일 타임에 메서드 호출이 결정되는 방식**을 의미한다. 즉, 실행 전에 어느 메서드가 호출될지 확정되며, 실행 중에 변경되지 않는다.

## 2. 정적 디스패치의 특징
✅ **컴파일 타임에 결정됨**
✅ **메서드 오버로딩(Method Overloading)이 대표적인 예**
✅ **JVM에서는 바이트코드에 메서드 호출이 명확하게 기록됨**
✅ **invoke 명령어 중 `invokestatic` 사용 (static 메서드 호출 시)**

## 3. 정적 디스패치의 대표적인 예: 메서드 오버로딩
### 📌 예제 코드
```java
class Example {
    void print(int a) { System.out.println("int"); }
    void print(double a) { System.out.println("double"); }
}

public class Test {
    public static void main(String[] args) {
        Example ex = new Example();
        ex.print(5);   // int
        ex.print(5.0); // double
    }
}
```
### 🔹 분석
- `print(int)`와 `print(double)`는 **메서드 오버로딩(Method Overloading)**
- **컴파일 타임에 어떤 메서드가 호출될지 결정됨**
- JVM 바이트코드에서 각각의 메서드 호출이 확정적으로 기록됨

## 4. 정적 디스패치의 바이트코드 분석
### 📌 `javap -c Test.class` 실행 결과
```
0: new           #2   // Example 객체 생성
3: dup
4: invokespecial #3   // Example 생성자 호출
7: astore_1
8: aload_1
9: iconst_5
10: invokevirtual #4  // Method Example.print:(I)V
13: aload_1
14: ldc2_w       #5   // double 리터럴 5.0
17: invokevirtual #6  // Method Example.print:(D)V
```
### 🔹 바이트코드 해석
- `invokevirtual #4` → `print(int)` 메서드 호출
- `invokevirtual #6` → `print(double)` 메서드 호출
- **즉, 컴파일 타임에 호출될 메서드가 결정되었기 때문에, 실행 중에는 변경되지 않음**

## 5. 정적 디스패치의 활용 사례
### 📌 1. 메서드 오버로딩
- 오버로딩된 메서드는 **정적 디스패치를 통해** 컴파일 타임에 호출이 확정됨

📌 **예제**
```java
class MathUtils {
    static int add(int a, int b) { return a + b; }
    static double add(double a, double b) { return a + b; }
}
```
바이트코드에서 `add(int, int)`와 `add(double, double)`이 각각 다르게 기록됨.

### 📌 2. `static` 메서드 호출
- `static` 메서드는 **클래스 로딩 시점에 메서드 주소가 결정되므로 정적 디스패치가 적용됨**

📌 **예제**
```java
class Utils {
    static void log(String message) {
        System.out.println("Log: " + message);
    }
}

public class Test {
    public static void main(String[] args) {
        Utils.log("Hello");
    }
}
```
#### 🔹 바이트코드 분석 (`javap -c Test.class`)
```
0: ldc           #2   // String "Hello"
3: invokestatic  #3   // Method Utils.log:(Ljava/lang/String;)V
```
✅ `invokestatic` → `log(String)` 메서드가 **컴파일 타임에 결정됨**

## 6. 정적 디스패치의 한계
✅ **정적 디스패치는 다형성을 지원하지 않음**
✅ **객체의 실제 타입과 무관하게 선언된 타입 기준으로 메서드 호출이 결정됨**
✅ **동적 디스패치(Dynamic Dispatch)를 사용해야 다형성을 구현할 수 있음**

## 🎯 정적 디스패치 최종 정리
✅ **메서드 호출이 컴파일 타임에 결정됨**
✅ **오버로딩된 메서드는 정적 디스패치를 사용**
✅ **static 메서드 호출은 `invokestatic` 명령어를 사용하여 정적 디스패치 처리됨**
✅ **JVM 바이트코드에서 호출될 메서드가 명확하게 기록됨**
✅ **다형성을 지원하지 않으며, 객체의 실제 타입에 따라 실행되는 오버라이딩은 동적 디스패치를 사용해야 함**

---

