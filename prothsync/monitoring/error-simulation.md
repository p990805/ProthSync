# 오류 시뮬레이션 (Swagger → Loki → 향후 n8n)

`ErrorTestController`는 모의 장애를 선택해 JSON 로그와 HTTP 응답을 생성합니다. 실제 DB/Redis 연결 변경, 데이터 쓰기, 외부 API 호출, 지연 대기는 수행하지 않습니다.

## 활성화 및 호출

백엔드 실행 시 `error-test` 프로필을 명시하고 재시작합니다. IDE 실행 설정의 Active profiles에 `error-test`를 추가하거나 환경변수 `SPRING_PROFILES_ACTIVE=error-test`를 설정하세요. 기존 프로필이 있다면 `local,error-test`처럼 함께 지정합니다.

Gradle로 실행한다면 백엔드 디렉터리에서 기존 환경변수를 설정한 후 실행합니다.

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=error-test'
```

기본 프로필에서는 컨트롤러가 등록되지 않습니다. `prod` 또는 `production` 프로필이 함께 활성화되어도 등록되지 않습니다. 프로필 이름이 다른 운영 환경에서는 `error-test`를 활성화하지 마세요.

기존 인증 규칙을 그대로 사용하므로 Access Token이 필요합니다.

1. [Swagger UI](http://localhost:8080/swagger-ui/index.html)에서 로그인 API를 호출합니다.
2. 받은 Access Token을 **Authorize**에 입력합니다. `Bearer` 접두사는 붙이지 않습니다.
3. **오류 시뮬레이션** 태그를 엽니다.
4. `GET /api/test/errors`에서 시나리오 목록을 확인합니다. 이 호출은 오류를 생성하지 않습니다.
5. `POST /api/test/errors/{scenario}`에서 시나리오를 선택해 **Execute**합니다. 의도된 HTTP 400/500/504 응답도 테스트가 정상 실행된 결과입니다.

IDE HTTP 클라이언트 예시는 [error-test.http](../src/main/http/error-test.http)에 있습니다. 실제 토큰은 저장/커밋하지 마세요.

## 시나리오

| scenario | 예외/동작 | HTTP | 로그 레벨 |
| --- | --- | --- | --- |
| `RUNTIME` | `RuntimeException` | 500 | ERROR |
| `NULL_POINTER` | `NullPointerException` | 500 | ERROR |
| `ARITHMETIC` | `ArithmeticException` (0으로 나누기) | 500 | ERROR |
| `DATABASE_CONNECTION` | `DataAccessResourceFailureException` → `SQLException` (SQLState `08001`) | 500 | ERROR |
| `DATABASE_CONSTRAINT` | `DataIntegrityViolationException` → `SQLException` (SQLState `23505`) | 500 | ERROR |
| `REDIS_CONNECTION` | `RedisConnectionFailureException` → `ConnectException` | 500 | ERROR |
| `EXTERNAL_TIMEOUT` | `ResourceAccessException` → `SocketTimeoutException` | 504 | ERROR |
| `NESTED_CAUSE` | `IllegalStateException` → `RuntimeException` → `IOException` | 500 | ERROR |
| `BAD_REQUEST` | `IllegalArgumentException` | 400 | WARN |
| `WARN_ONLY` | 예외 없이 경고만 기록 | 200 | WARN |
| `ERROR_LOG_ONLY` | 예외 없이 오류 로그만 기록 | 200 | ERROR |

예외는 컨트롤러 내부에서 포착합니다. 기존 AOP와 전역 예외 처리기에 전파하지 않으므로 한 번 호출할 때 시뮬레이션 이벤트를 한 건만 기록합니다. 이 API는 **로그 수집·알림·AI 분석 테스트**를 위한 것이며, 실제 외부 시스템의 장애나 전역 예외 처리기의 동작 자체를 재현하는 것은 아닙니다. 기존 AOP의 실행 시간 INFO 로그는 별도로 남을 수 있습니다.

## 응답과 구조화 로그

각 응답에는 다음 필드가 들어갑니다.

```json
{
  "simulationId": "요청마다 생성되는 UUID",
  "scenario": "EXTERNAL_TIMEOUT",
  "logLevel": "ERROR",
  "httpStatus": 504,
  "exceptionType": "org.springframework.web.client.ResourceAccessException",
  "message": "[ERROR_SIMULATION] EXTERNAL_TIMEOUT: 외부 API 응답 시간 초과 (모의, 실제 대기 없음)"
}
```

실제 수집 파일 `logs/prothsync.json`에는 기존 `level`, `logger_name`, `message`, `stack_trace` 외에 아래 JSON 필드가 추가됩니다. `WARN_ONLY`와 `ERROR_LOG_ONLY`는 예외 스택이 없습니다.

| JSON 필드 | 값/용도 |
| --- | --- |
| `logger_name` | `ERROR_SIMULATION` |
| `event` | `error_simulation` |
| `synthetic` | `true` — 테스트 오류와 실제 장애 구분 |
| `simulation_id` | 응답의 `simulationId`와 같은 UUID |
| `error_scenario` | 시나리오 이름 |
| `http_status` | 의도한 HTTP 응답 코드 |
| `exception_type` | 예외 클래스 이름, 로그만 기록한 경우 null |

이 필드들은 JSON 원문에 보존되며 Alloy/Loki의 인덱스 라벨로 추가하지 않습니다. `level`, `service_name`, `environment`는 기존 라벨을 사용합니다.

## Loki 조회

Grafana Explore에서 Loki 데이터 소스를 고르고 최근 15분으로 설정합니다.

시뮬레이션 전체:

```logql
{service_name="prothsync"} | json | logger_name="ERROR_SIMULATION"
```

시뮬레이션 중 ERROR만:

```logql
{service_name="prothsync", level="ERROR"} | json | logger_name="ERROR_SIMULATION"
```

타임아웃 시나리오만:

```logql
{service_name="prothsync"} | json | logger_name="ERROR_SIMULATION" | error_scenario="EXTERNAL_TIMEOUT"
```

응답 ID로 특정 호출 추적 (`UUID` 부분을 실제 응답 값으로 변경):

```logql
{service_name="prothsync"} | json | simulation_id="UUID"
```

최근 5분 테스트 ERROR 개수 (향후 Grafana 알림 → n8n 조건):

```logql
sum(count_over_time(
  {service_name="prothsync", level="ERROR"}
  | json logger_name="logger_name"
  | logger_name="ERROR_SIMULATION"
  | __error__=""
  [5m]
))
```

운영 오류 알림에서 테스트 이벤트를 제외하려면:

```logql
{service_name="prothsync", level="ERROR"}
| json
| logger_name != "ERROR_SIMULATION"
| logger_name != "LOKI_SMOKE_TEST"
```

`ERROR_LOG_ONLY`는 HTTP 200이어도 ERROR 알림이 발생해야 하고, `WARN_ONLY`/`BAD_REQUEST`는 ERROR 전용 알림에서 제외되어야 합니다. `NESTED_CAUSE`는 AI가 `stack_trace`의 최하위 원인까지 분석하는지 확인할 때 사용합니다. n8n에서는 `simulation_id`로 중복 처리를 막고 `error_scenario`별 분석 결과를 비교할 수 있습니다.

## 자동 검증

DB/Redis에 연결하지 않고 각 시나리오의 HTTP 상태, 로그 레벨, 예외 원인 체인, 고유 ID, 인증 및 프로필 활성화 조건을 검사합니다.

```powershell
.\gradlew.bat test --tests com.prothsync.prothsync.controller.ErrorTestControllerTest --tests com.prothsync.prothsync.controller.ErrorTestProfileTest
```
