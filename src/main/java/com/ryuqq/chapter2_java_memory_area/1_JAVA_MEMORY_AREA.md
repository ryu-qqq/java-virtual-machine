# 자바 메모리 영역과 메모리 오버플로

## 런타임 데이터 영역


### 1. 프로그램 카운터 레지스터

PC 레지스터는 JVM이 프로그램 실행 중 현재 어떤 명령어를 실행하고 있는지 추적하는 작은 메모리 공간입니다.


- 책을 읽을 때 어디까지 읽었는지 책갈피를 끼워놓는 것처럼, PC 레지스터는 지금 실행 중인 명령어가 어디까지 진행되었는지 표시해줍니다.


### PC 레지스터의 특징
1. 쓰레드별로 하나씩 존재
   - JVM은 멀티스레딩을 지원하며, CPU 코어를 여러 스레드가 교대로 사용합니다.
   - 스레드가 전환될 때 멈춘 지점을 복원하기 위해 각 스레드마다 자기만의 책갈피(PC 레지스터)를 갖습니다.
   - PC 레지스터는 스레드 프라이빗 메모리입니다.

2. JVM 명령어 추적
   - 바이트코드로 컴파일된 명령어를 실행하면서, 다음 실행할 명령어의 위치를 기록합니다.

3. 네이티브 메서드 실행 시 비워짐
   - 네이티브 메서드(C/C++ 코드)가 실행되면, PC 레지스터는 값을 비워둡니다.

4. OutOfMemoryError 없음
   - PC 레지스터는 JVM에서 OutOfMemoryError 조건이 명시되지 않은 유일한 메모리 영역입니다.

예제 코드:
```java
public class PCExample {
    public static void main(String[] args) {
        int a = 10; // PC 레지스터: 'int a = 10;' 위치 기록
        int b = 20; // PC 레지스터: 'int b = 20;' 위치 기록
        int c = a + b; // PC 레지스터: 'int c = a + b;' 위치 기록
    }
}
```

--- 

### 2. 스택

자바 가상 머신 스택(VM 스택)은 자바 프로그램이 실행될 때 메서드 호출에 필요한 데이터를 저장하는 메모리 영역입니다.  
각 스레드마다 독립적으로 생성되고 관리됩니다.

- 메서드가 종료되면 해당 기록은 삭제됩니다.


### JVM 스택의 구조

- JVM 스택은 스택 프레임(Stack Frame)으로 구성됩니다.  
  각 스택 프레임은 아래 데이터를 포함합니다:

   1. **지역 변수 배열 (Local Variables)**
      - 메서드에서 사용하는 모든 지역 변수를 저장합니다.
   2. **오퍼랜드 스택 (Operand Stack)** 
      - 연산을 위한 스택 (JVM이 명령어 실행 시 사용하는 자료구조)
   3. **Frame Pointer**
      - 현재 실행 중인 프레임 위치
   3. **Return Address**
      - 메서드가 끝나고 돌아갈 명령어 주소.



### JVM 스택의 특징
1. **쓰레드별로 독립적으로 생성**
   - JVM 스택은 각 스레드마다 독립적으로 관리되며, 서로 간섭하지 않습니다.

2. **메서드 호출 시 생성, 종료 시 삭제**
   - 메서드가 호출되면 새로운 스택 프레임이 추가되고, 메서드가 종료되면 해당 스택 프레임은 제거됩니다.

3. **크기 제한**
   - JVM 스택은 크기가 제한되어 있으며, `-Xss` 옵션으로 설정 가능합니다.
   - 제한을 초과하면 StackOverflowError가 발생합니다.


### 실행 예제

```java
public class StackExample {
    public static void main(String[] args) {
        int result = add(5, 3); // 스택 프레임 생성 (main)
    }

    public static int add(int a, int b) {
        int sum = a + b; // 스택 프레임 생성 (add)
        return sum; // 스택 프레임 제거 (add 종료)
    }
}
```
#### 실행 순서:
1. `main` 메서드 호출
   - **스택 프레임 생성 (main)**: JVM은 `main` 메서드 호출에 필요한 정보를 저장하기 위해 스택 프레임을 생성합니다.

2. `add` 메서드 호출
   - **새 스택 프레임 생성 (add)**: `add` 메서드가 호출되면, 해당 메서드의 매개변수(`a`, `b`)와 지역 변수(`sum`)를 저장하기 위해 새로운 스택 프레임이 생성됩니다.

3. `add` 메서드 종료
   - **스택 프레임 제거 (add)**: `add` 메서드가 작업을 완료하고 값을 반환하면, 스택에서 `add` 메서드의 스택 프레임이 제거됩니다.

4. `main` 메서드 종료
   - **스택 프레임 제거 (main)**: `main` 메서드가 종료되면, `main` 메서드의 스택 프레임도 제거됩니다.


#### JVM 스택 관련 에러:

1. `StackOverflowError`
   - 너무 많은 메서드 호출(예: 무한 재귀 호출)로 스택이 꽉 찼을 때 발생.
```java
public class StackOverflowDemo {
    public static void main(String[] args) {
        recursiveMethod(); // 무한 재귀 호출
    }

    public static void recursiveMethod() {
        recursiveMethod();
    }
}

```
2. `OutOfMemoryError`
   - 스택을 확장하려는 시점에서 여유 메모리가 충분하지 않을때 발생


---

### 3. 네이티브 메서드 스택

네이티브 메서드 스택은 자바 스택과 매우 비슷한 역할을 한다. 비슷한 점이라면 가상 머신 스택은 자바 메서드(바이트코드)를 실행할 때 사용하고
네이티브 메서드 스택은 네이티브 메서드를 실행할 때 사용한다.


---


### 4. 힙

자바 힙은 JVM(Java Virtual Machine) 메모리 영역 중 하나로, 객체와 배열이 저장되는 공간입니다.
프로그램 실행 중에 동적으로 생성된 모든 객체는 힙에 저장됩니다.
힙은 JVM에서 가장 큰 메모리 영역으로, **가비지 컬렉션(Garbage Collection, GC)**에 의해 관리됩니다.

### 힙 특징

1. 모든 쓰레드가 공유
   - 자바 힙은 JVM의 모든 쓰레드가 공유하는 메모리 공간입니다.

2. 객체와 배열 저장
   - 클래스 설계도를 기반으로 생성된 객체와 배열이 저장됩니다.
   - 힙에 저장된 객체는 참조를 통해 접근됩니다.

3. 가비지 컬렉션
   - 힙은 자동 메모리 관리 시스템인 가비지 컬렉터에 의해 관리됩니다.
   - 더 이상 참조되지 않는 객체는 메모리에서 제거됩니다.

4. 크기 제한 가능
   - JVM 힙의 크기는 -Xms와 -Xmx 옵션으로 최소값과 최대값을 설정할 수 있습니다.


### 힙의 구조
1. Young Generation (YOUNG)
   - 새롭게 생성된 객체가 저장됩니다.
   - Young 영역은 다음 세 부분으로 나뉩니다
      - Eden Space: 객체가 처음 생성되는 영역.
      - Survivor Space: GC에서 살아남은 객체가 이동하는 영역.
   - Young 영역에서 객체가 일정 시간 동안 살아남으면 Old 영역으로 이동합니다.

2. Old Generation (OLD)
   - Young 영역에서 오래 살아남은 객체가 이동하는 공간.
   - 주로 장기간 사용하는 객체가 저장됩니다.



#### 힙 메모리 관련 에러:

1. `OutOfMemoryError:  Java heap space`
   - 객체를 너무 많이 생성하여 힙 메모리가 부족할 때 발생합니다.
   - 해결 방법
      - 힙 크기를 늘리기(-Xmx 옵션).

2. `OutOfMemoryError: GC overhead limit exceeded`
   - 가비지 컬렉션이 과도하게 실행되면서도 충분히 메모리를 확보하지 못할 때 발생합니다.
   - 해결 방법
      - GC 튜닝.

### 실행 예제

```java
import java.util.ArrayList;
import java.util.List;

public class HeapExample {
    public static void main(String[] args) {
        List<int[]> list = new ArrayList<>();
        while (true) {
            list.add(new int[1024 * 1024]); // 1MB 크기의 배열을 계속 생성
        }
    }
}

// 프로그램 실행 중, 힙 메모리가 부족해지면 OutOfMemoryError: Java heap space가 발생합니다.
```

--- 

### 5. 메서드 영역(Method Area)

1. 클래스 정보(Class Metadata)
    - 클래스의 이름, 접근 제한자, 부모 클래스, 인터페이스 등 메타데이터.

2. 상수(Constant Pool)
    - 리터럴 상수와 상수 풀(Constant Pool)에 저장된 값들.

3. Static 변수
    - 클래스 수준에서 선언된 static 변수.

4. 메서드 코드(Compiled Code)
    - 클래스의 메서드(바이트코드)와 생성자 코드.

5. 런타임 상수 풀(Runtime Constant Pool)
    - 컴파일된 클래스 파일에 있는 상수 풀을 런타임에 사용하는 형태로 변환한 것.

    
### 메서드 영역의 특징
1. **쓰레드가 공유**
    - 메서드 영역은 JVM의 모든 쓰레드가 공유하는 메모리 공간입니다

2. **클래스 로드 시 저장**
    - 클래스 로더에 의해 클래스가 처음 로드될 때 메서드 영역에 저장됩니다.

3. **가비지 컬렉션 가능**
    - 사용하지 않는 클래스 데이터를 가비지 컬렉션이 정리할 수 있습니다.

3. **자바 8부터 Metaspace로 대체**
    - 자바 8 이전에는 메서드 영역이 **Permanent Generation(PermGen)**에 포함되어 있었지만, 자바 8부터는 Metaspace로 변경되어 네이티브 메모리를 사용합니다.
    - PermGen은 크기 제한이 있고 클래스 메타 정보가 너무 많으면 OutOfMemoryError 가 발생하므로 유연하지 못했습니다. Metaspace는 네이티브 메모리를 사용해 유연하게 확장 가능합니다.

#### 메서드 영역 관련 에러:

1. `OutOfMemoryError: Metaspace`
    - 메서드 영역에 클래스나 메타데이터가 너무 많이 로드되면 발생합니다.
        - -XX:MaxMetaspaceSize 옵션으로 Metaspace 크기를 늘립니다.

```java
public class MethodAreaExample {
   static int staticVar = 100; // 메서드 영역에 저장

   public static void main(String[] args) {
      MethodAreaExample obj1 = new MethodAreaExample();
      MethodAreaExample obj2 = new MethodAreaExample();

      // static 변수는 모든 인스턴스가 공유
      System.out.println(obj1.staticVar); // 100
      System.out.println(obj2.staticVar); // 100

      obj1.staticVar = 200; // static 변수 값 변경
      System.out.println(obj2.staticVar); // 200
   }
}
```

### 자바 8 이후의 Metaspace

1. Metaspace란?
    - 자바 8부터 메서드 영역은 Metaspace라는 네이티브 메모리를 사용합니다.
    - Metaspace는 JVM 힙 외부에 저장되며, 기본 크기가 제한되어 있지 않습니다.


2. Metaspace 크기 조정 옵션
    - XX:MetaspaceSize: 초기 Metaspace 크기 설정.
    - XX:MaxMetaspaceSize: 최대 Metaspace 크기 설정.

---

###  6. 런타임 상수 풀(Runtime Constant Pool)

런타임 상수 풀은 JVM의 메서드 영역(Method Area) 안에 있는 특수한 메모리 영역으로,
클래스와 인터페이스의 상수 및 메서드/필드 참조 정보를 저장하고 관리합니다.

- 프로그램 실행 중 자주 참조되는 상수나 메서드 정보가 이곳에 저장됩니다.
- 필요할 때 빠르게 가져다 쓰고, 중복을 방지하기 위해 동일한 상수는 한 번만 저장됩니다.



### 런타임 상수 풀에 저장되는 내용

1. 상수(Constant Values)
    - 숫자, 문자열 리터럴 등 리터럴 상수 값.

2. 메서드 및 필드 참조(Method and Field References)
    - 클래스가 참조하는 메서드와 필드에 대한 정보.

3. 컴파일 타임 상수(Constant Expressions)
    - 컴파일 시에 계산된 상수 값.

4. 클래스/인터페이스 참조(Class or Interface References)
    - 클래스와 인터페이스의 이름 및 관련 정보.

   
### 런타임 상수 풀의 특징

1. 클래스 로드 시 생성
    - 클래스 파일에 있는 상수 풀(Constant Pool) 정보를 기반으로 런타임 상수 풀이 생성됩니다.

2. 중복 방지
    - 동일한 상수를 여러 번 저장하지 않습니다.

3. 쓰레드 간 공유
    - 런타임 상수 풀은 JVM의 메서드 영역에 있으므로, 모든 쓰레드가 공유합니다.

3. 메모리 최적화
    - 상수를 재사용함으로써 메모리를 효율적으로 관리합니다.

   
#### 런타임 상수 풀 관련 에러:

1. `OutOfMemoryError: Metaspace`
    - 런타임 상수 풀이 과도하게 커져 메모리가 부족하면 발생합니다.
    - 특히 많은 문자열 리터럴을 생성하거나 동적 클래스 로딩을 반복할 때 문제가 발생할 수 있습니다.


#### 런타임 상수 풀 과 String Pool:
- String Pool은 런타임 상수 풀의 일부로, 문자열 리터럴을 저장하는 공간입니다.
- 예를 들어, "Hello"라는 문자열 리터럴은 String Pool에 저장되고, 동일한 문자열은 재사용됩니다.

```java
public class StringPoolExample {
   public static void main(String[] args) {
      String s1 = "Hello"; // String Pool에 저장
      String s2 = "Hello"; // 기존 "Hello" 재사용

      System.out.println(s1 == s2); // true, 같은 객체 참조
   }
}

```

### 런타임 상수 풀과 클래스 파일 상수 풀의 차이

| **항목**         | **클래스 파일 상수 풀**          | **런타임 상수 풀**             |
|------------------|--------------------------------|--------------------------|
| **위치**         | `.class` 파일 내부             | JVM의 메서드 영역(Method Area) |
| **생성 시점**     | 컴파일 시                     | 클래스 로드 시  (동적)           |
| **내용**         | 모든 상수, 참조 정보            | 실제 사용되는 상수 및 참조 정보       |
| **가비지 컬렉션** | 없음                          | 가능                       |


### 실행 예제
```java
public class ConstantPoolExample {
   public static void main(String[] args) {
      String str1 = "Java"; // 런타임 상수 풀에 저장
      String str2 = "Java"; // 같은 문자열 재사용

      // 동적 생성된 문자열은 힙 메모리에 저장
      String str3 = new String("Java");

      System.out.println(str1 == str2); // true, 상수 풀에서 동일 객체
      System.out.println(str1 == str3); // false, 힙 메모리에 새로운 객체 생성
   }
}
```


---

### 7. 다이렉트 메모리(Direct Memory)

다이렉트 메모리는 JVM 힙 메모리 외부에 할당되는 네이티브 메모리입니다.
주로 NIO(New Input/Output) 버퍼를 통해, 바이트 데이터를 빠르게 처리하기 위해 사용됩니다.

- 데이터가 힙 메모리를 거치지 않고 직접 다이렉트 메모리에서 처리되기 때문에 더 빠릅니다.


### 다이렉트 메모리의 특징

1. 네이티브 메모리 사용
   - 다이렉트 메모리는 JVM 힙이 아니라, 운영 체제의 네이티브 메모리를 사용합니다.

2. 빠른 데이터 처리
   - 네트워크 I/O나 파일 I/O 같은 작업에서 데이터를 직접 읽고 쓰는 버퍼로 활용됩니다.
   - 중간에 힙 메모리로 복사하지 않으므로 성능이 향상됩니다.

3. ByteBuffer 클래스로 관리
   - 다이렉트 메모리는 자바의 java.nio.ByteBuffer 클래스에서 관리됩니다.
   - allocateDirect() 메서드를 사용해 다이렉트 버퍼를 생성합니다.

4. 가비지 컬렉션의 영향을 받지 않음
   - 다이렉트 메모리는 JVM 힙 메모리 외부에 있으므로, 가비지 컬렉션(GC)의 직접적인 영향을 받지 않습니다.

---

### 다이렉트 메모리 사용 예제

```java
import java.nio.ByteBuffer;

public class DirectMemoryExample {
   public static void main(String[] args) {
      // 다이렉트 버퍼 생성
      ByteBuffer buffer = ByteBuffer.allocateDirect(1024); // 1KB 크기

      // 데이터 쓰기
      buffer.put((byte) 1);
      buffer.put((byte) 2);

      // 읽기 준비
      buffer.flip();

      // 데이터 읽기
      System.out.println(buffer.get()); // 1
      System.out.println(buffer.get()); // 2
   }
}


```
### 다이렉트 메모리 사용 시 주의점

1. 적절한 크기 설정
   - 너무 작은 크기는 성능을 제한하고, 너무 큰 크기는 메모리 부족을 초래할 수 있습니다.

2. 명시적인 해제
   - 다이렉트 메모리는 JVM 힙처럼 자동 해제되지 않으므로, 필요 시 명시적으로 해제해야 합니다.
   - Cleaner API를 활용하거나 DirectByteBuffer의 내부 메커니즘을 사용합니다.





---


### "메서드 영역", "런타임 상수 풀", "스택"에 뭐가 저장되는지" 자세히 보기



```java
public class MemoryExample {
static int staticCounter = 0;                  // 🔸 static 변수 (메서드 영역)

    public static void main(String[] args) {
        int local = 10;                            // 🔹 스택 (지역 변수)
        String str1 = "Hello";                     // 🔹 str1: 스택 / "Hello": 상수 풀
        String str2 = new String("Hello");         // 🔹 str2: 스택 / 객체: 힙 / "Hello": 상수 풀
        String str3 = str2.intern();               // 🔹 상수 풀의 "Hello"를 참조

        staticCounter++;                           // 🔸 static 변수 접근

        int sum = add(local, 5);                   // 🔹 add 호출 → 스택에 프레임 생성
    }

    public static int add(int a, int b) {
        int result = a + b;                        // 🔹 스택 (a, b, result 전부 지역 변수)
        return result;
    }
}
```

```markdown
메서드 영역 (Method Area)
│
├── 클래스: MemoryExample
│   ├── staticCounter: 0 (static 변수)
│   ├── 메서드: main(), add()
│
├── 런타임 상수 풀 (Runtime Constant Pool)
│   ├── "Hello" (String 리터럴)
│   ├── 메서드 참조: add(int, int)
│   └── 클래스 참조: MemoryExample

힙 (Heap)
│
├── new String("Hello")
│   ├── 값: "Hello" (내부적으로 상수 풀 참조)
│
스택 (JVM Stack, 스레드별)
│
└── main() 프레임
├── local = 10
├── str1 → "Hello" (상수 풀 참조)
├── str2 → new String("Hello") (힙 참조)
├── str3 → "Hello" (상수 풀 참조)
├── sum → 15
└── add() 프레임
├── a = 10
├── b = 5
├── result = 15
```