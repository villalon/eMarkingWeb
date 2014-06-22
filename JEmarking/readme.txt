#-------------------------------------------------------------------------------
# This file is part of Moodle - http://moodle.org/
# 
# Moodle is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Moodle is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Moodle.  If not, see <http://www.gnu.org/licenses/>.
# 
# @package cl.uai.webcursos.emarking
# @copyright 2014 Jorge Villalón {@link http://www.villalon.cl}
# @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
#-------------------------------------------------------------------------------
Si no funciona en el eclipse, ver que est� configurado el build path y la aplicaci�n tenga
el run configuration correcto.

SE EJECUTA EL JAR

jEmarking.jar -render "c:\target\target.pdf"            	(un archivo)
jEmarking.jar -render "c:\target\"                 (una carpeta completa)

DEVUELVE:

Para los normales  <Status> <direccion> <pagina EN el pdf> <alumno> <p�gina en el examen> <curso> <esbackpage> 
Para los errores:  <Status> <direccion> <pagina en el pdf> <error>  <es backpage?>

EJEMPLO DE OUTPUT:
Found 1 pdf files in folder.
log4j:WARN No appenders could be found for logger (net.sf.ghost4j.Ghostscript).
log4j:WARN Please initialize the log4j system properly.
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 0 33989 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 1 33989 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 2 33989 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 3 33989 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 4 33989 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 5 33989 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 6 33989 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 7 33989 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 8 35043 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 9 35043 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 10 35043 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 11 35043 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 12 35043 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 13 35043 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 14 35043 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 15 35043 4 1
E C:\emarkingdata\1\Examen_Programacion_S3.PDF 16 UnknownError 0
E C:\emarkingdata\1\Examen_Programacion_S3.PDF 17 UnknownError 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 18 33557 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 19 33557 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 20 33557 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 21 33557 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 22 33557 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 23 33557 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 24 33447 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 25 33447 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 26 33447 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 27 33447 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 28 33447 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 29 33447 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 30 33447 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 31 33447 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 32 34730 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 33 34730 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 34 34730 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 35 34730 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 36 34730 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 37 34730 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 38 34730 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 39 34730 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 40 34303 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 41 34303 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 42 34303 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 43 34303 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 44 34303 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 45 34303 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 46 34303 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 47 34303 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 48 34857 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 49 34857 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 50 34857 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 51 34857 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 52 34857 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 53 34857 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 54 34857 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 55 34857 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 56 34506 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 57 34506 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 58 34506 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 59 34506 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 60 34506 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 61 34506 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 62 34506 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 63 34506 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 64 35172 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 65 35172 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 66 35172 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 67 35172 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 68 35172 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 69 35172 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 70 35172 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 71 35172 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 72 34367 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 73 34367 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 74 34367 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 75 34367 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 76 34367 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 77 34367 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 78 34367 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 79 34367 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 80 33382 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 81 33382 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 82 33382 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 83 33382 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 84 33382 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 85 33382 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 86 33382 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 87 33382 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 88 33421 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 89 33421 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 90 33421 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 91 33421 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 92 33421 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 93 33421 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 94 33421 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 95 33421 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 96 35173 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 97 35173 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 98 35173 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 99 35173 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 100 35173 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 101 35173 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 102 35207 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 103 35207 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 104 35207 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 105 35207 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 106 35207 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 107 35207 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 108 35207 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 109 35207 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 110 34976 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 111 34976 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 112 34976 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 113 34976 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 114 34976 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 115 34976 3 1
E C:\emarkingdata\1\Examen_Programacion_S3.PDF 116 QrNotFoundOrRead 0
E C:\emarkingdata\1\Examen_Programacion_S3.PDF 117 QrNotFoundOrRead 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 118 35198 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 119 35198 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 120 35198 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 121 35198 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 122 35198 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 123 35198 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 124 35198 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 125 35198 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 126 34733 1 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 127 34733 1 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 128 34733 2 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 129 34733 2 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 130 34733 3 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 131 34733 3 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 132 34733 4 0
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 133 34733 4 1
OK C:\emarkingdata\1\Examen_Programacion_S3.PDF 134 34019 1 0
