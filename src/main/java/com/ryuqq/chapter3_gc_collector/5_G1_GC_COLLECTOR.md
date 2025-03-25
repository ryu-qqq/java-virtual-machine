# 🚀 G1 GC (Garbage First Garbage Collector) 완전 정리

---

## 📌 왜 G1 GC가 필요했는가?

기존 GC의 단점 요약:

| GC 종류 | 단점 |
|---------|------|
| Serial / Parallel | Stop-The-World 시간이 너무 길다 |
| CMS | Fragmentation 발생, Concurrent Failure 가능성 |
| Parallel Old | 처리량은 높지만 Full GC는 여전히 STW |

➡️ **Throughput vs Latency 트레이드오프 문제를 해결할 필요가 있었다.**

---

## 🧠 G1 GC 핵심 아이디어

### ✅ 1. Heap을 Region 단위로 나눈다
- 힙을 고정된 Young / Old로 나누지 않음
- 수천 개의 **Region(1~32MB 단위)** 으로 나눠 유연하게 관리
- 각 Region은 GC 시점에 따라 역할이 동적으로 지정됨 (Eden, Survivor, Old 등)

### ✅ 2. 가장 효율적인 Region부터 수집 ("Garbage First")
- 살아있는 객체 비율이 낮은 Region부터 GC 수행
- 즉, **"Garbage First" = 가장 회수 효율 높은 곳 먼저 수거**

---

## ⚙️ G1 GC의 동작 과정

1. **Initial Mark (STW)**  
   - GC Root에서 살아 있는 객체 탐색 시작

2. **Concurrent Mark (동시 실행)**  
   - 전체 힙을 스캔하여 Region별 "살아있는 비율" 계산

3. **Final Mark (STW)**  
   - 변경된 객체 참조 정보 반영 (SATB 사용)

4. **Cleanup / Copy**  
   - 효율 낮은 Region부터 정리
   - 살아있는 객체는 다른 Region으로 복사 (압축 효과)

---

## ✅ 장점 정리

| 장점 | 설명 |
|------|------|
| Pause Time 예측 가능 | -XX:MaxGCPauseMillis로 목표 설정 |
| Region 단위 GC | 전체가 아닌 일부만 수거, STW 시간 감소 |
| Fragmentation 없음 | 살아있는 객체 복사로 압축 자동 수행 |
| Throughput도 괜찮음 | 병렬 마킹 및 복사 |
| 자동 튜닝 시스템 | 역할 자동 조정 (Young/Old 수동 튜닝 불필요) |

---

## ⚠️ 단점

| 단점 | 설명 |
|------|------|
| 초반 튜닝 난이도 존재 | Heap, Region, Pause Time 조합 민감 |
| Full GC는 STW | 공간 부족, System.gc() 호출 시 발생 |
| 초대형 힙에는 불리 | 초거대 객체가 많은 경우 ZGC, Shenandoah가 유리함 |

---

## 🛠️ 주요 JVM 옵션

```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+PrintGCDetails
```

---

## 🧠 왜 G1이 특별한가? 요약

| 문제 | 기존 GC | G1의 해결 전략 |
|------|----------|------------------|
| 긴 STW 시간 | Serial/Parallel/CMS | Region 단위 GC로 부분 수거 |
| Fragmentation | CMS는 Compact 안함 | 객체 복사로 자동 압축 수행 |
| GC 우선순위 없음 | 비효율적인 수거 순서 | 회수 효율 높은 Region부터 선택 |
| Young/Old 고정 구조 | 비탄력적 공간 분배 | 자동 역할 할당으로 유연성 확보 |

---

## ✅ 결론

> **G1은 Latency와 Throughput을 동시에 잡기 위해 설계된 고성능 GC.**
> **정밀한 GC 타겟팅, 자동 튜닝, 부분 수거 기반으로 JVM의 미래를 위한 전환점이 되었다.**