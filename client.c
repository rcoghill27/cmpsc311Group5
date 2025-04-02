#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <arpa/inet.h>
#define BUFFER_SIZE 1024

void getTimeStamp(char* timestamp, size_t len){
	time_t now = time(NULL);
	struct tm *tm_info = localtime(&now);
	strftime(timestamp, len, "%m/%d/%Y %H:%M:%S", tm_info);
}
void logClientMessage(const char* fileName,  const char *message){
	FILE* logFile = fopen(fileName, "a");
	if (logFile == NULL){
		perror("Failed to open log file");
		exit(EXIT_FAILURE);
	}
	char timestamp[20];
	getTimeStamp(timestamp, sizeof(timestamp));
	fprintf(logFile, "[%s] %s\n", timestamp, message);
	fclose(logFile);
}

int main(int argc, char* argv[]) {
int client_fd;
struct sockaddr_in server_addr;
char buffer[BUFFER_SIZE];
ssize_t bytes_sent;
char displayName[BUFFER_SIZE];
char clientLog[] = "clientLog.txt";
char serverIP[20];
int serverPort;
if (argc == 2){
	strcpy(displayName, argv[1]);
	strcpy(serverIP, "127.0.0.1");
	serverPort = 8080;
}
if (argc == 4){
	strcpy(displayName, argv[3]);
	strcpy(serverIP, argv[1]);
	serverPort = atoi(argv[2]);
}
// Create socket
client_fd = socket(AF_INET, SOCK_STREAM, 0);
if (client_fd == -1) {
logClientMessage(clientLog, "Socket Creation Failed");
return -1;
}
// Set up the server address struct
memset(&server_addr, 0, sizeof(server_addr));
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(serverPort);
// Convert IP address from text to binary form
if (inet_pton(AF_INET, serverIP, &server_addr.sin_addr) <= 0) {
logClientMessage(clientLog, "Invalid address/Address not supported");
close(client_fd);
return -1;
}
// Connect to the server
if (connect(client_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) ==
-1) {
logClientMessage(clientLog, "Connection to server failed");
close(client_fd);
return -1;
}
char connectedServer[50];
logClientMessage(clientLog, "Connected to the server");
sprintf(connectedServer, "Connected to %s:%d", serverIP, serverPort);
logClientMessage(clientLog, connectedServer);
bytes_sent = send(client_fd, displayName, strlen(displayName),0);
// Read and send data to the server
while (1) {
printf("Enter message (or 'exit' to quit): ");
fgets(buffer, sizeof(buffer), stdin);
//char messageSent[100];
logClientMessage(clientLog, "Message sent from client");
// Check for exit condition
if (strncmp(buffer, "exit", 4) == 0) {
break;
}
// Send data to the server
bytes_sent = send(client_fd, buffer, strlen(buffer), 0);
if (bytes_sent == -1) {
logClientMessage(clientLog, "Failed to send message");
break;
}
}
// Close the socket
close(client_fd);
logClientMessage(clientLog, "Client disconnected from the server\n");
return 0;
}
