@echo off
chcp 65001 >nul
echo ========================================
echo Product Data Import Script
echo ========================================
echo.
echo Please ensure the following services are running:
echo 1. MySQL Database (port 3306)
echo 2. Elasticsearch (port 9200)
echo 3. Nacos (port 8848)
echo 4. search-service (port 8087)
echo.
pause
echo.
echo Starting data import...
echo.

REM Clear and reimport data using PowerShell
powershell -Command "Invoke-RestMethod -Uri 'http://localhost:8087/admin/import/reload' -Method Post"

echo.
echo ========================================
echo Data import completed!
echo ========================================
echo.
echo You can verify the import results by:
echo 1. Visit http://localhost:8087/doc.html for API documentation
echo 2. Use GET http://192.168.80.129:9200/hmall/_search to check ES data
echo 3. Test search functionality using search APIs
echo.
pause