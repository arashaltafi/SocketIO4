const express = require('express'); //requires express module
const socket = require('socket.io'); //requires socket.io module
const fs = require('fs');
const app = express();
var PORT = process.env.PORT || 3000;
const server = app.listen(PORT); //hosts server on localhost:3000



app.use(express.static('public'));
console.log('Server is running on Port 3000');
const io = socket(server);

//Peticion por default
app.get('/', (request, response) => {
    response.send('notification server is running on port 3000');
});

//Socket.io Connection------------------
io.on('connection', (socket) => {

    console.log("New socket connection: " + socket.id)

    socket.on('notification', () => {
        //console.log()
        io.emit('title', "test title");
        io.emit('description', "test description");
    })
	
	socket.on('disconnect', () => {
        console.log('User has left ');

        socket.broadcast.emit('userdisconnect', ' user has left');
    });
})