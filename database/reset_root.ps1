# Requires elevation (admin) for Stop-Service / Start-Service

$ErrorActionPreference = 'Stop'

$iniFile = 'C:\ProgramData\MySQL\MySQL Server 8.0\my.ini'
$initFile = 'D:\vehical\database\reset_root.sql'

if (-not (Test-Path $iniFile)) { throw "MySQL ini not found: $iniFile" }
if (-not (Test-Path $initFile)) { throw "init SQL not found: $initFile" }

Write-Host 'Stopping MySQL80 service...' -ForegroundColor Cyan
Stop-Service -Name MySQL80 -Force
Start-Sleep -Seconds 3

# Verify service stopped
$svc = Get-Service -Name MySQL80
if ($svc.Status -ne 'Stopped') {
  Write-Host 'MySQL80 did not stop cleanly; attempting taskkill...' -ForegroundColor Yellow
  Get-Process mysqld -ErrorAction SilentlyContinue | Stop-Process -Force
  Start-Sleep -Seconds 2
}

Write-Host 'Starting MySQL in temporary mode with init-file (no network)...' -ForegroundColor Cyan
$mysqld = (Get-Command mysqld).Source
& $mysqld --defaults-file="$iniFile" --init-file="$initFile" --skip-networking 2>&1 | Out-Null
# mysqld runs in the foreground; wait ~10s then cleanly kill via mysqladmin shutdown
$ts = Get-Date
do {
  Start-Sleep -Seconds 1
  try {
    $env:MYSQL_PWD = 'root'
    mysql -u root -S "" -e "SELECT 1;" 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) { break }
  } catch {}
} while ((Get-Date) - $ts | ForEach-Object { $_.TotalSeconds -lt 18 })
$env:MYSQL_PWD = $null

Write-Host 'Stopping temporary MySQL instance...' -ForegroundColor Cyan
Get-Process mysqld -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 3

Write-Host 'Starting MySQL80 service...' -ForegroundColor Cyan
Start-Service -Name MySQL80
Start-Sleep -Seconds 3
$svc = Get-Service -Name MySQL80
Write-Host "MySQL80 status: $($svc.Status)" -ForegroundColor Green

Write-Host 'Verifying root login with password=root...' -ForegroundColor Cyan
$env:MYSQL_PWD = 'root'
mysql -u root -e "SELECT VERSION() AS mysql_version, NOW() AS now_ts;" 2>&1
$ok = ($LASTEXITCODE -eq 0)
$env:MYSQL_PWD = $null
if ($ok) { Write-Host 'SUCCESS: root password set to "root".' -ForegroundColor Green }
else { throw 'root password reset FAILED.' }
