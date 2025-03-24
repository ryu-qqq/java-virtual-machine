# 가상 머신에서의 객체 들여다보기

## 객체 생성 과정 (메모리 관점)
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

멀티 스레딩 환경에서는 여러 스레드가 동시에 객체를 생성하려고할때 문제가 발생 할 수 있다
한 스레드가 요청한 객체 A를 메모리에 할당하는 과정에서 포인터의 값을 아직 수정하기 전에 다른 스레드가 객체 B용 메모리를 요청 할 수 있다.


### 힙 메모리 할당과 TLAB의 역할

1. 힙 메모리의 Young Generation
   - 객체는 일반적으로 Young Generation의 Eden 영역에서 생성됩니다
   - Eden 영역은 모든 쓰레드가 공유하기 때문에 **동기화 이슈**가 발생할 수 있습니다.

2. TLAB의 도입
   - JVM은 Eden 영역을 더 작은 조각으로 나눠 각 쓰레드에 TLAB을 할당합니다.
   - TLAB 내에서는 동기화가 필요 없으므로, 객체를 더 빠르게 생성할 수 있습니다.
   - TLAB의 공간이 부족하면 새로운 TLAB을 Eden 영역에서 할당받습니다.
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

4. Escape Analysis 와 TLAB의 관계
- JVM이 TLAB 을 최적화할지 말지 결정하는 중요한 기술이 Escape Analysis
- Escape Analysis 를 통해 객체가 쓰레드 외부로 "탈출"하지 않는다고 판단되면, 힙이 아닌 스택에 객체를 할당


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
- TLAB을 사용할 수 없을 경우 → Eden 전체 영역에 Lock 기반 동기화 후 직접 할당

3. Minor GC 발생
- Eden이 꽉 찼다면 → Minor GC 발생, Survivor → Old 로 이동 가능성 생김
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
