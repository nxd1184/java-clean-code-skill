# Package the java-clean-code skill into a zip ready for Claude upload.
# Output: dist\java-clean-code.zip with SKILL.md at the zip root.
$ErrorActionPreference = 'Stop'

$RepoDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$SkillSrc = Join-Path $RepoDir '.claude\skills\java-clean-code'
$DistDir  = Join-Path $RepoDir 'dist'
$ZipPath  = Join-Path $DistDir 'java-clean-code.zip'

if (-not (Test-Path $SkillSrc)) {
    Write-Error "Skill folder not found at $SkillSrc"
    exit 1
}

New-Item -ItemType Directory -Force -Path $DistDir | Out-Null
if (Test-Path $ZipPath) { Remove-Item $ZipPath -Force }

# Compress-Archive with a trailing \* puts the folder *contents* at zip root
# (so SKILL.md sits at the top, not under java-clean-code\).
$items = Get-ChildItem -Path $SkillSrc -Force |
    Where-Object { $_.Name -ne '.DS_Store' }

Compress-Archive -Path $items.FullName -DestinationPath $ZipPath

Write-Host "Packaged: $ZipPath"
Write-Host ""
Write-Host "Contents:"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($ZipPath)
try {
    $zip.Entries | ForEach-Object { Write-Host "  $($_.FullName)" }
} finally {
    $zip.Dispose()
}
