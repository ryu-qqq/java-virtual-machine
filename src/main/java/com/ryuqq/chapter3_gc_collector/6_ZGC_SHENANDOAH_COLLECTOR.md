# ⚙️ ZGC & Shenandoah GC 비교 정리 (초저지연 GC)

---

## 🚀 ZGC (Z Garbage Collector)

### ✅ 목표
- **초저지연(ultra-low pause time)** GC
- GC Pause Time을 **10ms 이하**로 유지하는 것이 목표
- 최대 수백 GB ~ TB급 힙에서도 정밀한 응답 보장

### ✅ 핵심 특징
- **Concurrent GC (STW 최소화)**
- **Region 기반 메모리 구조**
- **Colored Pointer + Load Barrier 사용**  
  → 객체 이동 여부를 포인터에 색 정보로 표시

### ✅ 동작 방식
1. **Concurrent Marking**
   - GC 루트부터 시작해 객체 참조를 탐색
   - 대부분의 마킹 작업은 애플리케이션 스레드와 병렬 수행

2. **Concurrent Relocation**
   - 객체를 복사하면서 힙 정리
   - Load Barrier를 통해 참조 주소를 실시간으로 갱신

3. **STW는 극히 짧게 발생**
   - 초기 루트 마크 / 최종 리마크 일부만 STW

### ✅ 장점
- Pause Time 1~10ms 수준 유지
- 매우 큰 힙에서도 안정적 동작
- 메모리 파편화 없음 (복사 기반 압축)

### ⚠️ 단점
- **JDK 11 이상부터 지원**
- CPU 자원을 많이 씀 (Load Barrier 비용 존재)
- `-XX:+UseZGC` JVM 옵션 필요

---

## 🌌 Shenandoah GC

### ✅ 목표
- ZGC처럼 **Pause Time 최소화**, **Concurrent GC**
- G1의 Region 기반 모델 + 동시 Compaction이 핵심

### ✅ 핵심 특징
- **Concurrent Compaction**
   → 객체 이동조차 STW 없이 진행

- **Brooks Pointer 사용**
   → 객체 내부에 forwarding pointer를 둬서 위치 추적

### ✅ 동작 방식
1. **Concurrent Marking**
2. **Concurrent Evacuation (복사)**
3. **Concurrent Update References**
4. **STW Final Update (매우 짧음)**

### ✅ 장점
- 대부분의 GC 작업이 concurrent
- Pause Time 거의 10ms 미만
- 압축까지 동시에 수행 (fragmentation X)

### ⚠️ 단점
- 다른 GC보다 내부 구조 복잡
- 역시 CPU 리소스 사용 많음
- RedHat 중심으로 개발 → 모든 JVM에 기본 탑재는 아님

---

## 🔧 주요 옵션

### ZGC
```bash
-XX:+UseZGC
-XX:MaxHeapSize=8g
```

### Shenandoah
```bash
-XX:+UseShenandoahGC
-XX:ShenandoahUncommitDelay=1000
```

---

## 🧠 G1 vs ZGC vs Shenandoah 비교표

| 특징 | G1 GC | ZGC | Shenandoah |
|------|-------|-----|-------------|
| 목표 | Pause Time 단축 | Ultra Low Pause | Ultra Low Pause |
| STW 시간 | 수십~수백 ms | 1~10 ms | 1~10 ms |
| 압축 | 있음 (Copy 단계) | 있음 (Concurrent) | 있음 (Concurrent) |
| 객체 이동 | STW 중 이동 | Concurrent | Concurrent |
| 기술 방식 | Region 기반 | Load Barrier + Colored Pointer | Brooks Pointer |
| 적합 대상 | 일반 대용량 앱 | 초거대 힙, 지연 민감 시스템 | 낮은 지연 시간 요구 서버 |

---

## ✅ 결론 요약

| GC | 추천 시나리오 |
|----|----------------|
| G1 | 대부분의 일반 서버 환경 |
| ZGC | TB급 힙 / 실시간 응답 필요 앱 |
| Shenandoah | 낮은 지연 요구, CPU 많은 고성능 서버 |

ZGC와 Shenandoah는 둘 다 GC를 “백그라운드 작업”처럼 돌리는 구조지만, 내부 구현과 전략이 다르기 때문에 시스템 특성과 목적에 따라 선택해야 합니다.