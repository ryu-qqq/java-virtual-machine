# 🚀 JVM 클래스 로딩 메커니즘

## 📌 **클래스 로딩 개요**
JVM은 `.class` 파일을 로딩하여 실행할 때 **5단계 과정**을 거친다.

1️⃣ **로딩 (Loading)** - `.class` 파일을 읽고 JVM 메모리에 적재
2️⃣ **검증 (Verification)** - 클래스 파일이 유효한지 확인
3️⃣ **준비 (Preparation)** - `static` 필드에 대한 기본값 할당
4️⃣ **해석 (Resolution)** - 심볼릭 참조를 실제 메모리 참조로 변환
5️⃣ **초기화 (Initialization)** - `static` 블록 및 변수 초기화

이제 **로딩(Loading) 과정**을 깊이 파보자! 🔥

---

## 🚀 **1. 로딩 (Loading) – JVM이 .class 파일을 가져와 메서드 영역에 적재하는 과정**

### 📌 **Step 1: 바이너리 바이트 스트림을 가져옴**
✔️ JVM은 `.class` 파일을 읽어서 **이진 데이터(바이트 스트림)** 형태로 메모리에 적재한다.
✔️ 이진 데이터는 파일 시스템, JAR, 네트워크 등에서 가져올 수 있음.

🔹 **예제: 클래스 로더가 `Test.class` 파일을 읽음**
```java
public class Test {
    static int num = 10;
}
```
🚀 **이걸 바이너리 바이트 스트림으로 보면?**
```
CAFEBABE 00000034 001F0006 74657374 ...
```
✔️ 첫 4바이트 `CAFEBABE` → JVM이 **"이거 클래스 파일 맞다!"** 확인

📌 **즉, JVM이 .class 파일을 통째로 읽어서 "이진 데이터" 상태로 메모리에 적재한다.**
하지만 그대로 사용할 수 없기 때문에 **런타임 데이터 구조로 변환이 필요함!**

---

### 📌 **Step 2: 런타임 데이터 구조로 변환 (메서드 영역 저장)**
✔️ JVM은 바이트 스트림을 **클래스 정보가 담긴 데이터 구조**로 변환한다.
✔️ 이 변환된 데이터가 **메서드 영역(Method Area)** 에 저장됨.

🔹 **메서드 영역에 저장되는 정보**
- **클래스 이름** (`Test`)
- **필드 정보** (`static int num`)
- **메서드 정보** (없음)
- **상수 풀(Constant Pool)** ([10])

✔️ **이제 JVM이 `Test` 클래스에 대한 정보를 메서드 영역에서 빠르게 조회 가능!**
✔️ **클래스 정보를 따로 저장하는 이유?** → 메서드 호출, 필드 접근 등을 빠르게 처리하려고!

📌 **즉, JVM은 바이트 스트림을 해석해서 "런타임 데이터 구조"로 만든다!**

---

### 📌 **Step 3: 힙(Heap) 메모리에 `Class<?>` 객체 생성**
✔️ **애플리케이션이 이 클래스를 사용할 수 있어야 함!**
✔️ 그래서 JVM이 **"클래스 객체(Class Object)"** 를 힙(Heap)에 만든다.
✔️ **이 클래스 객체는 `java.lang.Class` 타입의 인스턴스!**
✔️ 애플리케이션은 이 객체를 통해 클래스 정보를 가져올 수 있음.

🔹 **예제: 힙 영역에 클래스 객체를 생성하면?**
```java
Class<?> clazz = Test.class;
```
📌 **이제 `clazz` 객체를 통해 "메서드 영역의 클래스 데이터"를 접근 가능!**
📌 즉, `clazz.getName()` 하면 `"Test"` 같은 정보 가져올 수 있음.

🚀 **이게 "애플리케이션이 메서드 영역 데이터를 활용할 수 있는 통로" 라는 뜻!**

---

## 🚀 **2. 검증 (Verification) – 클래스 파일이 안전한지 검사하는 단계**
JVM은 `.class` 파일이 신뢰할 수 있는지 확인하는 4단계 검증 과정을 거친다.

### ✅ **1. 파일 형식 검증 (File Format Verification) – 클래스 파일 구조 확인**
✔️ `.class` 파일이 올바른 포맷인지 확인 (`0xCAFEBABE` 있는지 체크)
✔️ 파일이 손상되었거나 조작되면 `ClassFormatError` 발생

### ✅ **2. 메타데이터 검증 (Metadata Verification) – 클래스 구조 확인**
✔️ `final` 클래스가 상속되지 않는지, `abstract` 클래스가 제대로 구현됐는지 확인
✔️ 인터페이스 메서드가 올바르게 구현됐는지 검사
✔️ 접근 제한자(`private`, `public`)가 규칙을 어기지 않는지 체크
✔️ 오류 발생 시 `VerifyError` 발생

### ✅ **3. 바이트코드 검증 (Bytecode Verification) – 실행 가능한 코드인지 확인**
✔️ JVM 명령어(Opcode)가 정상적으로 사용됐는지 확인
✔️ 로컬 변수 & 스택이 정상적으로 사용됐는지 체크
✔️ 메서드 호출 시 인자 개수 & 타입이 맞는지 검사
✔️ 오류 발생 시 `VerifyError` 발생

### ✅ **4. 심볼 참조 검증 (Symbolic Reference Verification) – 참조가 유효한지 확인**
✔️ 참조하는 클래스/메서드/필드가 실제로 존재하는지 확인
✔️ 없는 메서드를 호출하는 경우 `NoSuchMethodError` 발생
✔️ private 메서드를 외부 클래스에서 호출하면 `IllegalAccessError` 발생

---

## 🚀 **3. 준비 (Preparation) – static 필드 메모리 할당**
JVM의 **준비(Preparation) 단계**는 검증을 통과한 클래스의 **static 필드를 메모리에 할당하는 과정**이다.  
즉, **클래스의 static 변수가 메모리에 올라가지만, 값은 아직 설정되지 않는다!**

### 📌 **준비(Preparation) 단계에서 일어나는 일**
✔️ **클래스의 static 필드가 메모리에 생성됨**  
✔️ **static 필드는 기본값(0, null, false)으로 초기화됨**  
✔️ **이 단계에서는 static 블록이 실행되지 않음**

🔹 **예제: 준비 단계에서 static 변수의 초기 상태**
```java
class Test {
    static int x = 10;
    static String msg = "Hello";
}
```
🚀 **준비 단계에서 JVM이 하는 일**  
1️⃣ `static int x` → **메모리에 올라가지만 기본값 `0`**  
2️⃣ `static String msg` → **메모리에 올라가지만 기본값 `null`**  
✔️ `x = 10`과 `msg = "Hello"`는 아직 할당되지 않음!  
✔️ **이 값들은 초기화(Initialization) 단계에서 설정됨**

📌 **즉, JVM은 static 변수를 기본값으로 메모리에 올리고, 실제 값 할당은 초기화 단계에서 처리한다!**

---


## 🚀 **4. 해석 (Resolution) – 심볼릭 참조를 실제 참조로 변환**
JVM이 클래스 로딩을 하고 검증/준비까지 끝냈다면, 이제 **클래스 내 심볼릭 참조(Symbolic Reference)** 를 **실제 메모리 참조(Runtime Reference)** 로 변환해야 한다!

### 📌 **왜 필요한가?**
✔️ `.class` 파일 안에서는 **클래스, 필드, 메서드 참조가 심볼(문자열)로 저장됨.**
✔️ **JVM이 실제 메모리 주소를 모르기 때문에 실행 전에 변환해야 함.**

### ✅ **1. 클래스 또는 인터페이스 해석 (Class or Interface Resolution)**
📌 **클래스 이름을 심볼에서 실제 메모리의 클래스 객체로 변환하는 과정**
✔️ `.class` 파일에는 `"java/lang/String"` 같은 문자열로 클래스 참조가 저장됨.
✔️ JVM이 이걸 실제 **메모리의 Class 객체** 로 변환함.

🔹 **예제: 클래스 해석 과정**
```java
String s = new String("Hello");
```
🚀 **JVM이 실행하는 과정**
1️⃣ `"java/lang/String"` **심볼릭 참조를 찾음**  
2️⃣ **클래스를 로딩했는지 확인 → 없으면 로딩**  
3️⃣ **메서드 영역(Method Area)에서 실제 `String.class` 객체를 찾음**  
4️⃣ **변환 완료! 이제 String 객체를 생성할 수 있음**

---

### ✅ **2. 필드 해석 (Field Resolution)**
📌 **클래스가 참조하는 필드를 실제 메모리 위치로 변환하는 과정**
✔️ **클래스 내부에 선언된 필드는 메서드 영역(Method Area)에 저장됨.**
✔️ JVM이 필드의 심볼릭 참조를 실제 필드의 메모리 주소로 변환함.

🔹 **예제: 필드 해석 과정**
```java
class Test {
    static int x = 100;
}

public class Main {
    public static void main(String[] args) {
        System.out.println(Test.x);
    }
}
```
🚀 **JVM이 실행하는 과정**
1️⃣ `"Test.x"` **필드 참조를 찾음**  
2️⃣ **메서드 영역에서 `Test` 클래스의 필드 목록 검색**  
3️⃣ **"x" 필드의 실제 메모리 주소를 찾아 참조**  
4️⃣ **출력값 `100` 반환**

---

### ✅ **3. 메서드 해석 (Method Resolution)**
📌 **메서드 호출 시 심볼릭 참조를 실제 메서드 주소로 변환하는 과정**
✔️ **JVM은 클래스 내부의 메서드를 메서드 영역(Method Area)에 저장함.**
✔️ **메서드 호출 시 "이 메서드 어디 있냐?" 하고 실제 메모리 주소를 찾음.**

🔹 **예제: 메서드 해석 과정**
```java
class Test {
    void printMessage() {
        System.out.println("Hello, JVM!");
    }
}

public class Main {
    public static void main(String[] args) {
        Test t = new Test();
        t.printMessage();
    }
}
```
🚀 **JVM이 실행하는 과정**
1️⃣ `"Test.printMessage()"` **메서드 참조를 찾음**  
2️⃣ **Test 클래스의 메서드 테이블(Method Table) 검색**  
3️⃣ **"printMessage" 메서드의 실제 메모리 주소를 찾음**  
4️⃣ **변환 완료! 이제 메서드를 실행 가능**

---

### ✅ **4. 인터페이스 메서드 해석 (Interface Method Resolution)**
📌 **인터페이스의 메서드를 실제 구현된 메서드 주소로 변환하는 과정**
✔️ **인터페이스는 구현 클래스가 다를 수 있기 때문에 실행 시점에 해석해야 함.**
✔️ **JVM이 인터페이스의 심볼릭 참조를 실제 구현된 메서드로 연결함.**

🔹 **예제: 인터페이스 메서드 해석 과정**
```java
interface Speak {
    void say();
}

class Dog implements Speak {
    public void say() {
        System.out.println("멍멍!");
    }
}

public class Main {
    public static void main(String[] args) {
        Speak s = new Dog();
        s.say();
    }
}
```
🚀 **JVM이 실행하는 과정**
1️⃣ `"Speak.say()"` **메서드 참조를 찾음**  
2️⃣ **Speak는 인터페이스이므로 구현 클래스(Dog) 검색**  
3️⃣ **"say" 메서드가 Dog 클래스에서 구현됨 → Dog의 say() 주소를 참조**  
4️⃣ **"멍멍!" 출력!**

---

## 🎯 **JVM 해석(Resolution) 단계 요약**
| 해석 단계 | 역할 | 예제 |
|----------|----------------------|----------------------|
| **클래스 해석** | `"java/lang/String"` 같은 심볼을 `String.class` 로 변환 | `String s = new String("Hello");` |
| **필드 해석** | `"Test.x"` 같은 심볼을 실제 필드 메모리 위치로 변환 | `System.out.println(Test.x);` |
| **메서드 해석** | `"Test.printMessage()"` 같은 심볼을 실제 메서드 주소로 변환 | `t.printMessage();` |
| **인터페이스 메서드 해석** | `"Speak.say()"` 같은 심볼을 실제 구현 메서드로 연결 | `Speak s = new Dog(); s.say();` |


---

## 🚀 **5. 초기화 (Initialization) – static 변수 및 블록 실행**
JVM에서 **초기화(Initialization) 단계**는 클래스 로딩의 마지막 과정이다.  
이 단계에서 **static 필드가 최종 값으로 설정되고, static 블록이 실행됨.**

### 📌 **초기화 단계에서 일어나는 일**
✔️ **static 필드가 코드에서 정의된 값으로 변경됨.**  
✔️ **static 블록이 한 번만 실행됨.**  
✔️ **부모 클래스가 먼저 초기화된 후, 자식 클래스가 초기화됨.**

---

## ✅ **1. static 변수 초기화**
초기화 단계에서 **static 변수는 정의된 값으로 설정됨.**  
이전 준비 단계에서는 기본값(`0`, `null`, `false`)이었지만, 이제 코드에서 선언된 값이 할당됨.

🔹 **예제: static 변수 초기화**
```java
class Test {
    static int x = 10; // 초기화 단계에서 값 설정됨.
}
```
🚀 **JVM이 하는 일**  
1️⃣ `static int x` → 준비 단계에서는 `0`  
2️⃣ 초기화 단계에서 `x = 10;` 값 설정

✔️ **이제 `x`가 실제 값(10)으로 변경됨!**

---

## ✅ **2. static 블록 실행**
✔️ **static 블록은 클래스가 처음 로딩될 때 한 번만 실행됨.**  
✔️ **static 필드와 함께 실행되며, 실행 순서는 선언된 순서대로 진행됨.**

🔹 **예제: static 블록 실행**
```java
class Test {
    static int x = 10;

    static {
        System.out.println("클래스 초기화됨!");
        x = 20; // static 블록에서 값 변경 가능!
    }
}
```
🚀 **JVM이 하는 일**  
1️⃣ `static int x = 10;` (기본 할당)  
2️⃣ `static 블록 실행` → `"클래스 초기화됨!"` 출력  
3️⃣ `x = 20;` 값 변경

✔️ **static 블록이 실행되면서 x 값이 10 → 20으로 변경됨.**

---

## ✅ **3. 부모 클래스부터 초기화됨**
✔️ **JVM은 항상 부모 클래스를 먼저 초기화한 후 자식 클래스를 초기화함.**

🔹 **예제: 부모-자식 클래스 초기화 순서**
```java
class Parent {
    static {
        System.out.println("부모 클래스 초기화!");
    }
}

class Child extends Parent {
    static {
        System.out.println("자식 클래스 초기화!");
    }
}

public class Main {
    public static void main(String[] args) {
        Child c = new Child(); // 부모 → 자식 순으로 초기화됨.
    }
}
```
🚀 **출력 결과**
```
부모 클래스 초기화!
자식 클래스 초기화!
```
✔️ **부모 클래스가 먼저 초기화되고, 그다음 자식 클래스가 초기화됨.**  
✔️ **이는 JVM이 상속 관계를 올바르게 처리하기 위함!**

---

## 🎯 **초기화 단계 요약**
| 과정 | 설명 |
|------|----------------------|
| **static 변수 최종 값 설정** | 코드에서 정의된 값으로 변경됨 |
| **static 블록 실행** | 한 번만 실행되며, 변수 값을 변경할 수도 있음 |
| **부모 → 자식 순서로 초기화** | 항상 부모 클래스가 먼저 초기화됨 |

---