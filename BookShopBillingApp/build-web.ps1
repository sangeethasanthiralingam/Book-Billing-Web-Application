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
        
        # Compile all Java files including servlets
        Write-Host "Compiling all Java files..." -ForegroundColor Yellow
        
        # Compile model classes first
        & $javacPath -d bin src\model\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling model classes" -ForegroundColor Red; exit 1 }
        
        # Compile util classes
        & $javacPath -d bin -cp $classpath src\util\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling util classes" -ForegroundColor Red; exit 1 }
        
        # Compile DAO classes
        & $javacPath -d bin -cp $classpath src\dao\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling DAO classes" -ForegroundColor Red; exit 1 }
        
        # Compile service classes
        & $javacPath -d bin -cp $classpath src\service\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling service classes" -ForegroundColor Red; exit 1 }
        
        # Compile factory classes
        & $javacPath -d bin -cp $classpath src\factory\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling factory classes" -ForegroundColor Red; exit 1 }
        
        # Compile builder classes
        & $javacPath -d bin -cp $classpath src\builder\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling builder classes" -ForegroundColor Red; exit 1 }
        
        # Compile controller classes (servlets) with servlet API
        & $javacPath -d bin -cp $classpath src\controller\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling controller classes" -ForegroundColor Red; exit 1 }
        
        # Compile demo classes
        & $javacPath -d bin -cp $classpath src\demo\*.java
        if ($LASTEXITCODE -ne 0) { Write-Host "Error compiling demo classes" -ForegroundColor Red; exit 1 }
        
        # Create WEB-INF/lib directory for JARs
        if (!(Test-Path "WebContent\WEB-INF\lib")) {
            New-Item -ItemType Directory -Path "WebContent\WEB-INF\lib" -Force | Out-Null
        }
        
        # Copy JAR files to WEB-INF/lib for deployment
        if (Test-Path "lib") {
            Write-Host "Copying JAR files to WEB-INF/lib..." -ForegroundColor Yellow
            Copy-Item -Path "lib\*.jar" -Destination "WebContent\WEB-INF\lib\" -Force
        }
        
        # Copy compiled classes to WEB-INF/classes for web deployment
        Write-Host "Copying classes to WEB-INF/classes..." -ForegroundColor Yellow
        Copy-Item -Path "bin\*" -Destination "WebContent\WEB-INF\classes\" -Recurse -Force
        
        Write-Host "Web application build completed!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Cyan
        Write-Host "1. Deploy to Tomcat: Copy BookShopBillingApp to Tomcat webapps folder" -ForegroundColor White
        Write-Host "2. Start Tomcat: Run startup.bat in Tomcat bin folder" -ForegroundColor White
        Write-Host "3. Access in browser: http://localhost:8080/BookShopBillingApp/" -ForegroundColor White
        Write-Host ""
        
    } else {
        Write-Host "Error: javac not found at $javacPath" -ForegroundColor Red
        Write-Host "Please ensure Java Development Kit (JDK) is installed" -ForegroundColor Red
    }
} else {
    Write-Host "Error: Java not found in PATH" -ForegroundColor Red
    Write-Host "Please ensure Java is installed and in PATH" -ForegroundColor Red
} 