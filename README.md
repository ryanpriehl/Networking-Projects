# Networking-Projects
***
## Assn3
#### FTP client program using Apache Commons Net
Supports the following commands for interacting with an FTP server:
* ls
* cd 'directory name'
* cd ..
* delete 'file name'
* get 'file name'
* get 'diretory name'
* put 'file name'
* put 'directory name'
* mkdir 'directory name'
* rmdir 'directory name'

Compile and run with: 
```
javac -cp "commons-net-3.6.jar" Assn3.java
java -cp ".:commons-net-3.6.jar" Assn3 <server IP> <username>:<password> "ls"
```
(Tested using an AWS EC2 server running Ubuntu 16.04)


## Assn5
#### Simple TCP client/server program
(Adapted from code in *Distributed Systems: Concepts and Design 5th Edition* by George Coulouris)

Provides a server program that waits for a connection and message from client program, then responds with the reverse of the message. Also provides a client program for connecting to the server and sending/receiving messages.

Compile and run with:
```
javac Assn5Client.java
nohup java Assn5Server &
```
and
```
javac  Assn5Server.java
java TCPClient <server IP> <message>
```
(Tested using an AWS EC2 server running Ubuntu 16.04)

## Assn6
#### 

## Assn7
#### 
