# CSC-502 - Multi-Process Client communicating with Multi-Threaded Server Java
CSC-502 Final Project


The overall design consists of a server and a client. Server listens to the client on a socket and establishes a connection using the socket. An arbitrary number (in my case i used 10) as the maximum number of connections supported by the Server. For each connection established, server creates a new thread. The ClientThread initializes input and output streams and waits for Client's input (txt file) over the socket.

The client prompts and accepts a text file from the user. The client reads the text file and sends it across to the server. The server accepts the content from the client over the socket, and performs a number of computations. The results are sent back to Client over the socket. The client writes the results into a text file. The client also prints the results to the command prompt.


# To Run the code

1. Edit the input text via to be sent to the server for processing
2. Run server.java
3. Run MultiThreadedClient.java



 
