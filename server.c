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
#include <stdarg.h>

//Timestamp function for logging
void get_timestamp(char* timestamp, size_t len){
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    strftime(timestamp, len, "%m-%d-%Y %H:%M:%S", tm_info);
}

// merged the verbose function and the log_file function
//prints messages to log files, either server or message 
void verbose_log(int verbose, const char *filename, const char *format, ...) {
    FILE *log_file = fopen(filename, "a");
    if (log_file == NULL){
        perror("Failed to open log file");
        exit(EXIT_FAILURE);
    }
    char timestamp[20];
    get_timestamp(timestamp, sizeof(timestamp));
    fprintf(log_file, "[%s] %s\n", timestamp, format);
    fclose(log_file);

    if (verbose) {
        va_list args;
        va_start(args, format);
        vprintf(format, args);
        va_end(args);
        printf("\n");
    }
}

int main(int argc, char *argv[]){
	//Creating and declaring all variables
	int socket_fd;
	int result;
	int client_fd;
	char *ip_address = "127.0.0.1";
	struct sockaddr_in server_address;
	struct sockaddr_storage client_address;
	socklen_t client_length = sizeof(client_address);
	int port = 8080;
	char buffer[1024];
	char serverLog_filename[] = "serverLog.txt";
  	char messageLog_filename[] = "messageLog.txt";
	ssize_t bytes_read;
	int verbose = 0;
	int tempArgc = argc - 1;
	char displayName[1024];

	//Checking if verbose argument is passed
	for (int i = 1; i < argc; i++) {
		if (strcmp(argv[i], "-v") == 0 || strcmp(argv[i], "--verbose") == 0) {
			verbose = 1;
			tempArgc--;
		}
	}

	//Assigning IP, Port, and Display name depending on variables passed
	if (tempArgc == 3) {
		ip_address = argv[1];
		port = atoi(argv[2]);
		strcpy(displayName, argv[3]);
	}
	if (tempArgc == 1) {
		strcpy(displayName, argv[1]);
	}
	
  	//Printing messages with variables to log using sprintf
  	char message[200];
  	sprintf(message, "Selected Address: %s\n Selected Port: %d\nDisplay Name: %s\n", ip_address, port, displayName);
  	verbose_log(verbose, serverLog_filename, message);

  
  	verbose_log(verbose, serverLog_filename, "Verbose output enabled");
	
	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    	verbose_log(verbose, serverLog_filename, "Server Started");
	// Error handle socket
	if (socket_fd == -1){
		verbose_log(verbose, serverLog_filename, "Error: Socket");
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
	verbose_log(verbose, serverLog_filename, "Error: Bind");
	close(socket_fd);
	exit(EXIT_FAILURE);
	}

	result = listen(socket_fd, 5);

	//Connecting to client
	client_fd = accept(socket_fd, (struct sockaddr*) &client_address, &client_length);

	if (client_fd == -1){
		verbose_log(verbose, serverLog_filename, "Error: Accept");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}
	verbose_log(verbose, serverLog_filename, "Client Connected");
	
	//Printing messages sent and received to the message log file
	while((bytes_read = recv(client_fd, buffer, sizeof(buffer)-1, 0))> 0){
		buffer[bytes_read] = '\0';
	
    		char messageSent[100];
    		sprintf(messageSent, "%s: %s", displayName, buffer);
    		verbose_log(verbose, messageLog_filename, messageSent);

		verbose_log(verbose, serverLog_filename, "Received Request from Client");
	}
	//Shutting down the server
	close(socket_fd);
	verbose_log(verbose, serverLog_filename, "Server shutting down\n");
	close(client_fd);
	return 0;
}
