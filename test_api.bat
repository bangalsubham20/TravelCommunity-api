@echo off
echo Registering...
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d @register.json
echo.
echo.
echo Logging in...
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d @register.json
echo.
