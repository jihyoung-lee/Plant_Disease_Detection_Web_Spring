# 🌿 Plant Disease Detection API

작물 병해충 정보를 검색하고, 이미지 기반 AI 예측 결과를 병해충 상세 정보와 연결하는 Spring Boot 백엔드입니다.

> 예측은 AI 서버가 담당하고, 병명·증상·예방 및 방제 정보는 농촌진흥청 국가농작물병해충관리시스템 API를 기반으로 제공합니다.

## 주요 기능

| 기능 | 설명 |
| --- | --- |
| 병해충 검색 | 작물명 또는 병해충명으로 검색하고, 5개 단위로 페이지를 반환합니다. |
| 병해충 상세 조회 | 증상, 발생 조건, 예방·방제 방법 및 관련 이미지를 제공합니다. |
| AI 이미지 예측 | 지원 작물과 병해 이미지를 받아 AI 서버에 예측을 요청합니다. |
| 상세 정보 자동 연결 | 예측 병명을 병해충 API에서 찾아, 가능한 경우 상세 정보를 함께 반환합니다. |
| Redis 캐시 | 검색 결과는 30분, 상세 정보는 1시간 동안 캐시합니다. |
| 예외 응답 표준화 | 입력 오류, 파일 용량 초과, 외부 API 오류·타임아웃을 일관된 형식으로 반환합니다. |

## 기술 스택

- Java 21
- Spring Boot 4.1
- Spring Web MVC / WebFlux (`WebClient`)
- Spring Validation
- Spring Cache + Redis
- Gradle

## 동작 구조

```text
클라이언트
  │
  ├── GET /pest ────────────────┐
  ├── GET /pest/{sickKey}       ├── 농촌진흥청 병해충 API
  │                             └── Redis 캐시
  │
  └── POST /predict
         │
         ├── AI 예측 서버 (/predict)
         └── 예측 병명으로 병해충 API 조회 → 상세 정보 결합
```

## 사전 요구 사항

- JDK 21
- Redis 7 이상 (`localhost:6379` 기본값)
- 농촌진흥청 병해충 API 인증 키
- 이미지 예측 AI 서버 

AI 서버는 아래 `multipart/form-data` 요청을 처리해야 합니다.

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `crop_name` | String | 영문 작물 식별자 |
| `image` | File | 병해 이미지 |


## 환경 설정

기본 설정은 [`application.properties`](src/main/resources/application.properties)에 있습니다.

```properties
pest.api.key=${PEST_API_KEY}
pest.api.url=http://ncpms.rda.go.kr/npmsAPI/service

ai.api.url=http://test.com/predictionAPI

spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

### 환경 변수 설정

PowerShell:

```powershell
$env:PEST_API_KEY = "발급받은_농촌진흥청_API_키"
```

AI 서버 또는 Redis 주소를 변경해야 할 경우 실행 옵션으로 덮어쓸 수 있습니다.

```powershell
.\gradlew.bat bootRun --args="--ai.api.url=http://test.com/predictionAPI --spring.data.redis.host=localhost"
```

## 실행

```powershell
# 1. Redis 실행
redis-server

# 2. 환경 변수 설정
$env:PEST_API_KEY = "발급받은_API_키"

# 3. Spring Boot 실행
.\gradlew.bat bootRun
```

기본 포트는 `8080`입니다.

```text
http://localhost:8080
```

## API

### 1. 병해충 검색

```http
GET /pest?cropName={cropName}&sickNameKor={sickNameKor}&page={page}
```

`cropName`, `sickNameKor` 중 하나 이상은 필수이며 `page`의 기본값은 `1`입니다.

```bash
curl.exe "http://localhost:8080/pest?cropName=사과&page=1"
```

응답 예시:

```json
{
  "totalCount": 12,
  "page": 1,
  "displayCount": 5,
  "totalPages": 3,
  "items": [
    {
      "cropName": "사과",
      "thumbImg": "https://...",
      "sickNameKor": "갈색무늬병",
      "sickKey": "..."
    }
  ]
}
```

### 2. 병해충 상세 조회

```http
GET /pest/{sickKey}
```

```bash
curl.exe "http://localhost:8080/pest/{sickKey}"
```

응답에는 `infectionRoute`, `developmentCondition`, `symptoms`, `preventionMethod`, 생물적·화학적 방제 방법과 `imageList`가 포함됩니다.

### 3. AI 이미지 예측

```http
POST /predict
Content-Type: multipart/form-data
```

요청 필드:

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `cropName` | String | O | 아래 지원 작물 중 하나 |
| `image` | File | O | 이미지 파일, 최대 20MB |

지원 작물:

| 요청 값 | 작물명 |
| --- | --- |
| `potato` | 감자 |
| `apple` | 사과 |
| `grape` | 포도 |
| `peach` | 복숭아 |
| `strawberry` | 딸기 |

```powershell
curl.exe -X POST "http://localhost:8080/predict" -F "cropName=apple" -F "image=@C:\images\apple-leaf.jpg"
```

#### 예측 결과 상태

| `status` | 의미 | `pestInfo` |
| --- | --- | --- |
| `SUCCESS` | 예측 병명과 연결된 상세 정보를 찾았습니다. | 상세 객체 포함 |
| `UNDETERMINED` | 이미지로 병해를 판별하기 어렵습니다. | `null` |
| `INFO_NOT_FOUND` | 병명은 예측했지만 연결할 상세 정보가 없습니다. | `null` |

`SUCCESS` 응답 예시:

```json
{
  "status": "SUCCESS",
  "cropName": "apple",
  "sickNameKor": "갈색무늬병",
  "confidence": 99.0,
  "message": "예측에 성공했습니다.",
  "pestInfo": {
    "cropName": "사과",
    "sickNameKor": "갈색무늬병",
    "symptoms": "...",
    "preventionMethod": "...",
    "imageList": []
  }
}
```

`INFO_NOT_FOUND` 응답 예시:

```json
{
  "status": "INFO_NOT_FOUND",
  "cropName": "apple",
  "sickNameKor": "예측된 병명",
  "confidence": 95.5,
  "message": "예측 결과와 맞는 병해충 상세 정보를 찾지 못했습니다.",
  "pestInfo": null
}
```

## 캐시 정책

| 캐시 이름 | 대상 | 키 | TTL |
| --- | --- | --- | --- |
| `pestSearch` | 병해충 검색 결과 | `cropName:sickNameKor:page` | 30분 |
| `pestInfo` | 병해충 상세 정보 | `sickKey` | 1시간 |

## 오류 응답

모든 예외는 다음 형식으로 반환합니다.

```json
{
  "status": 400,
  "code": "BAD_REQUEST",
  "message": "작물명 또는 병명을 입력해야 합니다.",
  "timestamp": "2026-06-23 12:00:00"
}
```

| HTTP 상태 | 코드 | 발생 상황 |
| --- | --- | --- |
| `400` | `BAD_REQUEST` | 잘못된 입력, 지원하지 않는 작물, 페이지 번호 오류 |
| `413` | `FILE_TOO_LARGE` | 20MB 초과 파일 업로드 |
| `502` | `AI_SERVER_ERROR` | AI 서버 연결 또는 응답 처리 실패 |
| `502` | `PEST_API_ERROR` | 병해충 외부 API 호출 실패 |
| `504` | `AI_TIMEOUT` | AI 서버 응답 시간 초과 (15초) |
| `504` | `PEST_API_TIMEOUT` | 병해충 외부 API 응답 시간 초과 |
| `500` | `INTERNAL_SERVER_ERROR` | 처리되지 않은 서버 오류 |

## 프로젝트 구조

```text
src/main/java/com/jihyoung/plant_disease_detection_web_spring
├── global
│   ├── dto                 # 공통 오류 응답
│   └── exception           # 예외 타입 및 전역 예외 처리
└── pest
    ├── cache               # Redis 캐시 설정
    ├── client              # AI 서버·병해충 외부 API 클라이언트
    ├── controller          # REST 엔드포인트
    ├── dto                 # 검색·상세·AI 응답 DTO
    └── service             # 검색, 상세 조회, AI 결과 연결 로직
```

## 테스트

```powershell
.\gradlew.bat test
```

---

병해충 정보 출처: 농촌진흥청 국가농작물병해충관리시스템
