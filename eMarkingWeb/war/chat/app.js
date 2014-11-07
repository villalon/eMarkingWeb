//node+express object
var express = require('express'),app=express();
//servidor (http) object
var http = require('http').Server(app);
//socket.io object
var io = require('socket.io')(http);
//mongoose object (database manager (like mysql) for node.js)
var mongoose = require('mongoose');

//All users object
users = {};

//Room global variable
numRoom = "0";

//Online user global variable
actualUser = "no";

//Create DB and connection
mongoose.connect('mongodb://localhost/chat', function(err){
	if(err){
		console.log(err);
	} else {
		console.log('Conexión exitosa!');
	}
});

//Define mongoDB schema
var chatSchema = mongoose.Schema({
	nick: String,
	msg: String,
	created: {type: Date, default: Date.now},
	room: {type: String, default: "0"}
});

//Create collection in mongoDB
var Chat2 = mongoose.model('Message', chatSchema);

//Redirects to chat.html
app.get('/', function(req, res){
  res.sendfile('chat.html');
});

//Using same path for static files (css, js, html...)
app.use(express.static(__dirname));

//When the chat server is requested
io.on('connection', function(socket){

	//Log for new user connected
	console.log("A user connected!");
	
	//Get groupID into room object
	socket.on('chat room', function(groupID, callback){
		if(groupID){
			callback("Success saving room");
			numRoom = groupID;
			console.log("Success saving room: "+numRoom);
			loadMessages();
		}else{
			callback("Error saving room!");
			console.log("FAILURE! Room not saved. It will be 0");
			loadMessages();
		}
		
	});
	
	//When nickname is defined, checking and setting (unique usernames)
	socket.on('nuevo usuario', function(user, callback){
		if(user in users){
			callback("usuario existente");
		} else {
			callback("nuevo usuario agregado");
			//Save username for the session (until user disconnects)
			socket.nickname = user;
			//users[socket.nickname] = socket;
			users[user] = {username: user, room: numRoom};
			console.log(users);
			actualUser = user;
			console.log("Usuario actual: "+actualUser);
			console.log("Room actual: "+users[actualUser].room)
			updateNicknames();
		}
	});
	
	//Get old messages antes de estar conectado. "query" is a variable of Chat object
	function loadMessages(){
		var query = Chat2.find({ room:numRoom });
		query
			.sort('created') //'-created' is DESC 'created is ASC'
			.limit(1000). //last 1000 messages only!
			exec(function(err, docs){
			if(err) throw err;
			console.log('Enviando mensajes antiguos de la room '+numRoom);
			socket.emit('cargar mensajes antiguos',docs);
		});
	}
	
	
	//Update users online list
	function updateNicknames(){
		//io.emit('usernames', Object.keys(users)); //An array of users object keys
		io.emit('usernames', users); //An array of users object keys
	}

	//When a user submits a message (includes message text and chatRoom)
	socket.on('chat message', function(mensaje, chatRoom, callback){
		//muestra mensaje en el servidor
  		//console.log("Mensaje: "+mensaje); //esto muestra el mensaje escrito en la consola

  		//PRIVATE MESSAGES (points to specific user)
  		var msg = mensaje.trim(); //Avoid space problems...
  		if(msg.substr(0,3) === '/w '){
  			msg = msg.substr(3);
  			var ind = msg.indexOf(' '); //First index of first space (for detecting the username after /w)
  			if(ind !== -1){
  				var name = msg.substr(0,ind);
  				var msg = msg.substr(ind + 1);
  				if(name in users){
  					//send the whisper (it will be checked)
  					io.emit('whisper', {msg: msg, nick: socket.nickname, toUser: name});
  					console.log('whisper!'); //Show whisper in console if '/w ' is used		
  				} else {
  					callback('Error! Enter a valid user.');
  				}
  			} else {
  				callback('Error! Please enter a message for your whisper.');
  			}

  		} else {
  			//Save global messages into mongoDB
  			var newMsg = new Chat2({msg: msg, nick: socket.nickname, room: chatRoom});
  			newMsg.save(function(err){
  				if(err) throw err;
  				//else
  				//Show message to ALL users connected
	    		io.emit('chat message', {msg: msg, nick: socket.nickname, room: chatRoom});
  			});
    	}
  	});

  	//When user is disconnected
	socket.on('disconnect',function(){
		console.log("User disconnected");
		//If username is not assigned just disconnect
		if(!socket.nickname) return;
		//If username is assigned, delete and disconnect (removing from connected users list)
		delete users[socket.nickname];
		updateNicknames();
	});

}); //END io.on


//Enable server port access
http.listen(3000, function(){
  console.log('Server listening on *:3000');
});