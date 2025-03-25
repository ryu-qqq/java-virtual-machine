# ☠️ Classic GC Collector 정리

JVM의 클래식 가비지 컬렉터(Classic Garbage Collectors)는 Java 9 이전에 사용되던 대표적인 수동/병렬 수집기들입니다.
아래는 각 수집기의 특징과 차이점을 요약한 문서입니다.

---

## 📦 1. Serial Collector (시리얼 컬렉터)

### ✅ 특징
- **단일 스레드로 동작**
- Young / Old 영역 모두 단일 스레드로 GC 수행
- Stop-The-World 시간 길지만, 구현이 간단함

### 🧠 적합한 상황
- CPU가 하나거나, 리소스가 매우 적은 환경 (ex. Client, 임베디드 시스템)
- 작은 힙 환경

### 🔧 옵션
```bash
-XX:+UseSerialGC
```

---

## ⚡ 2. ParNew Collector (파뉴 컬렉터)

### ✅ 특징
- **Serial의 Young 영역을 병렬 처리한 버전**
- Old 영역은 **Serial Old**나 **CMS**와 함께 사용됨
- Multithread 지원 (GC 자체를 병렬로 처리)

### 🧠 적합한 상황
- 멀티코어 환경
- **CMS와 함께 사용되는 Young GC로 가장 많이 활용됨**

### 🔧 옵션
```bash
-XX:+UseParNewGC
```

---

## 🚀 3. Parallel Scavenge Collector (패러럴 스캐빈지)

### ✅ 특징
- Throughput(처리량) 위주 설계
- **Young 영역을 멀티스레드로 GC**
- 목표 수집 시간 또는 처리량을 설정 가능

### 🧠 적합한 상황
- **서버 환경에서 Throughput 극대화**
- 사용자 지연보다는 총 처리량이 중요한 환경

### 🔧 옵션
```bash
-XX:+UseParallelGC
-XX:MaxGCPauseMillis=200
-XX:GCTimeRatio=19
```

---

## 🪦 4. Serial Old Collector (시리얼 올드)

### ✅ 특징
- Serial Collector의 Old 영역 전용
- 단일 스레드
- **ParNew와는 함께 쓸 수 없음**

### 🧠 적합한 상황
- 단일 CPU 환경의 Old 영역 수집기
- `-XX:+UseSerialGC` 시 기본으로 포함됨

---

## 🛠️ 5. Parallel Old Collector (패러럴 올드)

### ✅ 특징
- Parallel Scavenge의 Old 영역 버전
- **Young + Old 영역 병렬 처리**
- **Throughput 극대화용 Full GC 수집기**

### 🧠 적합한 상황
- 많은 데이터, 고성능 서버, 병렬 GC 필요할 때
- 대규모 시스템에서 "빠르게" Full GC 처리하고 싶을 때

### 🔧 옵션
```bash
-XX:+UseParallelOldGC
```

---

## 🧼 6. CMS Collector (Concurrent Mark Sweep)

### ✅ 특징
- **Old 영역을 동시에 마크 & 스윕함 (최대한 STW 최소화)**
- Young: ParNew / Old: CMS
- **Stop-the-world 시간을 최소화하는 GC**

### ⚠️ 단점
- 프래그멘테이션 발생
- Concurrent 모드 실패 가능성 있음

### 🧠 적합한 상황
- **지연 시간(Latency)이 중요한 서비스**
- 반응성이 중요한 실시간 애플리케이션

### 🔧 옵션
```bash
-XX:+UseConcMarkSweepGC
```

---

## 📌 요약 비교표

| 컬렉터 | Young 처리 방식 | Old 처리 방식 | 멀티스레드 | 지연 최소화 | Throughput 최적화 |
|--------|------------------|----------------|-------------|--------------|------------------|
| Serial | Serial            | Serial Old     | ❌          | ❌           | ❌               |
| ParNew | ParNew            | CMS            | ✅          | ✅           | ❌               |
| PS     | Parallel Scavenge | Parallel Old   | ✅          | ❌           | ✅               |
| CMS    | ParNew            | CMS            | ✅          | ✅           | ❌               |