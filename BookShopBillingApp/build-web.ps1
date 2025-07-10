# PowerShell Build Script for BookShop Billing Web Application
Write-Host "Building BookShop Billing Web Application..." -ForegroundColor Green

# Create bin directory if it doesn't exist
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Create WEB-INF/classes directory for web deployment
if (!(Test-Path "WebContent\WEB-INF\classes")) {
    New-Item -ItemType Directory -Path "WebContent\WEB-INF\classes" -Force | Out-Null
}

# Create lib directory if it doesn't exist
if (!(Test-Path "lib")) {
    New-Item -ItemType Directory -Path "lib" | Out-Null
}

# Find Java installation
$javaPath = Get-Command java -ErrorAction SilentlyContinue
if ($javaPath) {
    $javaHome = Split-Path (Split-Path $javaPath.Source)
    $javacPath = Join-Path $javaHome "bin\javac.exe"
    
    if (Test-Path $javacPath) {
        Write-Host "Using Java compiler: $javacPath" -ForegroundColor Yellow
        
        # Build classpath with lib JARs
        $libJars = @()
        if (Test-Path "lib") {
            $libJars = Get-ChildItem -Path "lib" -Filter "*.jar" | ForEach-Object { $_.FullName }
        }
        $classpath = "bin"
        if ($libJars.Count -gt 0) {
            $classpath = "bin;" + ($libJars -join ";")
            Write-Host "Including JARs in classpath: $($libJars.Count) files" -ForegroundColor Yellow
        }
        
        # Compile all Java files using sources.txt
        Write-Host "Compiling all Java files from sources.txt..." -ForegroundColor Yellow
        $sourceFiles = Get-Content sources.txt
        & $javacPath -d bin -cp $classpath $sourceFiles
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling Java files" -ForegroundColor Red; exit 1 }
        
        # Create WEB-INF/lib directory for JARs
        if (!(Test-Path "WebContent\WEB-INF\lib")) {
            New-Item -ItemType Directory -Path "WebContent\WEB-INF\lib" -Force | Out-Null
        }
        
        # Copy JAR files to WEB-INF/lib for deployment, EXCLUDING jakarta.servlet-api-6.0.0.jar
        if (Test-Path "lib") {
            Write-Host "Copying JAR files to WEB-INF/lib (excluding servlet-api)..." -ForegroundColor Yellow
            Get-ChildItem -Path "lib" -Filter "*.jar" | Where-Object { $_.Name -ne "jakarta.servlet-api-6.0.0.jar" } | ForEach-Object {
                Copy-Item -Path $_.FullName -Destination "WebContent\WEB-INF\lib\" -Force
            }
        }
        
        # Copy compiled classes to WEB-INF/classes for web deployment
        Write-Host "Copying classes to WEB-INF/classes..." -ForegroundColor Yellow
        Copy-Item -Path "bin\*" -Destination "WebContent\WEB-INF\classes\" -Recurse -Force
        
        # Copy all web content (JSPs, HTML, images, etc.) to deployment directory
        Write-Host "Copying all WebContent files for deployment..." -ForegroundColor Yellow
        if (Test-Path "dist") { Remove-Item -Recurse -Force "dist" }
        New-Item -ItemType Directory -Path "dist" | Out-Null
        Copy-Item -Path "WebContent\*" -Destination "dist\" -Recurse -Force
        
        Write-Host "Web application build completed!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Cyan
        Write-Host "1. Deploy to Tomcat: Copy bookshop-billing to Tomcat webapps folder" -ForegroundColor White
        Write-Host "2. Start Tomcat: Run startup.bat in Tomcat bin folder" -ForegroundColor White
        Write-Host "3. Access in browser: http://localhost:8080/bookshop-billing/" -ForegroundColor White
        Write-Host ""
        
    } else {
        Write-Host "Error: javac not found at $javacPath" -ForegroundColor Red
        Write-Host "Please ensure Java Development Kit (JDK) is installed" -ForegroundColor Red
    }
} else {
    Write-Host "Error: Java not found in PATH" -ForegroundColor Red
    Write-Host "Please ensure Java is installed and in PATH" -ForegroundColor Red
} 