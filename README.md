# Java Virtual Machine

> 책: **JVM 밑바닥 까지 파헤치기**


이 레포지토리는 **JVM 밑바닥 까지 파헤치기** 책을 읽으며 학습한 내용을 정리한 노트입니다. 각 단원별로 주요 내용을 요약하고, 배운 점과 인사이트를 기록합니다.  공부한 단원의 순서는 꼭 순차적이진 않습니다.


⚠️ **경고**: 이 문서는 제가 기억하기 쉽게 저만의 언어로 비유를 많이 해놨습니다. 따라서 표현이 다소 상스럽거나 직설적일 수 있으니 주의 바랍니다. 하지만 내용의 본질은 확실하게 전달될 것입니다. 🧠🔥


## 목차

1. 자바 메모리 영역과 메모리 오버플로
   1. [JAVA 메모리 영역](src/main/java/com/ryuqq/chapter2_java_memory_area/1_JAVA_MEMORY_AREA.md)
   2. [가상 머신에서의 객체 들여다보기](src/main/java/com/ryuqq/chapter2_java_memory_area/2_OBJECT_FROM_JVM_PERSPECTIVE.md)
   3. [그외 정보](src/main/java/com/ryuqq/chapter2_java_memory_area/3_EXTRA_INFO.md)
2. 가비지 컬렉터와 메모리 할당 전략
   1. [GC 참조 분석](src/main/java/com/ryuqq/chapter3_gc_collector/1_JVM_GC_REFERENCE_ANALYSIS.md)
   2. [GC 최적화](src/main/java/com/ryuqq/chapter3_gc_collector/2_JVM_GC_OPTIMIZATION.md)
3. [자바 클래스 파일 구조](src/main/java/com/ryuqq/chapter6_class_file_structure/CLASS_FILE_STRUCTURE.md)
4. 자바 JVM 클래스 로딩 및 클래스 로더
   1. [JVM CLASS LOADING 과정](src/main/java/com/ryuqq/chapter7/JVM_CLASS_LOADER.md)
   2. [JVM CLASS LOADER](src/main/java/com/ryuqq/chapter7/JVM_CLASS_LOADING.md)
5. 바이트 코드 실행 엔진
   1. [JVM_STATIC_DISPATCH](src/main/java/com/ryuqq/chapter8/JVM_STATIC_DISPATCH.md)
   2. [JVM_DYNAMIC_DISPATCH](src/main/java/com/ryuqq/chapter8/JVM_DYNAMIC_DISPATCH.md)
   3. [JVM_DYNAMIC_TYPING](src/main/java/com/ryuqq/chapter8/JVM_DYNAMIC_TYPING.md)
   4. [JVM_METHOD_INVOCATION](src/main/java/com/ryuqq/chapter8/JVM_METHOD_INVOCATION.md)
   5. [JVM_REFLECTION_CACHING](src/main/java/com/ryuqq/chapter8/JVM_REFLECTION_CACHING.md)
   6. [JVM_RUNTIME_STACK](src/main/java/com/ryuqq/chapter8/JVM_RUNTIME_STACK.md)
6. 컴파일과 최적화
   1. [JVM_COMPILER_OPTIMIZATION](src/main/java/com/ryuqq/chapter11/JVM_COMPILER_OPTIMIZATION.md)
7. [자바 메모리 모델 및 스레드](src/main/java/com/ryuqq/chapter12/JAVA_MEMORY_MODEL_THREAD.md)
8. [스레드 안전성과 락 최적화](src/main/java/com/ryuqq/chapter13/THREAD_SAFETY_LOCK_OPTIMIZATION.md)
