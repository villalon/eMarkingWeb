eMarking chat collaborative feature
====

Chat node.js para eMarking con almacenamiento en MongoDB

===

Instrucciones para habilitar el chat (hacer esto antes de ejecutar eMarking):

1. En una terminal, iniciar mongoDB (si no lo tiene, ver http://docs.mongodb.org/manual/installation/):
	>cd
	>sudo mongod

2. En otra terminal, iniciar el archivo app.js con node:
	>cd /RUTA/eMarking/eMarkingWeb/war/
	>node /chat/app.js

	OBS: los módulos de node están en “../war/node_modules”, por esto se debe iniciar el app.js desde la ruta “../war/chat”. Los módulos node serán usados para otras features.

3. Ejecutar eMarking con normalidad.



