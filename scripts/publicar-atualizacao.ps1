param(
    [Parameter(Mandatory = $true)]
    [string]$Version,
    [string]$Notes = "Atualização do Leonida Companion com novidades e melhorias."
)

$ErrorActionPreference = "Stop"
$Repo = "gtpgramiscelli/leonida-vi-companion"
$Tag = if ($Version.StartsWith("v")) { $Version } else { "v$Version" }
$ProjectRoot = Split-Path -Parent $PSScriptRoot

$env:JAVA_HOME = "C:\Users\gabri\.jdks\jdk-17.0.21.1+2"
$env:PATH = "$env:JAVA_HOME\bin;C:\Users\gabri\AppData\AndroidCLI;" + $env:PATH

Set-Location $ProjectRoot
Write-Host "1/3 Compilando e testando APK para $Tag..."
.\gradlew.bat testDebugUnitTest assembleDebug --no-daemon
if ($LASTEXITCODE -ne 0) { throw "Falha no build Gradle." }

$apkPath = Join-Path $ProjectRoot "app\build\outputs\apk\debug\app-debug.apk"
if (!(Test-Path $apkPath)) { throw "APK não encontrado em $apkPath" }

Write-Host "2/3 Obtendo credencial do GitHub via Git Credential Manager..."
$credLines = "protocol=https`nhost=github.com`n`n" | git credential fill 2>$null
$token = ($credLines | Where-Object { $_ -like "password=*" } | Select-Object -First 1) -replace "^password=", ""
if ([string]::IsNullOrWhiteSpace($token)) { throw "Nenhuma credencial GitHub encontrada." }

$headers = @{
    "Authorization" = "Bearer $token"
    "Accept"        = "application/vnd.github+json"
    "User-Agent"    = "LeonidaCompanion-Publisher"
}

Write-Host "3/3 Publicando Release $Tag em $Repo..."
$bodyObj = @{
    tag_name = $Tag
    name     = "Leonida Companion $Tag"
    body     = $Notes
    draft    = $false
    prerelease = $false
} | ConvertTo-Json -Depth 4

$release = Invoke-RestMethod -Method Post -Uri "https://api.github.com/repos/$Repo/releases" -Headers $headers -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($bodyObj))

$assetName = "leonida-vi-$Tag.apk"
$uploadUrl = ($release.upload_url -replace "\{\?name,label\}$", "") + "?name=$assetName"
$uploadHeaders = @{
    "Authorization" = "Bearer $token"
    "Accept"        = "application/vnd.github+json"
    "User-Agent"    = "LeonidaCompanion-Publisher"
    "Content-Type"  = "application/vnd.android.package-archive"
}

$asset = Invoke-RestMethod -Method Post -Uri $uploadUrl -Headers $uploadHeaders -InFile $apkPath
Write-Host "RELEASE_PUBLISHED: $($release.html_url)"
Write-Host "APK_DOWNLOAD_URL: $($asset.browser_download_url)"
