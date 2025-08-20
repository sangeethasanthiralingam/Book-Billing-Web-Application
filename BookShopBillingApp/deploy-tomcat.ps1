# BookShop Billing Application - Tomcat Deployment Script
# For Tomcat 11.0.9 at D:\BIT\Tomcat\apache-tomcat-11.0.9-windows-x64\apache-tomcat-11.0.9

param(
    [string]$TomcatPath = "D:\BIT\Tomcat\apache-tomcat-11.0.9-windows-x64\apache-tomcat-11.0.9",
    [switch]$StartTomcat,
    [switch]$StopTomcat,
    [switch]$Clean
)

$ErrorActionPreference = "Stop"

Write-Host "=== BookShop Billing Application - Tomcat Deployment ===" -ForegroundColor Green
Write-Host "Tomcat Path: $TomcatPath" -ForegroundColor Yellow

# Validate Tomcat installation
if (-not (Test-Path $TomcatPath)) {
    Write-Error "Tomcat not found at: $TomcatPath"
    Write-Host "Please verify your Tomcat installation path." -ForegroundColor Red
    exit 1
}

$TomcatBin = Join-Path $TomcatPath "bin"
$TomcatWebapps = Join-Path $TomcatPath "webapps"
$AppName = "bookshop-billing"

# Function to stop Tomcat
function Stop-Tomcat {
    Write-Host "Stopping Tomcat..." -ForegroundColor Yellow
    $StopScript = Join-Path $TomcatBin "shutdown.bat"
    if (Test-Path $StopScript) {
        & $StopScript
        Start-Sleep -Seconds 5
        Write-Host "Tomcat stopped." -ForegroundColor Green
    } else {
        Write-Host "Tomcat shutdown script not found." -ForegroundColor Yellow
    }
}

# Function to start Tomcat
function Start-Tomcat {
    Write-Host "Starting Tomcat..." -ForegroundColor Yellow
    $StartScript = Join-Path $TomcatBin "startup.bat"
    if (Test-Path $StartScript) {
        Start-Process -FilePath $StartScript -WindowStyle Minimized
        Start-Sleep -Seconds 10
        Write-Host "Tomcat started." -ForegroundColor Green
    } else {
        Write-Host "Tomcat startup script not found." -ForegroundColor Yellow
    }
}

# Function to clean previous deployment
function Remove-PreviousDeployment {
    Write-Host "Cleaning previous deployment..." -ForegroundColor Yellow
    $AppDir = Join-Path $TomcatWebapps $AppName
    
    if (Test-Path $AppDir) {
        Remove-Item -Path $AppDir -Recurse -Force
        Write-Host "Removed previous application directory." -ForegroundColor Green
    }
}

# Main deployment process
try {
    # Stop Tomcat if requested or if cleaning
    if ($StopTomcat -or $Clean) {
        Stop-Tomcat
    }
    
    # Clean previous deployment if requested
    if ($Clean) {
        Remove-PreviousDeployment
    }
    
    # Build the web application
    Write-Host "Building web application..." -ForegroundColor Yellow
    $BuildScript = Join-Path $PSScriptRoot "build-web.ps1"
    if (Test-Path $BuildScript) {
        & $BuildScript
        if ($LASTEXITCODE -ne 0) {
            throw "Build failed with exit code: $LASTEXITCODE"
        }
    } else {
        Write-Host "Build script not found. Please run build-web.ps1 first." -ForegroundColor Red
        exit 1
    }
    
    # Check if WebContent directory was created
    $WebContentPath = Join-Path $PSScriptRoot "WebContent"
    if (-not (Test-Path $WebContentPath)) {
        Write-Host "WebContent directory not found at: $WebContentPath" -ForegroundColor Red
        Write-Host "Please ensure the build process completed successfully." -ForegroundColor Yellow
        exit 1
    }
    
    # Copy WebContent to Tomcat webapps
    Write-Host "Deploying to Tomcat..." -ForegroundColor Yellow
    $TargetAppDir = Join-Path $TomcatWebapps $AppName
    
    # Remove existing deployment if it exists
    if (Test-Path $TargetAppDir) {
        Remove-Item -Path $TargetAppDir -Recurse -Force
    }
    
    # Copy the entire WebContent directory to Tomcat webapps
    Copy-Item -Path $WebContentPath -Destination $TargetAppDir -Recurse -Force
    Write-Host "Application deployed to: $TargetAppDir" -ForegroundColor Green
    
    # Start Tomcat if requested
    if ($StartTomcat) {
        Start-Tomcat
    }
    
    Write-Host "`n=== Deployment Summary ===" -ForegroundColor Green
    Write-Host "Application: $AppName" -ForegroundColor White
    Write-Host "Tomcat Path: $TomcatPath" -ForegroundColor White
    Write-Host "Webapps Directory: $TomcatWebapps" -ForegroundColor White
    Write-Host "Application Directory: $TargetAppDir" -ForegroundColor White
    
    if ($StartTomcat) {
        Write-Host "`nAccess your application at:" -ForegroundColor Cyan
        Write-Host "http://localhost:8080/$AppName" -ForegroundColor White
        Write-Host "http://localhost:8080/$AppName/jsp/login.jsp" -ForegroundColor White
    } else {
        Write-Host "`nTo start Tomcat manually:" -ForegroundColor Yellow
        Write-Host "cd `"$TomcatBin`" && startup.bat" -ForegroundColor White
        Write-Host "`nThen access: http://localhost:8080/$AppName" -ForegroundColor Cyan
    }
    
    Write-Host "`n=== Deployment Completed Successfully ===" -ForegroundColor Green
    
} catch {
    Write-Error "Deployment failed: $($_.Exception.Message)"
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Ensure Tomcat is not running on port 8080" -ForegroundColor White
    Write-Host "2. Check that you have write permissions to Tomcat webapps directory" -ForegroundColor White
    Write-Host "3. Verify the build process completed successfully" -ForegroundColor White
    Write-Host "4. Check Tomcat logs at: $TomcatPath\logs\catalina.out" -ForegroundColor White
    exit 1
} 