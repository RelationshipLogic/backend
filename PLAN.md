# RelationshipLogic Backend — 작업 계획

> 작성일: 2026-06-07
> 저장소: https://github.com/RelationshipLogic/backend.git
> 서버: M1 맥미니 홈서버 (OrbStack Ubuntu 24.04 VM) / 도메인 미정

---

## 전체 개요

UX 픽스 전에 완료할 수 있는 백엔드 작업을 우선 진행한다.
API(Controller) 레이어는 UX 확정 후 붙인다.

---

## Phase 0 — 우선 진행 (UX 무관)

### Task 1. 인프라 셋업

**목표**: 로컬 개발 환경과 홈서버 배포 환경 구성

#### 1-1. Docker Compose

```
PostgreSQL 16    포트 5432  유저/결제/분석 세션 등 정형 데이터
MongoDB 7        포트 27017 대화 히스토리·분석 결과 등 비정형 데이터
Spring Boot App  포트 8080  애플리케이션 서버
```

- `docker-compose.yml` — 전체 스택 (운영/CI용)
- `docker-compose.override.yml` — 로컬 개발용 (볼륨 마운트, 로그 레벨)

#### 1-2. GitHub Actions CI/CD

```
push to main
  └─▶ Build & Test (Java 17)
  └─▶ Docker Image Build & Push (GHCR)
  └─▶ SSH → 홈서버 → docker compose pull && up -d
```

- 홈서버 접근: Cloudflare Tunnel SSH 또는 내부 IP (192.168.50.123)
- Secrets: `SSH_HOST`, `SSH_USER`, `SSH_KEY`, `GHCR_TOKEN`

#### 1-3. 환경변수 관리

```
.env.example     ← 커밋 (키 없이 구조만)
.env             ← .gitignore (실제 값)
```

필요 환경변수:
```
# DB
POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD
MONGO_URI

# AI
ANTHROPIC_API_KEY
GOOGLE_API_KEY

# 결제
PORTONE_API_KEY, PORTONE_API_SECRET, PORTONE_STORE_ID

# 앱
JWT_SECRET
```

---

### Task 2. 프로젝트 뼈대

**목표**: DDD 기반 Spring Boot 3.x 프로젝트 구조 생성

#### 기술 스택

| 항목 | 선택 |
|---|---|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.x |
| 빌드 | Gradle (Kotlin DSL) |
| ORM | Spring Data JPA (PostgreSQL) |
| NoSQL | Spring Data MongoDB |
| 편의 | Lombok |
| 테스트 | JUnit 5 + AssertJ (Fake 객체 기반) |

#### 패키지 구조

```
com.relationshiplogic
 ├── domain
 │    ├── analysis
 │    │    ├── AnalysisSession.java          // 분석 세션 엔티티
 │    │    ├── AnalysisResult.java           // 분석 결과 (MongoDB document)
 │    │    ├── RelationshipType.java         // 관계 유형 enum
 │    │    ├── AnalysisSessionRepository.java
 │    │    └── AnalysisResultRepository.java
 │    ├── payment
 │    │    ├── AnalysisUnlock.java           // unlock 상태 엔티티
 │    │    ├── Payment.java                  // 결제 기록 엔티티
 │    │    ├── UnlockGrade.java             // BASIC / DEEP_SCAN / NEXT_MOVE enum
 │    │    ├── UnlockMethod.java            // AD / CASH enum
 │    │    └── PaymentRepository.java
 │    ├── archive
 │    │    ├── AnalysisArchive.java          // 분석 저장함 (MongoDB document)
 │    │    └── AnalysisArchiveRepository.java
 │    └── user
 │         ├── User.java
 │         └── UserRepository.java
 ├── application
 │    ├── analysis
 │    │    └── AnalysisService.java
 │    ├── payment
 │    │    └── PaymentService.java
 │    └── archive
 │         └── ArchiveService.java
 ├── infrastructure
 │    ├── ai
 │    │    ├── AiClient.java                // 인터페이스
 │    │    ├── AnthropicAiClient.java
 │    │    ├── GoogleAiClient.java
 │    │    └── AiRouter.java               // 결제 등급 → 클라이언트 선택
 │    ├── persistence
 │    │    └── (JPA / MongoDB 설정)
 │    └── payment
 │         └── PortOneClient.java
 └── interfaces
      └── api
           └── (UX 확정 후 작업)
```

---

### Task 5. 데이터 스키마 설계

**목표**: UX 무관한 핵심 엔티티 정의

#### PostgreSQL 엔티티

**users**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT PK | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |
> 로그인 방식 미확정 → 최소 스펙 유지. 인증 방식 확정 후 컬럼 추가.

**analysis_sessions**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK | nullable (비로그인 허용 검토) |
| relationship_type | VARCHAR | 썸/연인/전연인/짝사랑/친구/직장 |
| context_memo | TEXT | 사용자 맥락 메모 (옵셔널) |
| input_type | VARCHAR | IMAGE / TEXT |
| created_at | TIMESTAMP | |

**analysis_unlocks**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT PK | |
| session_id | BIGINT FK | |
| grade | VARCHAR | BASIC / DEEP_SCAN / NEXT_MOVE |
| method | VARCHAR | AD / CASH |
| payment_id | BIGINT FK | nullable (AD unlock 시 null) |
| unlocked_at | TIMESTAMP | |

**payments**
| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK | |
| portone_payment_id | VARCHAR | 포트원 결제 ID |
| amount | INT | 결제 금액 (원) |
| status | VARCHAR | PENDING / PAID / FAILED / CANCELLED |
| created_at | TIMESTAMP | |
| paid_at | TIMESTAMP | nullable |

#### MongoDB 컬렉션

**analysis_results**
```json
{
  "_id": "ObjectId",
  "sessionId": "Long",
  "headline": "지금은 관망 모드, 먼저 연락하지 마세요",
  "mainMetric": { "label": "관심도", "value": 72 },
  "subMetrics": [
    { "label": "선톡 비율", "value": 60 },
    { "label": "미래 언급", "value": 40 }
  ],
  "signals": [
    { "type": "RESPONSE_SPEED", "description": "답장 속도가 점점 느려지고 있어요" }
  ],
  "hiddenIntentTeasers": [
    { "type": "AVOIDANCE", "confidence": 0.65, "description": "회피 신호 가능성" }
  ],
  "nextMoveTeaserDirection": "HOLD",
  "createdAt": "ISODate"
}
```

**analysis_archives**
```json
{
  "_id": "ObjectId",
  "userId": "Long",
  "sessionId": "Long",
  "personAlias": "썸남",
  "relationshipType": "CRUSH",
  "summary": "관망 모드 권장. 관심도 72점.",
  "mainMetricSnapshot": { "label": "관심도", "value": 72 },
  "keySignals": ["RESPONSE_SPEED_DECLINING", "FUTURE_MENTION_LOW"],
  "riskFlags": [],
  "savedAt": "ISODate"
}
```

---

## Phase 1 — UX 확정 후 진행

- REST API (Controller 레이어) 설계 및 구현
- 이미지 업로드 → Claude OCR 통합 호출
- 심리 엔진 파이프라인 (Layer 0~4) 구현
- 포트원 결제 연동 (결제 요청 → 승인 → unlock 상태 저장)
- 위기 대응 가드레일 (키워드 필터)
- 광고 unlock 연동

## Phase 2 — 분석 저장함

- 저장 플로우 API
- 인물별 기록 조회 API

## Phase 3 — RAG 기반 고도화

- pgvector 활성화
- RAG 검색 레이어

---

## 작업 순서 (Phase 0 기준)

```
1. docker-compose.yml + docker-compose.override.yml
2. .env.example
3. Spring Boot 프로젝트 초기화 (build.gradle.kts)
4. 패키지 구조 + 각 레이어 빈 클래스 생성
5. JPA 엔티티 (User, AnalysisSession, AnalysisUnlock, Payment)
6. MongoDB Document (AnalysisResult, AnalysisArchive)
7. GitHub Actions 워크플로우 (.github/workflows/cd.yml)
```

---

## 홈서버 배포 환경

| 항목 | 내용 |
|---|---|
| 기기 | M1 맥미니 |
| VM | OrbStack Ubuntu 24.04 |
| 내부 IP | 192.168.50.123 |
| 외부 접근 | Cloudflare Tunnel |
| 도메인 | 미정 (minu-dev.win 서브도메인 예정) |
| 런타임 | Docker Compose |
