// Jack
// Josmar
// Sheon
// Ryan
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>

int main(){
	int socket_fd;
	int result;
	int client_fd;
	const char *ip_address = "127.0.0.1";
	struct sockaddr_in server_address;
	struct sockaddr_storage client_address;
	socklen_t client_length = sizeof(client_address);
	int port = 8080;
	char buffer[1024];
	ssize_t bytes_read;

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);

	// Error handle socket
	if (socket_fd == -1){
		perror("socket");
		exit(EXIT_FAILURE);
	}

	// Create the address structure
	memset(&server_address, 0, sizeof(server_address)); // Sets to 0
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr = inet_addr(ip_address);
	server_address.sin_port = htons(port);

	// Bind to IP address
	result = bind(socket_fd, (struct sockaddr*) &server_address, sizeof(server_address));
	if (result < 0) {
	perror("bind");
	close(socket_fd);
	exit(EXIT_FAILURE);
	}

	result = listen(socket_fd, 5);
	printf("Socket bound to %s:%d\n", ip_address, port);
	printf("Waiting for connection...\n");
	client_fd = accept(socket_fd, (struct sockaddr*) &client_address, client_length);

	if (client_fd == -1){
		perror("accept");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}
	printf("Connection established...\n");
	while((bytes_read = recv(client_fd, buffer, sizeof(buffer)-1, 0))> 0){
		buffer[bytes_read] = '\0';
		printf("Received Data: %s", buffer);
	}
	close(socket_fd);
	close(client_fd);
	return 0;
}
