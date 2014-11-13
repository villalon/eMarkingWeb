/**
 * LOADING EXPRESS, SOCKET, MONGO
 */
//node+express object
var express = require('express'),app=express();
//servidor (http) object
var http = require('http').Server(app);
//socket.io object
var io = require('socket.io')(http);
//mongoose object (database manager (like mysql) for node.js)
var mongoose = require('mongoose');

/**
 * NAMESPACES: different connections for the chat and each wallType.
 */
var chatNSP = io.of('/chat'); 
var adminWallNSP = io.of('/adminWall');

/**
 * GLOBAL OBJECTS
 */
users = {};

/**
 * GLOBAL VARIABLES
 */
numRoom = "0";
actualUser = "no";

/**
 * MONGO CONNECTION
 */
mongoose.connect('mongodb://localhost/collaborativeFeatures', function(err){
	if(err){
		console.log(err);
	} else {
		console.log('Successful connection! (Port 3000 at localhost)');
	}
});

/**
 * MONGO SCHEMAS
 */
//Chat
var chatSchema = mongoose.Schema({
	nick: String,
	msg: String,
	created: {type: Date, default: Date.now},
	room: {type: String, default: "0"}
});
//Wall
var wallSchema = mongoose.Schema({
	user: String,
	role: String,
	post: String,
	created: {type: Date, default: Date.now},
	room: {type: String, default: "0"},
	wallType: String,
	comments: {subUser: String, subCreated: {type: Date, default: Date.now}, comment: String}
});

/**
 * MONGO COLLECTIONS
 */
var Chat = mongoose.model('Message', chatSchema);
var Wall = mongoose.model('Posts',wallSchema);

/**
 * REDIRECTING TO HTML FILES
 */
/*
//Chat
app.get('/chat', function(req, res){
  res.sendfile('chat.html');
});
//Wall
app.get('/wall', function(req, res){
  res.sendfile('wall.html');
});
*/

/**
 * SAME PATHS FOR STATIC FILES (css, js, html...)
 */
app.use(express.static(__dirname));

/**
 * CHAT SOCKET.on
 */
chatNSP.on('connection', function(socket){

//Log for new user connected
console.log("A user connected to the chat!");
//Get groupID into room object and load old messages from room
socket.on('chat room', function(groupID, callback){
	if(groupID){
		callback("Success saving room");
		numRoom = groupID;
		console.log("CHAT: Success saving room: "+numRoom);
		loadMessages();
	}else{
		callback("Error saving room!");
		console.log("CHAT: FAILURE! Room not saved. It will be 0");
		loadMessages();
	}
});
//When nickname is defined, checking and setting (unique usernames)
socket.on('nuevo usuario', function(user, callback){
	if(user in users){
		callback("Usuario existente!");
	} else {
		callback("Nuevo usuario agregado!");
		//Save username for the session (until user disconnects)
		socket.nickname = user;
		//users[socket.nickname] = socket;
		users[user] = {username: user, room: numRoom};
		console.log(users);
		actualUser = user;
		console.log("CHAT: Online user: "+actualUser);
		console.log("CHAT: Actual room: "+users[actualUser].room)
		updateNicknames();
	}
});
//Get old messages antes de estar conectado. "query" is a variable of Chat object
function loadMessages(){
	var query = Chat.find({ room:numRoom });
	query
		.sort('created') //'-created' is DESC 'created is ASC'
		.limit(1000). //last 1000 messages only!
		exec(function(err, docs){
		if(err) throw err;
		console.log('CHAT: Sending old messages from room '+numRoom);
		socket.emit('cargar mensajes antiguos',docs);
	});
}
//Update users online list
function updateNicknames(){
	chatNSP.emit('usernames', users); //An array of users object keys
}
//When a user submits a message (includes message text and chatRoom)
socket.on('chat message', function(mensaje, chatRoom, callback){
	//Private messages (whispers): points to a specific user
	var msg = mensaje.trim(); //Avoid space problems...
	if(msg.substr(0,3) === '/w '){
		msg = msg.substr(3);
		var ind = msg.indexOf(' '); //First index of first space (for detecting the username after /w)
		if(ind !== -1){
			var name = msg.substr(0,ind);
			var msg = msg.substr(ind + 1);
			if(name in users){
				//send the whisper (it will be checked)
				chatNSP.emit('whisper', {msg: msg, nick: socket.nickname, toUser: name});
				console.log('CHAT: whisper sended!'); //Show whisper in console if '/w ' is used		
			} else {
				callback('Error! Enter a valid user.');
			}
		} else {
			callback('Error! Please enter a message for your whisper.');
		}
	} else {
		//Save global messages into mongoDB
		var newMsg = new Chat({msg: msg, nick: socket.nickname, room: chatRoom});
		newMsg.save(function(err){
			if(err) throw err;
			//else
			//Show message to ALL users connected
			chatNSP.emit('chat message', {msg: msg, nick: socket.nickname, room: chatRoom});
		});
	}
});
//When user is disconnected
socket.on('disconnect',function(){
	console.log("CHAT: User disconnected");
	//If username is not assigned just disconnect
	if(!socket.nickname) return;
	//If username is assigned, delete and disconnect (removing from connected users list)
	delete users[socket.nickname];
	updateNicknames();
});
	
}); //END OF Chat SOCKET

/**
 * adminWall SOCKET.on
 */
adminWallNSP.on('connection',function(socket){
	//At connection
	console.log('WALL: Someone connected at adminWall namespace!');
	
	//Get groupID into room object and load old posts from room
	socket.on('adminWall room', function(groupID, callback){
		if(groupID){
			callback("WALL: Success wall room!");
			numRoom = groupID;
			console.log("WALL: Success loading room: "+numRoom);
			loadAdminPosts();
		}else{
			callback("Error getting room!");
			console.log("FAILURE! No room is detected");
			//loadAdminPosts();
		}
	});
	//Get old posts function for Wall
	function loadAdminPosts(){
		var query = Wall.find({ room:numRoom, wallType:"admin" });
		query
			.sort('created') //'-created' is DESC 'created is ASC'
			.limit(100). //last 100 messages only!
			exec(function(err, docs){
			if(err) throw err;
			console.log('WALL: Enviando posts antiguos de la room '+numRoom);
			socket.emit('old posts adminWall',docs);
		});
	}
	//When a user submits a post (includes post text and chatRoom)
	socket.on('send post adminWall', function(userOnline, posted, chatRoom){
		//Save global posts into mongoDB
		var newPost = new Wall({user: userOnline, role:"admin", post: posted, room: chatRoom, wallType:"admin"});
		newPost.save(function(err){
			if(err) throw err;
			//else
			//Show message to ALL users connected
			console.log("Este es el ultimo post id: "+newPost._id);
			adminWallNSP.emit('post message', {user: userOnline, post: posted, room: chatRoom, lastId: newPost._id });
		});
  	});
	
	//When a user submits a subPost (it uploads a post adding a comment)
	socket.on('send subPost adminWall', function(userOnline, parent, subPost){
		//Update main post with their comment (subPost) into mongoDB
		Wall.update({ _id: parent }, { $set: { comments: { subUser: userOnline, comment: subPost } } });
		newPost.save(function(err){
			if(err) throw err;
			//else
			//Show subPost to ALL users connected
			//adminWallNSP.emit('post message', {user: userOnline, post: posted, room: chatRoom});
		});
  	});
	
	//When user is disconnected from adminWall
	socket.on('disconnect',function(){
		console.log("WALL: User disconnected");
	});
	
}); // END OF adminWall SOCKET.

/**
 * ENABLE SERVER PORT ACCESS
 */
http.listen(3000, function(){
  console.log('Node server for collaborativeFeatures listening on *:3000');
});