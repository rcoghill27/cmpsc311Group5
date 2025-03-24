//Jack
//Sheon
//Ryan
//Josmar
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
void get_timestamp(char* timestamp, size_t len){
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    strftime(timestamp, len, "%m - %d - %Y %H:%M:%S", tm_info);
}

void log_message(const char *filename, const char *message){
    FILE *log_file = fopen(filename, "a");
    if (log_file == NULL){
        perror("Failed to open log file");
        exit(EXIT_FAILURE);
    }
    char timestamp[20];
    get_timestamp(timestamp, sizeof(timestamp));
    fprintf(log_file, "[%s] %s\n", timestamp, message);
    fclose(log_file);
}

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
	char log_filename[] = "serverLog.txt";
	ssize_t bytes_read;

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    log_message(log_filename, "Server Started");
	// Error handle socket
	if (socket_fd == -1){
		perror("socket");
		exit(EXIT_FAILURE);
	}

    if (argc == 3){
    	    ip_address = argv[1];
    	    port = atoi(argv[2]);
    	}
    printf("IP: %s\nPort: %d\n", ip_address, port);

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

	client_fd = accept(socket_fd, (struct sockaddr*) &client_address, &client_length);

	if (client_fd == -1){
		perror("accept");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}
	log_message(log_filename, "Client Connected");
	while((bytes_read = recv(client_fd, buffer, sizeof(buffer)-1, 0))> 0){
		buffer[bytes_read] = '\0';
		printf("Received Data: %s", buffer);
		log_message(log_filename, "Received Request from Client");
	}
	close(socket_fd);
	log_message(log_filename, "Server shutting down");
	close(client_fd);
	return 0;
}
