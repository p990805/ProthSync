param(
    [string]$LokiUrl = 'http://localhost:3100',
    [string]$LogDirectory = $(if ($env:LOG_DIR) { $env:LOG_DIR } else { Join-Path $PSScriptRoot '../logs' })
)

$ErrorActionPreference = 'Stop'
$probeId = [guid]::NewGuid().ToString()
$now = [DateTimeOffset]::UtcNow
$logFile = Join-Path $LogDirectory 'prothsync.json'

$ready = $false
for ($attempt = 0; $attempt -lt 20; $attempt++) {
    try {
        Invoke-RestMethod -Uri "$LokiUrl/ready" -TimeoutSec 5 | Out-Null
        $ready = $true
        break
    } catch {
        Start-Sleep -Seconds 2
    }
}
if (-not $ready) {
    throw "Loki is not ready at $LokiUrl. Start it with docker compose up -d loki alloy grafana."
}
New-Item -ItemType Directory -Path $LogDirectory -Force | Out-Null
$event = [ordered]@{
    '@timestamp' = $now.ToString('o')
    '@version' = '1'
    service_name = 'prothsync'
    environment = $(if ($env:APP_ENV) { $env:APP_ENV } else { 'local' })
    level = 'ERROR'
    level_value = 40000
    logger_name = 'LOKI_SMOKE_TEST'
    thread_name = 'monitoring-verification'
    message = "Loki pipeline verification $probeId"
    stack_trace = "java.lang.IllegalStateException: Synthetic monitoring probe $probeId"
}

# UTF-8 without BOM; JSON remains a single physical line, including stack traces.
$line = ($event | ConvertTo-Json -Compress) + [Environment]::NewLine
[IO.File]::AppendAllText([IO.Path]::GetFullPath($logFile), $line, [Text.UTF8Encoding]::new($false))

$query = [Uri]::EscapeDataString('{service_name="prothsync", level="ERROR"} |= "' + $probeId + '"')
$start = [Uri]::EscapeDataString($now.AddMinutes(-1).ToString('o'))
for ($attempt = 0; $attempt -lt 20; $attempt++) {
    $end = [Uri]::EscapeDataString([DateTimeOffset]::UtcNow.ToString('o'))
    $response = Invoke-RestMethod -Uri "$LokiUrl/loki/api/v1/query_range?query=$query&start=$start&end=$end&limit=10" -TimeoutSec 5
    if ($response.status -eq 'success' -and @($response.data.result).Count -gt 0) {
        Write-Output "PASS: Alloy collected the ERROR event and Loki returned it. Probe: $probeId"
        return
    }
    Start-Sleep -Seconds 2
}

throw "Loki did not return probe $probeId within 40 seconds. Check LOG_DIR and docker compose logs --tail=100 loki alloy."
