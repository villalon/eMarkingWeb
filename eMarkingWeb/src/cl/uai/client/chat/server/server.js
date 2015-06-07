var moment = require('moment');
var people = {};
var users=[];
var rooms = [];
var sockets = [];
var chatHistory = [];
var sosHistory=[];
var color = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19];

var port=9091;
var ipaddress="127.0.0.1";

console.log("["+logtime()+"] "+"Iniciando Servidor");
///////////////////////////////////////////////////////////////
var app = require('http').createServer(handler);
app.listen(port,ipaddress);

console.log("["+logtime()+"] "+"Servidor iniciado exitosamente en "+ipaddress+":"+port);
var  io = require('socket.io').listen(app);
var  fs = require('fs');



var _ = require('underscore')._;
///////////////////////////////////////////////////////////////
function handler (req, res) {
	fs.readFile('index.html',

			function (err, data) {
		if (err) {
			res.writeHead(500);
			return res.end('Error loading index.html');
		}
		res.setHeader("Content-Type", "text/html");
		res.writeHead(200);
		res.end(data);
	});
}
///////////////////////////////////////////
/////////// mysql conection////////////////
///////////////////////////////////////////
var mysql      = require('mysql');
var mysql = mysql.createConnection({
	host     : 'localhost',
	user     : 'root',
	password : '',
	database : 'moodle'
});
mysql.connect(function(err){
	if(!err) {
		console.log("["+logtime()+"] "+"Database is connected ");  
	} else {
		console.log("["+logtime()+"] "+"Error connecting database ");  
	}
});
io.sockets.on("connection", function (socket) {
	socket.on("joinserver", function(data) {
		var conectionData=JSON.parse(data);
		socket.room = conectionData.cm;
		socket.userName=conectionData.Username;
		socket.userid=conectionData.userid;
		socket.submissionId=conectionData.submissionId;
		socket.color=color[0];
		var index = color.indexOf(socket.color);
		if (index > -1) {
			color.splice(index, 1);
		}
		if(!inArray(socket.room,rooms)){
			rooms.push(socket.room);
			chatHistory[socket.room]=[];
			sosHistory[socket.room]=[];
			mysql.query('SELECT * from mdl_emarking_chat where room='+socket.room, function(err, rows, fields) {
				if (!err){
					if(rows.length > 0){
						for(i=0;i<rows.length;i++){
							chatHistory[socket.room].push(rows[i]);					    
						} 
					}
				}else
					console.log('Error while performing Query.');
				    console.log(err);
			});
			mysql.query('SELECT * from mdl_emarking_sos where room='+socket.room, function(err, rows, fields) {
				if (!err){
					if(rows.length > 0){
						for(i=0;i<rows.length;i++){
							sosHistory[socket.room].push(rows[i]);					    
						} 
					}
				}else
					console.log('Error while performing Query.');
				    console.log(err);
			});
		
		}
		socket.join(socket.room);
		var user = {};
		user.username=socket.userName;
		user.room=socket.room;
		user.userid=socket.userid;
		user.submissionid=socket.submissionId;
		user.color=socket.color;
		users.push(user);
		obj={};
		obj.user=user;
		obj.people=users;
		obj.chatHistory=chatHistory[socket.room];
		obj.sosHistory=sosHistory[socket.room];
		socket.emit("userJoin", JSON.stringify(obj));
		socket.broadcast.to(socket.room).emit("onBeginChatOther",JSON.stringify(user));
		console.log("["+logtime()+"] "+ socket.userName+ " se ha conectado en la sala " + socket.room);
	});
	socket.on("onSendMessage", function(data) {
		var data= JSON.parse(data);
		var message=data.message;
		var source =data.source;
		if(message=='historial -v') {
			console.log("["+logtime()+"]"+socket.userName +" en la sala "+socket.room+" activó el comando historial: ");
			for(var i =0;i<chatHistory[socket.room].length;i++){
				if(chatHistory[socket.room][i].source== source){
				var obj={};
				obj.time=chatHistory[socket.room][i].time;
				obj.userid=chatHistory[socket.room][i].userid;
				obj.message=chatHistory[socket.room][i].message;
				obj.username=chatHistory[socket.room][i].username;
				obj.source=chatHistory[socket.room][i].source;
				obj.submissionid=chatHistory[socket.room][i].submissionid;
					
				console.log(obj);
				}
			}
		}
		else {
			var obj={};
			obj.time=unixtime();
			obj.message=message;
			obj.userid=socket.userid;
			obj.room=socket.room;       
			obj.username= socket.userName;
			obj.color= socket.color;
			obj.source=source;
			obj.submissionid=socket.submissionId;
			chatHistory[socket.room].push(obj);
			socket.emit("onCatchMesage", JSON.stringify(obj));
			socket.broadcast.to(socket.room).emit("onCatchMesage",JSON.stringify(obj));//solo envia un mensaje
			console.log("["+logtime()+"] "+socket.userName +" en la sala "+socket.room+" ha enviado un mensaje: "+message);
            delete obj.color;
			var query = mysql.query('INSERT INTO mdl_emarking_chat SET ?', obj, function(err, result){

				if(!err) {
					console.log("["+logtime()+"] "+"Mensaje guardado en la base de datos. ");  
				} else {
					console.log("["+logtime()+"] "+"No se pudo guardar el mensaje en la base de datos ");
					console.log(err);
				}
			});
		}
	});

	socket.on("onSendSos", function(data) {
		var data= JSON.parse(data);
		var obj={};
		obj.time=unixtime();
		obj.comment=data.comment;
		obj.urgencylevel=data.urgencyLevel;
		obj.submissionid=socket.submissionId;
		obj.userid=socket.userid;
		obj.room=socket.room;       
		obj.status=1;
		obj.username=socket.userName;
		
		socket.emit("onCatchSos", JSON.stringify(obj));
		socket.broadcast.to(socket.room).emit("onCatchSos",JSON.stringify(obj));//solo envia un mensaje
		delete obj.username;
		
		var query = mysql.query('INSERT INTO mdl_emarking_sos SET ?', obj, function(err, result){

			if(!err) {
				console.log("["+logtime()+"] "+"SOS guardada en la base de datos. ");  
			} else {
				console.log("["+logtime()+"] "+"No se pudo guardar el SOS en la base de datos ");  
				console.log(err);
			}
		});
		
		
		
	});
	
	socket.on("disconnect", function() {
		socket.leave(socket.room);
		var user = {};
		user.username=socket.userName;
		user.room=socket.room;
		user.userid=socket.userid;
		user.color=socket.color;
		socket.broadcast.to(socket.room).emit("onRemoveChatUser",JSON.stringify(user));
		color.push(socket.color);
		console.log("["+logtime()+"] "+socket.userName+ " se ha desconectado en la sala " + socket.room);
		for(var i=0; i<users.length; i++) {

			if(socket.userName === users[i].username) {

				users.splice(i, 1);

				return;
			}
		}
	});
});

function inArray(needle, haystack) {
	var length = haystack.length;
	for(var i = 0; i < length; i++) {
		if(haystack[i] == needle)
			return true;
	}
	return false;
}
function logtime(){
	var lognow = moment(new Date());
	var logtime = lognow.format("DD-MM-YYYY HH:mm:ss");
	return logtime;

}
function unixtime(){
	var now = moment(new Date());
	var time = now.format("X");
	return time;

}
