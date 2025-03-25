# 📚 JVM 클래스 파일 구조 정보

## ✨ 클래스 파일 기본 구조
자바 소스 코드가 javac 컴파일러에 의해 `.class` 파일로 컴파일되면, JVM이 읽을 수 있는 이진 구조로 변환된다. 아래는 클래스 파일의 핵심 구조이다:


1. **Magic Number**: `0xCAFEBABE` - JVM이 클래스 파일인지 확인
2. **Version**: major/minor 버전 - 자바 컴파일러 버전 확인
3. **Constant Pool**: 클래스/메서드/필드/문자열 등의 참조 정보
4. **Access Flags**: `public`, `final`, `abstract` 등
5. **This Class / Super Class**: 클래스와 부모 클래스 이름
6. **Interfaces**: 구현한 인터페이스 목록
7. **Fields**: 필드 목록
8. **Methods**: 메서드 목록
9. **Attributes**: 부가 정보 (`Code`, `Annotations`, `LineNumberTable` 등)
   - ConstantValue
   - Code
   - Exceptions
   - SourceFile
   - LineNumberTable
   - LocalVariableTable
   - InnerClasses
   - Synthetic
   - Deprecated
   - EnclosingMethod
   - Signature
   - SourceDebugExtension
   - LocalVariableTypeTable
   - RuntimeVisible Annotations
   - RuntimeInvisible Annotations
   - RuntimeVisible ParameterAnnotations
   - RuntimeInvisible ParameterAnnotations
   - AnnotationDefault
   - StackMapTable
   - BootstrapMethods
   - RuntimeVisibleTypeAnnotations
   - RuntimeInvisible TypeAnnotations
   - MethodParameters
   - Module
   - ModulePackages
   - ModuleMainClass
   - NestHost
   - NestMembers
   - Record
   - PermittedSubclasses
   
## ⚡ **클래스 파일에서 필수적인 4가지 속성**
JVM이 파일을 이해하는데 필수적으로 필요한 4가지 속성을 확인해야 한다.

### 1. **Code (메서드 배열 정보)**
- 메서드의 **배열 코드**가 저장되는 공간.
- 모든 메서드에 포함 (abstract 메서드와 인터페이스 메서드는 제외)
- **없으면 JVM이 클래스 파일을 실행 못 한다!**
- **배열 코드 예제:**
  ```java
  public void sayHello() {
      System.out.println("Hello, JVM!");
  }
  ```

### 2. **StackMapTable (배열의 태그 확인 정보)**
- JVM이 **배열을 작동하기 전에** 정의된 그래픽을 확인하는 공간.
- **Java 7보다 보다 들어오면 필수화.**


### 3. **ConstantValue (`static final` 필드 값 저장)**
- `static final` 필드의 값을 정확하게 JVM에서 통합.
- **코드 예제:**
  ```java
  public class Test {
      static final int A = 100;
  }
  ```

### 4. **Exceptions (`throws` 구문의 예외 정보)**
- 메서드가 `throws` 선언을 하면 JVM이 이 개발자가 파악할 수 있는 예외를 보관할 수 있도록 정보를 저장.
- **코드 예제:**
  ```java
  public void risky() throws IOException {}
  ```

---~~

## ⚡ 실전 자바 코드 매핑

| 클래스 파일 요소 | 자바 코드 예시 | 설명 및 실무 연관성 |
|------------------|----------------|----------------------|
| **Magic Number** | - | `.class` 파일 식별 (JVM이 자동 생성) |
| **Version** | - | 컴파일러 버전 확인용 (실행 JVM과 불일치 시 `UnsupportedClassVersionError`) |
| **Constant Pool** | `"hello"`, `123`, 메서드 참조 | 상수 리터럴, 메서드 참조 등이 저장됨. `intern()`, 리플렉션 등에서 사용 |
| **Access Flags** | `public class A {}` | 접근 제어자 정보 (`ACC_PUBLIC` 등), 프록시 생성 시 판단 |
| **This/Super Class** | `class A extends B {}` | 상속 정보 저장. `getSuperclass()` 등 리플렉션에 사용 |
| **Interfaces** | `implements Serializable` | 구현 인터페이스 저장. 프록시 생성 기준 |
| **Fields** | `int age;` | 필드 이름, 타입, 접근제어자 저장 |
| **Methods** | `void sayHello() {}` | 메서드 이름, 시그니처, 바이트코드 저장 |
| **Attributes: Code** | 메서드 바디 | 실제 JVM이 실행하는 바이트코드 포함. 인터페이스/추상메서드는 없음 |
| **Attributes: StackMapTable** | - | 스택 상태 검증 정보 (Java 7+) |
| **Attributes: ConstantValue** | `static final int A = 10` | 컴파일 시 값 고정. 리플렉션으로 수정 불가 |
| **Attributes: Exceptions** | `throws IOException` | 메서드 선언에 명시된 예외 정보 |
| **RuntimeVisibleAnnotations** | `@Service`, `@Test` | 리플렉션에서 접근 가능한 어노테이션 |
| **Signature** | `List<String>` | 제네릭 타입 보존. 리플렉션으로 확인 가능 |
| **LineNumberTable** | `int a = 1;` | 디버깅 정보 (소스 줄 번호) |
| **LocalVariableTable** | 지역 변수들 | 디버깅/리플렉션용 변수 이름 정보 |

---

## 🔥 실무에 유용한 CLI 명령어

```bash
javap -v MyClass.class
```

- 클래스 파일 구조와 바이트코드 확인 가능
- 상수 풀, 메서드 바이트코드, 어노테이션 정보 등 모두 출력

---

## ✅ 실무 팁 요약

- `RuntimeVisibleAnnotations` → Spring, JUnit이 읽음
- `ConstantValue` → final 값 컴파일 타임에 고정됨
- `StackMapTable` → 바이트코드 검증 필수 요소
- `Signature` → 제네릭 정보 보존용
- `.class`는 단순한 파일이 아님, JVM이 "의미를 해석할 수 있는 구조화된 이진 문서"임

---

공부할 때 `javap`, `ASM`, `ByteBuddy` 같은 도구도 병행하면 클래스 파일에 대한 이해가 훨씬 깊어진다.




