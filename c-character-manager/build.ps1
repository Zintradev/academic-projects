# build.ps1 - PowerShell Build Script for Game Character Manager
# Detects available compilers (gcc, clang, cl) and compiles the project

$compiler = ""
if (Get-Command "gcc" -ErrorAction SilentlyContinue) {
    $compiler = "gcc"
} elseif (Get-Command "clang" -ErrorAction SilentlyContinue) {
    $compiler = "clang"
} elseif (Get-Command "cl" -ErrorAction SilentlyContinue) {
    $compiler = "cl"
}

if (-not $compiler) {
    Write-Host "Error: No suitable C compiler (gcc, clang, or cl) was found in your PATH." -ForegroundColor Red
    Write-Host "Please install GCC (via MinGW), Clang, or MSVC Build Tools and try again." -ForegroundColor Yellow
    Exit 1
}

Write-Host "Found C compiler: $compiler" -ForegroundColor Green

# Create output folder if not existing
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

if ($compiler -eq "cl") {
    Write-Host "Compiling with MSVC (cl.exe)..." -ForegroundColor Cyan
    # Compile main app
    & cl /Fe:bin\character_manager.exe /Iinclude src\main.c src\character.c src\storage.c src\ui.c /Fo:bin\
    # Compile tests
    & cl /Fe:bin\unit_tests.exe /Iinclude tests\test_character.c src\character.c /Fo:bin\
} else {
    Write-Host "Compiling with $compiler..." -ForegroundColor Cyan
    # Compile main app
    & $compiler -Wall -Wextra -std=c99 -Iinclude -o bin\character_manager.exe src\main.c src\character.c src\storage.c src\ui.c
    # Compile tests
    & $compiler -Wall -Wextra -std=c99 -Iinclude -o bin\unit_tests.exe tests\test_character.c src\character.c
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build Succeeded!" -ForegroundColor Green
    Write-Host "Executables generated in bin/" -ForegroundColor Green
    Write-Host "Run '.\bin\character_manager.exe' to execute the program." -ForegroundColor Cyan
    Write-Host "Run '.\bin\unit_tests.exe' to execute the unit tests." -ForegroundColor Cyan
} else {
    Write-Host "Build Failed!" -ForegroundColor Red
}
