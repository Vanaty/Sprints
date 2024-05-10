@echo off
setlocal enabledelayedexpansion

:: Déclaration des variables
set "jar_name=framework_servlet"
set "work_dir=C:\Users\itu\Documents\GitHub\Sprint\FrameWork\P01"
set "servlet_path=C:\apache-tomcat-11.0.0-M4\lib\servlet-api.jar"
set "temp=%work_dir%\temp"
set "lib=%work_dir%\lib"
set "src=%work_dir%\src"

:: Effacer le dossier [temp]
if exist "%temp%" (
    rd /s /q "%temp%"
)

:: Créer la structure de dossier
mkdir "%temp%\classes"

:: Compilation des fichiers .java dans src avec les options suivantes
:: Note: Assurez-vous que le chemin vers le compilateur Java (javac) est correctement configuré dans votre variable d'environnement PATH.
:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%src%\*.java" > sources.txt
:: Exécuter la commande javac
javac -d "%temp%\classes" -cp "%servlet_path%" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt

cd "%temp%\classes"
jar cvf "%lib%\%jar_name%.jar" *

echo Cretion Jar Terminer.
pause