@echo off
setlocal

:: Ambil path direktori dari file .bat
set "TARGET_FOLDER=%~dp0"

:: Hapus karakter backslash terakhir (opsional, agar rapi)
set "TARGET_FOLDER=%TARGET_FOLDER:~0,-1%"

:: Nama file output
set "OUTPUT_FILE=%TARGET_FOLDER%\struktur_folder.txt"

:: Generate struktur folder + file
tree "%TARGET_FOLDER%" /F > "%OUTPUT_FILE%"

echo Struktur folder telah disimpan ke:
echo %OUTPUT_FILE%
pause
