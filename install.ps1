$ErrorActionPreference = 'Stop'
$RepoDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$SkillSrc  = Join-Path $RepoDir '.claude\skills\java-clean-code'
$SkillRoot = Join-Path $env:USERPROFILE '.claude\skills'
$SkillDest = Join-Path $SkillRoot 'java-clean-code'

New-Item -ItemType Directory -Force -Path $SkillRoot | Out-Null

if (Test-Path $SkillDest) {
    $item = Get-Item $SkillDest -Force
    if ($item.LinkType -ne 'SymbolicLink') {
        Write-Error "$SkillDest exists and is not a symlink. Remove it first."
        exit 1
    }
    Remove-Item $SkillDest -Force
}

New-Item -ItemType SymbolicLink -Path $SkillDest -Target $SkillSrc | Out-Null
Write-Host "Installed: $SkillDest -> $SkillSrc"
Write-Host "Invoke in Claude Code via: /skills java-clean-code (or implicit load on explicit request)."
