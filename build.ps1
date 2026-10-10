# Builds the React app and packages everything into one runnable JAR.
$ErrorActionPreference = "Stop"

Push-Location frontend
npm ci
if ($LASTEXITCODE -ne 0) { throw "npm ci failed" }
npm run build
if ($LASTEXITCODE -ne 0) { throw "npm run build failed" }
Pop-Location

$static = "backend/src/main/resources/static"
if (Test-Path $static) { Remove-Item -Recurse -Force $static }
Copy-Item -Recurse frontend/dist $static

Push-Location backend
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -ne 0) { throw "Maven build failed" }
Pop-Location

Write-Host "Done. The JAR is in backend/target/"