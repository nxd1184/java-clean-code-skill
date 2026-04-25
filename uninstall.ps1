$ErrorActionPreference = 'Stop'
$SkillDest = Join-Path $env:USERPROFILE '.claude\skills\java-clean-code'

if (-not (Test-Path $SkillDest)) {
    Write-Host "Nothing to remove at $SkillDest"
    exit 0
}

$item = Get-Item $SkillDest -Force
if ($item.LinkType -ne 'SymbolicLink') {
    Write-Error "$SkillDest is not a symlink. Remove manually."
    exit 1
}

Remove-Item $SkillDest -Force
Write-Host "Removed: $SkillDest"
