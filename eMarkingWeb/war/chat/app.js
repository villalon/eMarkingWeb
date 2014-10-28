//objeto node+express
var express = require('express'),app=express();
//objeto servidor (http)
var http = require('http').Server(app);
//objeto socket.io
var io = require('socket.io')(http);
//objeto mongoose (gestor de mongodb (tipo mysql) para nodejs)
var mongoose = require('mongoose');

//OBJETO de usuarios inicialmente vacío
users = {};

//crea la base de datos y la conexión
mongoose.connect('mongodb://localhost/chat', function(err){
	if(err){
		console.log(err);
	} else {
		console.log('Conexión exitosa!');
	}
});

//define la forma de los datos que creará mongo en un JSON
var chatSchema = mongoose.Schema({
	nick: String,
	msg: String,
	created: {type: Date, default: Date.now}
});

//crea colección en la DB
var Chat = mongoose.model('Message', chatSchema);


//redirije al archivo chat.html
app.get('/', function(req, res){
  res.sendfile('chat.html');
});

//usar el path de la misma carpeta para archivos estáticos (css,js,html...)
app.use(express.static(__dirname));

//cuando un usuario accede al servidor del chat
io.on('connection', function(socket){

	//avisa cuando un usuario se ha conectado
	console.log("A user connected!");

	//obtiene los mensajes antes de estar conectado. {} son los filtros. "query" ES VARIABLE DEL OBJETO Chat
	var query = Chat.find({});
	query
		.sort('created') //'-createsd' es orden descendiente 'created es orden ascendente'
		.limit(20). //recibe solo los ultimos 20 mensajes!
		exec(function(err, docs){
		if(err) throw err;
		console.log('Enviando mensajes antiguos');
		socket.emit('cargar mensajes antiguos',docs);
	});

	//cuando un usuario ingresa su nickname, revisando que no exista un nickname igual
	socket.on('nuevo usuario', function(data,callback){
		if(data in users){
			callback(false);
		} else {
			callback(true);
			//guarda el nombre del usuario en el servidor (durante la sesión)
			socket.nickname = data;
			users[socket.nickname]=socket;
			updateNicknames();
		}
	});

	//funcion para actualizar la lista de usuarios conectados
	function updateNicknames(){
		io.emit('usernames', Object.keys(users)); //utiliza un arreglo con las keys del objeto users
	}

	//cuando un usuario realiza un comentario
	socket.on('chat message', function(mensaje, callback){
		//muestra mensaje en el servidor
  		//console.log("Mensaje: "+mensaje); //esto muestra el mensaje escrito en la consola

  		//definir MENSAJES PRIVADOS (dirijidos a un user particular)
  		var msg = mensaje.trim(); //evita problemas si ponen espacio antes del texto...
  		if(msg.substr(0,3) === '/w '){
  			msg = msg.substr(3);
  			var ind = msg.indexOf(' '); //obtiene el indice del primer espacio (con esto detecta el username después del /w)
  			if(ind !== -1){
  				var name = msg.substr(0,ind);
  				var msg = msg.substr(ind + 1);
  				if(name in users){
  					users[name].emit('whisper', {msg: msg, nick: socket.nickname});
  					console.log('whisper!'); //mostrar un whisper en la consola si usan comando '/w '		
  				} else {
  					callback('Error! Enter a valid user.');
  				}
  			} else {
  				callback('Error! Please enter a message for your whisper.');
  			}

  		} else {
  			//guardar los mensajes globales en la db
  			var newMsg = new Chat({msg: msg, nick: socket.nickname});
  			newMsg.save(function(err){
  				if(err) throw err;
  				//else
  				//muestra mensaje a todos los usuarios conectados (MENSAJE PARA TODOS!)
	    		io.emit('chat message', {msg: msg, nick: socket.nickname});
  			});
    	}
  	});

  	//avisa cuando un usuario se ha desconectado
	socket.on('disconnect',function(){
		console.log("User disconnected");
		//si al salir no tienen nickname, simplemente sale (desconecta)
		if(!socket.nickname) return;
		//si tienen un nickname al salir, se debe eliminar para que no aparezca en el listado
		delete users[socket.nickname];
		updateNicknames();
	});

}); //CIERRE io.on




//habilita el puerto de acceso al servidor
http.listen(3000, function(){
  console.log('Server listening on *:3000');
});