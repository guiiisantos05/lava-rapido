$ErrorActionPreference = 'Stop'
$classes = 'out'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName
javac -encoding UTF-8 -d $classes $sources
java -cp $classes br.com.lavarapido.Main
