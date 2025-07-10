@echo off
echo === Simple Migration for BookShop Billing ===
echo.

echo Running database migration...
mysql -h 127.0.0.1 -P 3306 -u root -proot@1234 bookshop < simple-migration.sql

echo.
echo Migration completed!
echo.
echo Now rebuild and deploy your application:
echo 1. .\build-web.ps1
echo 2. .\deploy-tomcat.ps1 -StartTomcat
echo.
pause 