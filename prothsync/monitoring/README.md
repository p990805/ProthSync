# 로컬 모니터링

현재 백엔드는 IDE/호스트에서 실행하고, 모니터링 도구는 Docker Compose로 실행합니다.

```text
Spring Boot ── /actuator/prometheus ── Prometheus ── Grafana
     └── logs/prothsync.json ── Alloy ── Loki ── Grafana Explore
```

## 실행

Docker Desktop을 Linux 컨테이너 모드로 실행한 뒤, 백엔드 디렉터리에서 실행합니다.

```powershell
cd C:\Users\p9908\SSAFY\ProthSync\prothsync
docker compose config --quiet
docker compose up -d loki alloy grafana
```

위 명령은 Grafana 의존성인 Prometheus도 시작합니다. DB/Redis까지 필요한 경우 기존처럼 `docker compose up -d`를 사용합니다.

백엔드를 **작업 디렉터리 `prothsync/`** 로 실행하거나 재시작합니다. 기존 환경변수 설정은 그대로 필요합니다. IDE의 실행 설정에서 Working directory를 확인하세요. 콘솔은 기존 텍스트 로그를 유지하고, 수집용 파일만 Spring Boot의 내장 Logstash JSON 형식으로 기록합니다. 별도 Java 로깅 의존성은 추가하지 않았습니다.

| 접속 위치 | 주소 |
| --- | --- |
| Grafana | http://localhost:3000 |
| Loki 준비 상태 | http://localhost:3100/ready |
| Alloy 수집 파이프라인 | http://localhost:12345 |

Grafana 로그인은 기존 `GRAFANA_ADMIN_USER` / `GRAFANA_ADMIN_PASSWORD` 설정을 따릅니다. 초기 기본값은 `admin` / `admin`이며, 기존 Grafana 볼륨의 비밀번호는 환경변수 변경으로 초기화되지 않습니다. Explore에서 `Loki` 데이터 소스를 선택합니다. Prometheus는 기본 데이터 소스로 유지됩니다.

## 로그 경로와 보관

- 기본 파일: `prothsync/logs/prothsync.json`. 한 줄에 JSON 객체 하나를 기록합니다. 예외 스택의 줄바꿈은 JSON 안에 이스케이프되어 하나의 이벤트로 보존됩니다.
- `LOG_DIR`로 경로를 변경할 수 있습니다. IDE와 Compose 양쪽에 **같은 절대 경로**를 설정하세요. 예: `C:/Users/p9908/SSAFY/ProthSync/prothsync/logs`. Compose의 `.env`가 IDE 프로세스에 자동 주입되지는 않습니다.
- `APP_ENV`는 로그의 `environment` 필드에 들어가며 기본값은 `local`입니다. 백엔드 실행 환경에 설정합니다.
- 로컬 파일은 10MB 또는 날짜 변경 시 `logs/archive/`로 압축 회전합니다. 아카이브는 최대 7일, 총 1GB를 기준으로 정리합니다. 현재 쓰는 활성 파일은 이 아카이브 용량 제한과 별개입니다.
- Loki 보관 기간은 7일(`168h`)입니다. Compactor가 주기적으로 정리하므로 만료 즉시 디스크에서 삭제되는 것은 아닙니다.
- Loki 데이터와 Alloy의 파일 읽기 위치는 각각 Docker 볼륨에 보존됩니다. `docker compose down` 후에도 유지되며, `down -v`는 기존 DB를 포함한 볼륨을 삭제하므로 로그 재시작 용도로 사용하지 마세요.
- Alloy는 활성 파일만 수집하고 압축 아카이브는 재수집하지 않습니다. Alloy 중단 중 파일이 회전하면 아카이브로 이동한 미수집 로그는 자동 복구되지 않습니다. 상시 운영에서는 수집기 상태를 감시하고, 필요하면 컨테이너 로그 수집/전송 버퍼를 설계하세요.

라벨은 `service_name`, `environment`, `level` 위주로 사용합니다. 메시지, 스택, 사용자 ID, 요청 ID를 인덱스 라벨로 추가하지 않습니다. JSON 원문은 그대로 저장하므로 조회할 때 필드를 파싱할 수 있습니다.

## 조회

전체 애플리케이션 로그:

```logql
{service_name="prothsync"}
```

로컬 오류 로그:

```logql
{service_name="prothsync", environment="local", level="ERROR"}
```

기존 AOP가 출력하는 느린 API 경고:

```logql
{service_name="prothsync"} |= "SLOW API DETECTED"
```

최근 5분 오류 이벤트 수(향후 알림 조건):

```logql
sum(count_over_time({service_name="prothsync", environment="local", level="ERROR"}[5m]))
```

이는 요청 실패 건수가 아니라 로그 이벤트 수입니다. 기존 AOP와 전역 예외 처리기가 같은 예외를 각각 기록할 수 있습니다. 알림 집계/AI 분석 시 중복 제거가 필요합니다. 유효성 검사 등 예상 가능한 예외도 현재 AOP에서는 `ERROR`로 기록될 수 있으므로, 운영 알림 도입 시 예상 오류와 서버 장애의 분류를 조정하세요.

## 반복되는 인증 실패(401)의 호출 출처 확인

`JwtAuthenticationEntryPoint`의 경고에는 콘솔과 JSON 파일 양쪽에 요청 식별 정보가 기록됩니다. 적용하려면 백엔드를 재시작해야 하며, 이미 저장된 과거 로그에 새 필드가 추가되지는 않습니다.

| JSON 필드 | 확인할 내용 |
| --- | --- |
| `event` | `authentication_required` |
| `http_method`, `request_path` | 어떤 메서드로 어느 URL을 호출했는지 |
| `client_ip` | 서버가 관찰한 접속 IP (`getRemoteAddr`) |
| `user_agent` | 브라우저/도구가 전달한 User-Agent. 출처를 추정하는 단서이며 인증된 신원은 아닙니다. |
| `authorization_present` | Authorization 헤더가 비어 있지 않은지 |
| `bearer_token_present` | `Bearer ` 뒤에 값이 있는지. 토큰의 유효성/만료 여부를 뜻하지는 않습니다. |
| `dispatcher_type`, `original_request_path` | `/error` 오류 디스패치라면 원래 요청 경로 확인 |
| `auth_exception` | 인증 예외의 클래스 이름 |

토큰·쿠키·쿼리 문자열·본문·Referer·예외 메시지는 이 로그에 기록하지 않습니다. 요청 메타데이터는 줄바꿈을 제거하고 각 값의 길이를 제한합니다. 프록시를 경유하면 `client_ip`에 프록시 주소가 보일 수 있으며, 신뢰할 프록시 구성이 없는 상태에서 `X-Forwarded-For`를 실제 호출자 IP로 간주하지 않습니다.

다음 쿼리를 실행한 뒤 401 경고가 다시 발생할 때 경로와 User-Agent를 확인하세요.

```logql
{service_name="prothsync", level="WARN"}
| json
| event="authentication_required"
| line_format "{{.http_method}} {{.request_path}} | ip={{.client_ip}} | ua={{.user_agent}} | auth={{.authorization_present}} bearer={{.bearer_token_present}} | dispatcher={{.dispatcher_type}} original={{.original_request_path}}"
```

- `authorization_present=false`: 요청에 Authorization 헤더가 없습니다. 호출하는 클라이언트의 로그인/헤더 설정을 확인합니다.
- `authorization_present=true`, `bearer_token_present=false`: Authorization 헤더가 Bearer 토큰 형식이 아닌지 확인합니다.
- `bearer_token_present=true`인데 401: 토큰이 유효한 Access Token인지 확인합니다. 이 필드만으로 만료와 서명 오류 등을 구분할 수는 없습니다.
- `request_path=/error`: `original_request_path`의 경로에서 시작된 요청을 먼저 확인합니다.

브라우저가 의심되면 개발자 도구 Network의 401 요청 URL/시각과 대조합니다. 이 코드는 기존 접근 허용 규칙이나 HTTP 401 응답을 변경하지 않습니다.

## 검증

DB/Redis를 기동하지 않고 실제 Spring Boot JSON 출력과 예외 스택을 검증합니다.

```powershell
.\gradlew.bat test --tests com.prothsync.prothsync.config.StructuredLoggingTest --tests com.prothsync.prothsync.UserTest
```

수집부터 Loki 검색까지는 아래 스크립트로 검증합니다. 활성 로그 파일에 고유한 테스트 `ERROR` 이벤트 한 건을 추가하고 해당 이벤트를 조회합니다. 실제 API에 오류를 발생시키지는 않습니다.

```powershell
.\monitoring\verify-loki.ps1
```

이벤트에는 `logger_name=LOKI_SMOKE_TEST`가 붙습니다. 향후 실제 오류 알림에서 검증 이벤트를 제외하려면 JSON 필터를 추가하세요.

```logql
{service_name="prothsync", level="ERROR"} | json | logger_name != "LOKI_SMOKE_TEST"
```

수집되지 않으면 백엔드 작업 디렉터리/`LOG_DIR`, `logs/prothsync.json` 생성 여부, Alloy UI의 컴포넌트 상태, 아래 컨테이너 로그를 순서대로 확인합니다.

```powershell
docker compose ps
docker compose logs --tail=100 loki alloy
```

설정 검증 명령(이미지 다운로드와 Docker 엔진 필요):

```powershell
docker compose run --rm --no-deps loki '-config.file=/etc/loki/config.yaml' '-verify-config=true'
docker compose run --rm --no-deps alloy validate /etc/alloy/config.alloy
```

### 컨테이너는 실행 중인데 새 로그가 안 보일 때

Grafana의 `No logs volume available`은 선택한 쿼리/시간 범위에 표시할 로그량이 없다는 뜻일 수 있습니다. Docker 저장 볼륨이 없다는 뜻으로 해석하지 말고, 실제 파일 수집부터 확인하세요. Live는 연결 이후 새로 들어오는 로그를 확인하는 기능이므로 백엔드가 실행 중인지도 확인해야 합니다.

2026-11-12 로컬 환경에서 앱 파일은 약 235KB까지 증가했지만 Alloy의 읽기 위치는 413바이트에서 멈춰 최근 로그가 조회되지 않는 현상이 있었습니다. 파일 경로와 마운트는 일치했고, 전송 실패/드롭 카운터는 0이었습니다. Alloy만 재시작하자 현재 파일의 누적 로그와 이후 추가 로그가 수집됐습니다. 정확한 정지 계기는 확인되지 않았으므로, 파일 회전/호스트 절전/재시작 후에는 아래 검증을 수행하세요.

```powershell
# 백엔드 디렉터리에서 파일 크기와 Alloy 읽기 위치를 비교합니다.
Get-Item .\logs\prothsync.json | Select-Object Length, LastWriteTime
$metrics = (Invoke-WebRequest http://localhost:12345/metrics).Content
$metrics -split "`n" | Select-String '^loki_source_file_(file_bytes|read_bytes|read_lines)'

# 파일은 증가하는데 읽기 위치가 멈춰 있다면 수집기만 재시작합니다.
docker compose restart alloy
.\monitoring\verify-loki.ps1
```

이 명령은 앱/DB/Loki를 재시작하거나 볼륨을 삭제하지 않습니다. 현재 파일의 읽기 위치를 이어서 복구하며, 이미 압축 아카이브로 이동한 로그까지 복구하는 것은 아닙니다. 복구 후 Grafana에서 최근 15분, `{service_name="prothsync"}`로 조회하고, Live를 켠 상태에서 새 API 요청을 발생시키세요. 다시 멈춘다면 재시작 전 파일 크기·읽기 위치·Alloy 로그를 확보해 원인을 추적합니다.

## 추후 n8n → AI → Discord 연동

여러 오류를 직접 발생시키며 파이프라인을 시험하려면 [오류 시뮬레이션 가이드](error-simulation.md)를 참고하세요.

이번 구성에는 n8n 워크플로, AI 호출, Discord 발송, 외부 웹훅을 등록하지 않았습니다. 권장 확장 경로는 다음과 같습니다.

```text
Grafana 오류 알림 → n8n Webhook → Loki에서 전후 로그 조회 → AI 분석 → Discord Webhook
```

1. Grafana에서 오류 집계 쿼리를 평가하고 임계값 초과 시 n8n Webhook을 호출합니다. Loki 자체가 임의의 n8n 웹훅으로 오류를 자동 전송하는 것은 아닙니다.
2. n8n이 `GET /loki/api/v1/query_range`로 해당 서비스/환경/시간대의 오류와 주변 로그를 조회합니다. `query`, `start`, `end`, `direction`, `limit`를 지정하며 `start`/`end`는 RFC3339 또는 나노초 Unix 시간 문자열을 사용할 수 있습니다.
3. 응답의 `data.result[].values`에는 `[나노초 타임스탬프, JSON 로그 문자열]`이 들어옵니다. 로그 문자열을 JSON으로 파싱하면 `message`, `logger_name`, `stack_trace`, `level`, `environment`를 AI 입력에 사용할 수 있습니다.
4. 같은 오류의 반복 알림을 묶고, 마지막 처리 시각/오류 지문을 저장해 중복 분석과 중복 발송을 줄입니다. 알림 복구(`resolved`)도 구분합니다.
5. AI에는 시간 범위와 로그 수를 제한한 입력을 보내고, 추정 원인·근거·확인할 항목을 Discord 메시지로 작성합니다. 전송 전에 토큰·비밀번호·개인정보를 제거하고, 로그 내용은 분석 데이터로만 취급합니다.

접속 주소는 n8n 실행 위치에 따라 달라집니다.

| n8n 실행 위치 | Loki 기본 주소 |
| --- | --- |
| 호스트 프로세스 | `http://localhost:3100` |
| 같은 `db-net`의 컨테이너 | `http://loki:3100` |

Loki는 현재 로컬 개발용으로 인증 없이 실행하며 호스트 포트는 `127.0.0.1`에만 바인딩합니다. 외부 n8n 서버를 연결하려면 인증 프록시/TLS 또는 사설 네트워크를 먼저 구성합니다. 운영 데이터 저장은 별도의 객체 스토리지와 가용성 설계를 검토하세요.

## 참고

- [Spring Boot 구조화 로깅](https://docs.spring.io/spring-boot/3.5/reference/features/logging.html)
- [Alloy 파일 수집](https://grafana.com/docs/alloy/latest/reference/components/loki/loki.source.file/)
- [Loki 파일시스템 구성](https://grafana.com/docs/loki/latest/configure/examples/configuration-examples/)
- [Loki 보관 기간](https://grafana.com/docs/loki/latest/operations/storage/retention/)
- [Loki HTTP API](https://grafana.com/docs/loki/latest/reference/loki-http-api/)
