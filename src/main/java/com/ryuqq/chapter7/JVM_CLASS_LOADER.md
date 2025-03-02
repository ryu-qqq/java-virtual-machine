# 🚀 JVM 클래스 로더

## 🚀 **JVM 클래스 로더(Class Loader) – 클래스 파일을 찾고 메모리에 올리는 과정**
**JVM의 클래스 로더는 `.class` 파일을 찾아서 메모리에 적재하는 역할**을 한다.  
즉, **애플리케이션이 클래스를 사용하려고 할 때, 이 클래스를 어디서 찾고 어떻게 로딩할지를 결정하는 시스템**이다.

### 📌 **1. 클래스 로더 계층 구조 (Class Loader Hierarchy)**
✔️ JVM에는 여러 **클래스 로더가 계층적으로 동작**한다.  
✔️ **부모 → 자식 순서로 클래스 로딩을 위임하는 구조** (부모 위임 모델)

| 클래스 로더 | 역할 |
|------------|----------------------------------|
| **Bootstrap ClassLoader** | `rt.jar`, `java.lang.*` 같은 **핵심 JDK 클래스 로딩** |
| **Extension ClassLoader** | `lib/ext/` 폴더의 확장 라이브러리 로딩 |
| **Application ClassLoader** | **클래스패스(CLASSPATH)에 있는 애플리케이션 클래스 로딩** |
| **Custom ClassLoader** | 개발자가 직접 만든 클래스 로더 (사용자 정의 로더) |

---

### ✅ **2. 부모 위임 모델 (Parent Delegation Model)**
✔️ **자식 로더가 클래스를 찾기 전에 부모 로더한테 먼저 요청**하는 방식  
✔️ **부모가 먼저 로딩할 수 있으면 자식은 로딩 안 함**  
✔️ **부모가 못 찾을 때만 자식이 직접 로딩**

### 🔹 **클래스 찾는 순서**
1️⃣ `Application ClassLoader` → **부모한테 "이 클래스 알아?" 물어봄**  
2️⃣ `Extension ClassLoader` → **"나도 몰라, 부모한테 물어봐"**  
3️⃣ `Bootstrap ClassLoader` → **"이거 JDK 기본 클래스야? 아니면 몰라"**  
4️⃣ **못 찾으면 다시 자식들이 직접 로딩 시도**  
5️⃣ 그래도 없으면? **`ClassNotFoundException` 터짐!**

---

### ✅ **3. 부모 위임 모델이 문제가 되는 경우 (제한 사항)**

#### 🚨 **문제 1: 동일한 이름의 클래스를 덮어쓰고 싶을 때**
✔️ `Bootstrap ClassLoader`가 항상 먼저 로딩하므로, **커스텀 클래스 로더가 같은 이름의 클래스를 로딩할 수 없음**  
✔️ 개발자가 `java.lang.String`을 수정해서 로딩하려 해도 **부트스트랩 로더가 이미 로딩했기 때문에 무시됨.**

**🔥 해결 방법?**  
✔️ **Custom ClassLoader를 만들어 직접 로딩하게끔 우선순위를 바꿈**

#### 🚨 **문제 2: 플러그인 시스템 같은 동적 로딩이 어려움**
✔️ **애플리케이션 실행 중 새로운 클래스를 로딩해야 하는데, 부모가 먼저 잡아버려서 어려움**  
✔️ 예제: **스프링이나 톰캣 같은 웹 애플리케이션에서 새 버전의 클래스를 로딩해야 하는 경우**  
✔️ `Application ClassLoader`가 먼저 잡아버리면 새로운 클래스를 로딩할 방법이 없음

**🔥 해결 방법?**  
✔️ **Custom ClassLoader를 만들어서 부모 위임 모델을 깨고 직접 로딩하도록 구현**  
✔️ 스프링, 톰캣 같은 프레임워크들은 **별도 ClassLoader를 만들어서 관리함**

#### 🚨 **문제 3: `ClassNotFoundException` vs `NoClassDefFoundError`**
✔️ **`ClassNotFoundException`** → **클래스 로더가 해당 클래스를 아예 찾지 못했을 때 발생**  
✔️ **`NoClassDefFoundError`** → **클래스는 있었는데 로딩 중 문제(의존성 문제 등)로 로딩 실패할 때 발생**

**🔥 해결 방법?**  
✔️ `ClassLoader`의 `findClass()` 메서드를 오버라이드해서 직접 로딩하도록 설정 가능

---

### ✅ **4. 직접 커스텀 클래스 로더 만들기 (부모 위임 모델 깨기)**
부모 위임 모델을 깨고, **클래스를 직접 로딩하는 방법**

🔹 **예제: 직접 `ClassLoader`를 확장해서 구현**
```java
import java.io.*;

public class CustomClassLoader extends ClassLoader {
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bytes = loadClassData(name);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        }
    }

    private byte[] loadClassData(String name) throws IOException {
        String fileName = name.replace(".", "/") + ".class";
        try (InputStream input = new FileInputStream(fileName);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            int data;
            while ((data = input.read()) != -1) {
                output.write(data);
            }
            return output.toByteArray();
        }
    }
}
```
🚀 **이제 JVM이 기본적으로 찾을 수 없는 클래스를 직접 로딩할 수 있음!**

---

## 🎯 **클래스 로더 & 부모 위임 모델 요약**
| 개념 | 설명 |
|------|----------------------|
| **클래스 로더** | `.class` 파일을 찾아 JVM 메모리에 올리는 역할 |
| **부모 위임 모델** | 자식 로더가 먼저 부모에게 클래스 로딩 요청하는 방식 |
| **문제 1** | JDK 기본 클래스를 오버라이드할 수 없음 |
| **문제 2** | 애플리케이션 실행 중 동적 로딩이 어려움 |
| **문제 3** | `ClassNotFoundException` vs `NoClassDefFoundError` 발생 가능 |
| **해결책** | **Custom ClassLoader를 만들어 직접 로딩하게 설정** |

---

## 🚀 **Java 모듈 시스템 (Java Module System)**
JDK 9에서 **Java 모듈 시스템(JMS)**이 도입된 이유는 **더 강력한 캡슐화와 안정적인 의존성 관리** 때문이다.

### 📌 **기존 클래스 로더의 문제점 (JDK 8 이하)**
✔️ **클래스 경로(Classpath) 기반 로딩** → 클래스가 많아질수록 충돌과 유지보수 어려움  
✔️ **캡슐화 불가능** → `private` 아니면 **모든 패키지 내부 클래스를 어디서든 접근 가능**  
✔️ **의존성 문제** → **잘못된 JAR이 포함되면 실행 시 `ClassNotFoundException` 발생**

### 🚨 **Java 모듈 시스템이 해결한 문제**
✅ **모듈화(Modularization)** → 각 JAR을 **독립적인 모듈**로 관리  
✅ **강력한 캡슐화(Encapsulation)** → 외부에서 접근 가능한 API만 공개 가능 (`exports`)  
✅ **명확한 의존성 관리** → **필요한 모듈만 명시적으로 가져옴 (`requires`)**  
✅ **불필요한 코드 제거** → JDK 자체도 **모듈화**되어 경량화 가능 (`java.base` 모듈만 필수)

---

## ✅ **1. Java 모듈 시스템의 구조**
`module-info.java` 파일을 기반으로 동작하며, **모듈이 어떤 기능을 제공하고, 어떤 모듈을 사용하는지 선언**할 수 있다.

### 🔹 **모듈 정의 예제 (`module-info.java`)**
```java
module com.example.myapp {
    requires java.sql;    // 이 모듈은 java.sql 모듈을 사용함
    exports com.example.myapp.utils;  // 이 패키지만 외부에서 접근 가능
}
```
🚀 **이제 `com.example.myapp.utils` 패키지만 외부에서 사용 가능하고, 다른 패키지는 숨겨짐!**  
즉, **불필요한 내부 클래스를 외부에서 접근할 수 없음!**

---

## ✅ **2. Java 모듈 시스템의 클래스 로더 변화**
기존 JVM의 **클래스 로더 구조**(부트스트랩 → 확장 → 애플리케이션 로더)는 **모듈 시스템 도입으로 변경됨.**

### 📌 **JDK 8까지의 클래스 로더 계층 구조**
```
[Bootstrap ClassLoader] → [Extension ClassLoader] → [Application ClassLoader]
```

### 📌 **JDK 9 이후의 모듈화된 클래스 로더 계층 구조**
```
[Bootstrap ClassLoader] → [Platform ClassLoader] → [Application ClassLoader]
```
✔️ **Extension ClassLoader가 삭제되고 Platform ClassLoader로 변경됨!**  
✔️ **JDK 내부도 여러 개의 모듈로 쪼개짐** → `java.base`, `java.sql`, `java.xml` 등  
✔️ **Application ClassLoader도 모듈 시스템을 따름** → `module-info.java` 기반

---

## ✅ **3. 기존 클래스 로더와 모듈 클래스 로더의 차이**
|  | **기존 클래스 로더 (JDK 8 이하)** | **모듈화된 클래스 로더 (JDK 9 이후)** |
|------|--------------------------------|----------------------------------|
| **구조** | `Bootstrap → Extension → Application` | `Bootstrap → Platform → Application` |
| **JDK 내부 API** | `rt.jar` (하나의 큰 JAR 파일) | 여러 개의 모듈 (`java.base`, `java.sql` 등) |
| **캡슐화** | 모든 패키지가 기본적으로 공개됨 | `exports` 키워드로 공개 패키지 지정 |
| **의존성 관리** | JAR 파일 충돌 가능 (`ClassNotFoundException` 빈번) | `requires` 키워드로 명확한 의존성 선언 |

---

## ✅ **4. 모듈 시스템의 장점과 한계**
### **🎯 장점**
✔️ **JDK 자체가 모듈화됨** → 필요 없는 모듈 제거 가능 (`jlink`로 맞춤형 JDK 생성)  
✔️ **보안성 강화** → `exports`로 불필요한 내부 코드 감출 수 있음  
✔️ **JAR 파일 충돌 해결** → 모듈 이름을 기반으로 명확한 의존성 관리 가능

### **🚨 단점**
❌ **기존 애플리케이션과 호환성 문제** → 모듈 시스템을 적용하려면 `module-info.java` 수정 필요  
❌ **리플렉션 API 사용 제한** → `setAccessible(true)`가 기본적으로 동작하지 않음 (수동 설정 필요)  
❌ **스프링, 톰캣 같은 기존 라이브러리와 충돌 가능** → 모듈 시스템을 사용하지 않는 라이브러리와 호환성 이슈

---

## ✅ **5. Java 모듈 시스템에서 클래스 로더 우회하는 법**
🚨 모듈 시스템이 **강력한 캡슐화를 적용하면서**, 기존의 `ClassLoader`를 이용한 동적 로딩이 막힐 수도 있음.  
이럴 때는 **`--add-opens`, `--add-exports` 같은 옵션을 이용해서 접근 가능**

🔹 **모듈 접근을 허용하는 방법 (JVM 옵션)**
```bash
java --add-opens java.base/java.lang=ALL-UNNAMED -jar myapp.jar
```
🚀 **이렇게 하면 `java.lang` 패키지를 모든 모듈에서 접근 가능하게 열어줌!**

---

## 🎯 **Java 모듈 시스템 요약**
| 개념 | 설명 |
|------|----------------------|
| **모듈 시스템 도입 이유** | JAR 충돌 방지, 강력한 캡슐화, 명확한 의존성 관리 |
| **기존 vs 새로운 클래스 로더** | JDK 8 → `Bootstrap → Extension → Application` / JDK 9 → `Bootstrap → Platform → Application` |
| **모듈 선언 방법** | `module-info.java` 에서 `exports`, `requires` 선언 |
| **장점** | 보안 강화, JDK 경량화 가능, 의존성 충돌 해결 |
| **단점** | 기존 앱과의 호환성 문제, 리플렉션 사용 제한, 일부 라이브러리와 충돌 가능 |
| **우회 방법** | JVM 옵션 `--add-opens`, `--add-exports` 사용 |

---

