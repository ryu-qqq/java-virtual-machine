# 📚 JVM 클래스 파일 구조 정보

## ✨ **클래스 파일 구조**
JVM이 이해할 수 있는 **클래스 파일 (각 .class)**은 보통 본능적으로 단순한 요소체로 진화되며, 보통 단계적으로 다음과 같은 구조를 가진다.

1. **Magic Number** (`0xCAFEBABE`) - JVM이 클래스 파일인지 확인
2. **Version** - 클래스 파일의 Java 버전 보호
3. **Constant Pool (상수 풀)** - 문자어, 숫자, 메서드, 필드와 같은 정보의 목록
4. **Access Flags** - `public`, `final`, `abstract` 등의 값을 반영
5. **This Class / Super Class** - 클래스 및 목적 클래스 정보
6. **Interfaces** - 구현한 인터페이스 목록
7. **Fields & Methods** - 클래스의 필드 및 메서드 정보
8. **Attributes (속성)** - 배열속성, 예외 정보, 디버그 정보 등을 포함
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
   - RuntimeVisibleAnnotations
   - RuntimeInvisible Annotations
   - RuntimeVisible ParameterAnnotations
   - RuntimeInvisibleparameterAnnotations
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



## ⚡ **정보 종합: JVM이 무조건 보는 4가지!**
| 속성명 | 역할 | 없으면? |
|--------|----------------|------------------|
| **Code** | 메서드의 배열 저장 | 클래스 실행 못 함 |
| **StackMapTable** | 배열 그래픽 결과 확인 | JVM이 실행 거부! |
| **ConstantValue** | `static final` 필드 값 저장 | 실행 속도 초과 |
| **Exceptions** | `throws` 구문의 예외 저장 | 예외 추적 보관 불가능 |




