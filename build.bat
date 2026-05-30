@echo off
echo === Compilando con Maven ===
call mvn clean package -q
if errorlevel 1 (
    echo ERROR: Maven falló
    pause
    exit /b 1
)

echo === Generando .exe con jpackage ===
jpackage ^
  --type exe ^
  --name "Rico en Inversiones" ^
  --app-version "1.0.0" ^
  --vendor "TheImperialOne" ^
  --input exportable ^
  --main-jar Rico_en_inversiones-0.0.1-SNAPSHOT.jar ^
  --main-class main.MainProcess ^
  --icon Rico-en-inversiones_logo.ico ^
  --dest dist ^
  --java-options "-Duser.dir=$APPDIR" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group "Rico en Inversiones" ^
  --win-upgrade-uuid "2dcf72b0-f002-4e5e-ad9d-ff186dafe07f"

if errorlevel 1 (
    echo ERROR: jpackage falló
    pause
    exit /b 1
)

echo === Listo! El instalador está en /dist ===
pause