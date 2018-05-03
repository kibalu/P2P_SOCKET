# P2P_SOCKET
	Compile .java files
1.	Put all the file and folder I provide in the same path, which include Client.java, PeerInformation.java, Server.java, Util.java, RFC.java and a folder called 60001
2.	Under the path include all the files, type following command 
javac Sever.java
javac Peer.java
	Run the programme
1.	Run server:
a)	Type following command to run the server: java Server
b)	After running the server, There is no operation in server side and server side will show all the request it receive and response to these requests
2.	Run client:
a)	Type following command to run the server: java Client
b)	After running the client, you can do operation following guidance showing in the server. All the operation is listed below
i.	Add a new RFC
ii.	Look up a specific RFC by RFC number
iii.	List all RFC
iv.	Down load a RFC
v.	EXIT
c)	All the RFC file a client have will be saved in the folder named by client’s port, which is under the same path with Class file.The folder called 60001 I provide is a preset RFC srouce folder for a client who use port 60001. For example, we create a client with port 60002, it RFC file folder will be 60002(reminder: we will not create this folder when the client runs. We will create this folder if it download file from peer.)
d)	The client will not upload the information about which RFC file it has, when client start. So we should use add a new RFC operation to provide these information to server.
e)	RFC name will be the same with RFC number.
