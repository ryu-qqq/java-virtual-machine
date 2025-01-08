# 자바 메모리 영역과 메모리 오버플로

## 1. 프로그램 카운터(PC 레지스터)

PC 레지스터는 JVM이 프로그램 실행 중 현재 어떤 명령어를 실행하고 있는지 추적하는 작은 메모리 공간입니다.

### 비유: "책갈피"
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

## 2. 자바 가상 머신 스택

자바 가상 머신 스택(VM 스택)은 자바 프로그램이 실행될 때 메서드 호출에 필요한 데이터를 저장하는 메모리 영역입니다.  
각 스레드마다 독립적으로 생성되고 관리됩니다.

### 비유: "작업 기록장"
- 사람이 어떤 작업을 할 때 필요한 메모를 기록하고, 완료되면 지우는 것처럼, JVM 스택도 메서드 실행에 필요한 정보를 기록합니다.
- 메서드가 종료되면 해당 기록은 삭제됩니다.

---

### JVM 스택의 구조

- JVM 스택은 스택 프레임(Stack Frame)으로 구성됩니다.  
  각 스택 프레임은 아래 데이터를 포함합니다:

    1. **지역 변수 배열 (Local Variables)**
        - 메서드에서 사용하는 모든 지역 변수를 저장합니다.
    2. **오퍼랜드 스택 (Operand Stack)**
        - 연산에 필요한 값들을 일시적으로 저장합니다.
    3. **프레임 데이터 (Frame Data)**
        - 현재 실행 중인 메서드에 대한 정보(리턴 주소, 예외 처리 정보 등).

---

### JVM 스택의 특징
1. **쓰레드별로 독립적으로 생성**
    - JVM 스택은 각 스레드마다 독립적으로 관리되며, 서로 간섭하지 않습니다.

2. **메서드 호출 시 생성, 종료 시 삭제**
    - 메서드가 호출되면 새로운 스택 프레임이 추가되고, 메서드가 종료되면 해당 스택 프레임은 제거됩니다.

3. **크기 제한**
    - JVM 스택은 크기가 제한되어 있으며, `-Xss` 옵션으로 설정 가능합니다.
    - 제한을 초과하면 StackOverflowError가 발생합니다.

---

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

## 3. 네이티브 메서드 스택

네이티브 메서드 스택은 자바 스택과 매우 비슷한 역할을 한다. 비슷한 점이라면 가상 머신 스택은 자바 메서드(바이트코드)를 실행할 때 사용하고
네이티브 메서드 스택은 네이티브 메서드를 실행할 때 사용한다.

---

## 4. 자바 힙

자바 힙은 JVM(Java Virtual Machine) 메모리 영역 중 하나로, 객체와 배열이 저장되는 공간입니다.
프로그램 실행 중에 동적으로 생성된 모든 객체는 힙에 저장됩니다.
힙은 JVM에서 가장 큰 메모리 영역으로, **가비지 컬렉션(Garbage Collection, GC)**에 의해 관리됩니다.


### 비유: "작업대"
- 프로그램에서 필요한 물건(객체)을 작업대에서 만들어서 사용, 필요 없는 물건(더 이상 참조되지 않는 객체)은 가비지 컬렉션(GC)에 의해 정리

### PC 레지스터의 특징
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



## 5. 메서드 영역(Method Area)

메서드 영역은 JVM(Java Virtual Machine) 메모리의 한 부분으로, 클래스 수준의 정보와 공유 데이터가 저장되는 공간입니다.
프로그램 실행 중에 **클래스 로더(Class Loader)**에 의해 로드된 클래스 관련 데이터를 관리합니다.

### 비유: "공용 창고"
- 프로그램에서 필요한 물건(객체)을 작업대에서 만들어서 사용, 필요 없는 물건(더 이상 참조되지 않는 객체)은 가비지 컬렉션(GC)에 의해 정리

---

### 메서드 영역에 저장되는 내용

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

---

### 메서드 영역의 특징
1. **쓰레드가 공유**
   - 메서드 영역은 JVM의 모든 쓰레드가 공유하는 메모리 공간입니다.

2. **클래스 로드 시 저장**
   - 클래스 로더에 의해 클래스가 처음 로드될 때 메서드 영역에 저장됩니다.

3. **가비지 컬렉션 가능**
   - 사용하지 않는 클래스 데이터를 가비지 컬렉션이 정리할 수 있습니다.

3. **자바 8부터 Metaspace로 대체**
   - 자바 8 이전에는 메서드 영역이 **Permanent Generation(PermGen)**에 포함되어 있었지만, 자바 8부터는 Metaspace로 변경되어 네이티브 메모리를 사용합니다.

---


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




## 5. 런타임 상수 풀(Runtime Constant Pool)

런타임 상수 풀은 JVM의 메서드 영역(Method Area) 안에 있는 특수한 메모리 영역으로,
클래스와 인터페이스의 상수 및 메서드/필드 참조 정보를 저장하고 관리합니다.

### 비유: "도서관의 참고 자료실"
- 프로그램 실행 중 자주 참조되는 상수나 메서드 정보가 이곳에 저장됩니다.
- 필요할 때 빠르게 가져다 쓰고, 중복을 방지하기 위해 동일한 상수는 한 번만 저장됩니다.

---

### 런타임 상수 풀에 저장되는 내용

1. 상수(Constant Values)
   - 숫자, 문자열 리터럴 등 리터럴 상수 값.

2. 메서드 및 필드 참조(Method and Field References)
   - 클래스가 참조하는 메서드와 필드에 대한 정보.

3. 컴파일 타임 상수(Constant Expressions)
   - 컴파일 시에 계산된 상수 값.

4. 클래스/인터페이스 참조(Class or Interface References)
   - 클래스와 인터페이스의 이름 및 관련 정보.

---

### 런타임 상수 풀의 특징

1. 클래스 로드 시 생성
   - 클래스 파일에 있는 상수 풀(Constant Pool) 정보를 기반으로 런타임 상수 풀이 생성됩니다.

2. 중복 방지
   - 동일한 상수를 여러 번 저장하지 않습니다.

3. 쓰레드 간 공유
   - 런타임 상수 풀은 JVM의 메서드 영역에 있으므로, 모든 쓰레드가 공유합니다.

3. 메모리 최적화
   - 상수를 재사용함으로써 메모리를 효율적으로 관리합니다.

---


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

## 6. 다이렉트 메모리(Direct Memory)

다이렉트 메모리는 JVM 힙 메모리 외부에 할당되는 네이티브 메모리입니다.
주로 NIO(New Input/Output) 버퍼를 통해, 바이트 데이터를 빠르게 처리하기 위해 사용됩니다.

### 비유: "유튜브 프리미엄"
- 다이렉트 메모리는 광고 없이 바로 접근할 수 있는 빠른 유튜브 프리미엄 같은 역할을 합니다.
- 데이터가 힙 메모리를 거치지 않고 직접 다이렉트 메모리에서 처리되기 때문에 더 빠릅니다.

---

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



# 가상 머신에서의 객체 들여다보기
객체 생성 과정 (메모리 관점)
JVM에서 객체가 생성되는 과정은 **메모리의 힙(Heap)**과 스택(Stack) 영역의 협력을 통해 이루어집니다.
객체는 힙에 생성되지만, 메모리의 다양한 영역에서 이 객체와 관련된 데이터를 관리합니다.

1. 객체 생성의 기본 개념
   - 힙(Heap) 영역: 객체의 실제 데이터가 저장되는 공간.
   - 스택(Stack) 영역: 객체의 참조(Reference)가 저장되는 공간.
   - 메서드(Method) 영역: 객체를 생성하기 위한 클래스 정보(필드, 메서드, 상수 등)가 저장된 공간.


2. 객체 생성 과정
   - 힙 메모리 할당
     - new 키워드가 호출되면, JVM은 힙에서 객체를 저장할 메모리를 확보합니다.
     - 메모리 공간이 확보되면, 객체의 기본 값(필드의 초기화 값)이 설정됩니다.
       예: 숫자형은 0, 참조형은 null로 초기화.
   - 객체 참조 설정
     - 힙에 생성된 객체의 주소(reference)는 스택 프레임에 저장됩니다.
     - 스택에 저장된 참조를 통해 객체를 사용할 수 있습니다.
   - 필드와 메서드 연결
     - JVM은 메서드 영역에 있는 클래스 정보를 확인하여, 해당 객체의 필드와 메서드에 대한 정보를 연결합니다.
   - 초기화 코드 실행
     - 필드의 초기화 값 설정 → 생성자 호출 → 객체 완성.
     - 이 과정에서 필요한 데이터는 힙과 스택 사이에서 주고받습니다.

3. 객체의 메모리 구조
   - 헤더(Header)
     - 객체 관리에 필요한 메타데이터가 저장되는 부분.
     - JVM마다 다르지만 일반적으로 다음이 포함됩니다:
       - Mark Word: 객체 상태(해시값, 잠금 정보 등).
       - 클래스 포인터: 객체의 클래스 정보에 대한 참조.
   - 인스턴스 데이터(Instance Data)
     - 객체의 필드 값이 저장되는 영역.
   -  패딩(Padding)
     - JVM은 메모리 정렬을 위해 패딩을 추가할 수 있습니다.


## 객체 생성 과정에서의 힙 메모리 할당

### 스레드 로컬 할당 버퍼(TLAB: Thread-Local Allocation Buffer)란?

**스레드 로컬 할당 버퍼(TLAB)**은 JVM에서 객체 생성 성능을 높이기 위해 도입된 기술로,
힙 메모리에서 각 쓰레드가 독립적으로 사용할 수 있는 작은 메모리 영역입니다.
객체 생성 시 동기화 비용을 줄이고 빠르게 메모리를 할당할 수 있도록 설계되었습니다.


### 힙 메모리 할당과 TLAB의 역할

1. 힙 메모리의 Young Generation
   - 객체는 일반적으로 Young Generation의 Eden 영역에서 생성됩니다
   - Eden 영역은 모든 쓰레드가 공유하기 때문에 동기화 이슈가 발생할 수 있습니다.

2. TLAB의 도입
   - JVM은 Eden 영역을 더 작은 조각으로 나눠 각 쓰레드에 TLAB을 할당합니다.
   - TLAB 내에서는 동기화가 필요 없으므로, 객체를 더 빠르게 생성할 수 있습니다.
   - LAB의 공간이 부족하면 새로운 TLAB을 Eden 영역에서 할당받습니다.
   - TLAB 외부(Eden 영역 전체)에서 메모리를 할당할 수도 있습니다.

3. TLAB 기본 동작
   - VM은 각 쓰레드가 자신의 TLAB에서 메모리를 할당하도록 합니다.
   - 객체 생성 시 TLAB에 여유 공간이 있으면 바로 할당됩니다.

   
### TLAB 기반 객체 생성 과정

1. JVM이 각 쓰레드에 TLAB을 초기화하여 Eden 영역의 일부를 할당합니다.
   할당 크기는 JVM 옵션이나 힙 메모리 크기에 따라 다릅니다.

2. 객체 생성 시 메모리 할당
   - TLAB 내 여유 공간 확인
     객체를 생성할 때, 쓰레드는 자신의 TLAB에 여유 공간이 있는지 확인합니다.
   - TLAB 내 공간 충분:
     객체는 TLAB에 생성됩니다.
     동기화 없이 매우 빠르게 처리됩니다.
   - TLAB 내 공간 부족:
     새로운 TLAB을 할당받거나, Eden 영역의 나머지 공간에서 메모리를 할당합니다.

3.  Eden 영역으로 반납
   - TLAB이 다 사용되면, TLAB은 Eden 영역으로 반환되고, Eden 영역은 가비지 컬렉션을 통해 정리됩니다.

### TLAB의 장점

1. 빠른 객체 생성
   TLAB은 쓰레드별로 독립적이므로 동기화 오버헤드가 제거됩니다.

2. 힙 메모리 관리 효율성
   - Eden 영역의 메모리를 쓰레드별로 나눠 사용하기 때문에 메모리 단편화와 충돌을 줄입니다.

3. 가비지 컬렉션 최적화
- TLAB에서 생성된 객체는 Young Generation에 속하며, 짧은 수명을 갖는 경우 Minor GC로 효율적으로 정리됩니다.

### JVM 옵션으로 TLAB 제어
JVM은 TLAB을 관리하는 몇 가지 옵션을 제공합니다:

TLAB 활성화 (기본적으로 활성화됨)
```bash
-XX:+UseTLAB
```

TLAB 크기 비율 설정
Eden 영역의 몇 퍼센트를 TLAB으로 할당할지 설정합니다.
```bash
-XX:TLABSize=1024 # 기본 크기 설정
-XX:ResizeTLAB # TLAB 크기 동적 조정 활성화
```

TLAB 디버깅
TLAB의 사용 현황을 디버깅하려면 아래 옵션을 사용합니다:

```bash
코드 복사
-XX:+PrintTLAB
-XX:+PrintGCDetails
```

--- 

### TLAB을 사용한 객체 생성 과정 예제
다음은 TLAB을 사용하는 객체 생성 과정을 시뮬레이션한 코드입니다:

```java
public class TLABExample {
    public static void main(String[] args) {
        for (int i = 0; i < 10_000_000; i++) {
            MyObject obj = new MyObject(); // 객체 생성
        }
    }
}

class MyObject {
    private int id;
    private String name;

    public MyObject() {
        this.id = 0;
        this.name = "TLAB Object";
    }
}
```
#### 실행 시 TLAB 동작:
1. TLAB 내 공간 충분

- MyObject 객체가 생성되면, 쓰레드는 자신의 TLAB에 메모리를 할당합니다.

2. TLAB 내 공간 부족

- 반복적으로 객체를 생성하면, TLAB이 가득 차게 됩니다. JVM은 Eden 영역에서 새로운 TLAB을 할당하거나, Eden 영역 전체에서 메모리를 할당합니다.

3. Minor GC 발생

- Eden 영역에 가비지 컬렉션이 발생하면, TLAB에 저장된 객체 중 참조되지 않는 객체는 정리됩니다.



### TLAB 관련 주요 지표 확인
JVM에서 TLAB 동작을 확인하려면 GC 로그를 분석하면 됩니다:

TLAB 활성화 (기본적으로 활성화됨)
```bash
-XX:+PrintGCDetails -XX:+PrintTLAB
```

출력 예시:
```plaintext
TLAB: gc thread: 0x00007f87e7a03000  eden: 8192K  survivors: 1024K
TLAB allocations: 512 KB, TLAB waste: 32 KB
```

TLAB allocations: TLAB에서 할당된 메모리 양.
TLAB waste: 사용하지 않은 TLAB 공간.


--- 


# 공부하다 궁금한 것들

## 왜 최대 힙 크기와 스택 메모리를 줄이는 게 더 많은 스레드를 만들 수 있는 유일한 방법인지?

### 1. JVM 메모리 구조와 제한
JVM은 다음과 같은 메모리 영역을 포함합니다:
- **힙(Heap)**: 객체와 배열이 저장되는 메모리.
- **메서드 영역(Method Area)**: 클래스 메타데이터와 상수 저장.
- **스택(Stack)**: 각 스레드가 사용하는 메모리로, 메서드 호출 정보(지역 변수, 매개변수, 리턴 주소 등)를 저장.

### 메모리 경쟁
- **스택 메모리**: JVM은 각 스레드마다 독립적인 스택 메모리를 할당합니다.
- **힙 메모리**: 모든 스레드가 공유하는 메모리로 객체를 저장합니다.
- **운영 체제는 JVM에 사용할 수 있는 메모리를 제한**하며, JVM은 이 메모리를 힙, 메서드 영역, 각 스레드의 스택으로 나눕니다.

---

### 2. 스레드 생성 시 스택 메모리 사용
스레드 생성 시, 운영 체제는 각 스레드에 대해 **스택 메모리 크기를 미리 할당**합니다.
- 스택 크기가 크면:
    - 스레드당 많은 메모리가 필요합니다.
    - 전체 메모리에서 적은 수의 스레드만 생성할 수 있습니다.
- 스택 크기가 작으면:
    - 스레드당 메모리 사용량이 줄어들어, 더 많은 스레드를 생성할 수 있습니다.

---

### 3. 힙 메모리와의 관계
- JVM 힙 메모리는 모든 스레드가 공유하므로, 힙 크기가 크면 전체 메모리에서 스택 메모리에 할당할 공간이 줄어듭니다.
- 반대로, 힙 크기를 줄이면 스택 메모리에 사용할 수 있는 공간이 늘어나 더 많은 스레드를 생성할 수 있습니다.

---

### 4. 왜 힙과 스택을 줄이는 것이 유일한 방법인가?
#### 스레드 수 제한의 원인
1. **메모리 한계**: 운영 체제가 JVM에 제공하는 메모리 양은 고정되어 있습니다.
2. **스택 메모리 요구량**: 각 스레드가 고정된 크기의 스택 메모리를 필요로 합니다.

#### 다른 선택지가 없는 이유
- **운영 체제의 메모리 제한**: JVM이 사용할 수 있는 메모리 양을 늘릴 수 없으면, 스택과 힙의 크기를 조정하는 것이 유일한 옵션입니다.
- **스택 메모리 고정**: 스레드 생성 시 스택 메모리는 고정 크기로 할당되므로, 크기를 줄이지 않으면 메모리가 부족해 더 많은 스레드를 생성할 수 없습니다.
- **힙 메모리와 스레드 경쟁**: 힙 메모리와 스택 메모리는 같은 프로세스 내에서 경쟁하므로, 힙 크기를 줄이지 않으면 스택 공간을 확보할 수 없습니다.

---

### 5. 결론
스레드를 더 많이 생성하려면:
1. **스택 크기를 줄인다**:
    - 스레드당 필요한 메모리 양을 줄여, 더 많은 스레드를 생성할 수 있습니다.
2. **힙 크기를 줄인다**:
    - JVM 전체 메모리에서 스택 메모리에 사용할 수 있는 공간을 늘립니다.

---

### 코드 예제
```java
public class ThreadExperiment {
    public static void main(String[] args) {
        int count = 0;
        try {
            while (true) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000000); // 스레드 대기
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                count++;
            }
        } catch (OutOfMemoryError e) {
            System.out.println("최대 스레드 수: " + count);
        }
    }
}

```
JVM 옵션 조정:

출력 예시:
```bash
java -Xmx128m -Xss256k ThreadExperiment
```


결과:
스택 크기를 줄이고 힙 크기를 줄이면 더 많은 스레드를 생성할 수 있습니다.
기본 설정(-Xss1m)에서는 약 2,000개의 스레드를 생성할 수 있다면, -Xss256k로 줄이면 약 8,000개의 스레드를 생성할 수 있습니다.

## 자바에서 문자 비교할 때 왜 `==`이 아닌 `equals()`를 사용하는가?

자바에서 문자열 비교를 할 때, **`==` 연산자** 대신 **`equals()` 메서드**를 사용하는 이유는 문자열이 저장되는 **상수 풀(String Pool)**과 관련이 있습니다. 문자열 비교 시 두 연산자는 서로 다른 방식으로 동작하므로, 안전한 값을 비교하려면 `equals()`를 사용하는 것이 중요합니다.

---

### 1. `==`와 `equals()`의 차이

#### 1) `==` 연산자
- **`==`는 참조(reference)를 비교합니다.**
- 두 문자열 변수가 **같은 메모리 주소를 가리키는지** 확인합니다.
- 문자열이 **상수 풀에 저장되어 동일한 객체를 참조**하면 `true`를 반환하지만, 그렇지 않으면 `false`를 반환합니다.

#### 2) `equals()` 메서드
- **`equals()`는 문자열의 값(value)을 비교합니다.**
- 두 문자열의 내용이 동일하면 `true`를 반환합니다.
- 문자열이 상수 풀에 있든, 힙 메모리에 있든 상관없이 **문자열의 실제 값**만 비교합니다.

---

### 2. 문자열 상수 풀과 `==`의 동작

#### 1) 상수 풀에 저장된 문자열
- 문자열 리터럴(예: `"example"`)은 상수 풀에 저장됩니다.
- 동일한 문자열 리터럴은 상수 풀에서 **공유**됩니다.

```java
String s1 = "hello";
String s2 = "hello";

System.out.println(s1 == s2);      // true (같은 객체를 참조)
System.out.println(s1.equals(s2)); // true (값이 같음)
```
"hello"는 상수 풀에 저장되며, s1과 s2는 같은 객체를 참조합니다.
따라서 ==와 equals() 모두 true를 반환합니다.

#### 2) 새로운 객체 생성
new 키워드를 사용하면 힙 메모리에 새로운 문자열 객체가 생성됩니다.
이 경우, 동일한 문자열이라도 상수 풀에 있는 문자열과는 다른 객체입니다.

```java
String s1 = "hello";
String s2 = new String("hello");

System.out.println(s1 == s2);      // false (다른 객체를 참조)
System.out.println(s1.equals(s2)); // true (값이 같음)
```

### 3. 왜 equals()를 사용해야 할까?
#### 1)  상수 풀이 아닌 문자열 비교
   ==는 객체의 참조를 비교하므로, 동일한 문자열이라도 참조가 다르면 false를 반환합니다.
   문자열의 실제 값(value)을 비교하려면 항상 equals()를 사용해야 합니다.

```java

String s1 = "world";
String s2 = new String("world");

if (s1.equals(s2)) {
System.out.println("같은 문자열입니다."); // 이 방식이 안전
}

if (s1 == s2) {
System.out.println("같은 참조입니다."); // 이 경우 참조가 다르면 실행되지 않음
}
```

#### 2) intern() 메서드를 이용한 상수 풀 강제 사용
   문자열을 상수 풀에서 관리하고 싶다면, intern() 메서드를 사용합니다.
   intern()은 문자열을 상수 풀로 이동하거나, 이미 상수 풀에 있는 동일한 문자열을 반환합니다.

```java

String s1 = new String("hello");
String s2 = s1.intern(); // 상수 풀에 저장된 "hello" 반환
String s3 = "hello";

System.out.println(s2 == s3);     // true (같은 상수 풀 객체)
System.out.println(s1 == s3);     // false (s1은 힙 객체)
System.out.println(s1.equals(s3)); // true (값은 동일)
```

### 4 프로그래밍에서 안전성 확보
   문자열 비교에서 ==는 코드에 따라 예기치 못한 결과를 초래할 수 있습니다.
   equals()는 문자열 값에만 의존하기 때문에 값 비교의 안전성을 보장합니다.
