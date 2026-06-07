# ADR-0001 — 도메인 객체 / JPA 엔티티 분리 전략

- 작성일: 2026-06-07
- 상태: 확정

---

## 결정

엔티티와 도메인 객체를 **전면 분리하지 않고**, 레이어 특성에 따라 절충한다.

| 대상 | 전략 | 이유 |
|---|---|---|
| `AnalysisSession`, `Payment`, `AnalysisUnlock`, `User` | **통합** — JPA 엔티티 = 도메인 객체 | 로직이 단순한 데이터 컨테이너 수준. 매퍼 보일러플레이트 대비 실익 없음 |
| `AnalysisResult`, `AnalysisArchive` (MongoDB) | **통합** — MongoDB Document = 도메인 객체 | 비정형 Document 자체가 이미 도메인 표현 |
| 심리 엔진 파이프라인 (Layer 0~4) | **분리** — 순수 Java 도메인 서비스 | 핵심 비즈니스 로직. JPA/MongoDB 의존 없이 독립 유지 |

---

## 배경

DDD 원칙상 도메인 레이어가 인프라(JPA)에 의존하면 안 된다.
그러나 전면 분리 시 모든 엔티티마다 JPA Entity / Repository 구현체 / 매퍼가 추가되어
MVP 단계에서 파일 수 3배 증가, 개발 속도 저하 문제가 있다.

---

## 결과

- 단순 엔티티는 `@Entity` + `@NoArgsConstructor(access = PROTECTED)` + 정적 팩토리 메서드로 작성
- setter 금지, 상태 변경은 도메인 메서드로만 (`markAsPaid()` 등)
- 심리 엔진 파이프라인(`SignalExtractor`, `FrameworkMapper` 등)은 `infrastructure` 의존 없이 순수 Java 인터페이스 + 구현체로 유지
- 향후 엔티티 로직이 복잡해지면 분리 전환 검토
