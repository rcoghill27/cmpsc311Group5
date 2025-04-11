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
#include <stdbool.h>
#include <pthread.h>
#include <signal.h>

bool shutDown = false;

void get_timestamp(char* timestamp, size_t len){
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    strftime(timestamp, len, "%m-%d-%Y %H:%M:%S", tm_info);
}

// merged the verbose function and the log_file function
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

//client thread handler
static void * client_handler (void *arg) {
	//FIXME: placeholder test code
	char *str = (char *) arg;
	printf("ThreadFunc: Arguments passed to thread are: %s, pid = (%d)\n ", str, getpid());
	sleep(2);
	printf("\n ThreadFunc: exiting now\n");
	return 0;
}

static void handleSIGTERM(int sigNum) {

    if (sigNum == SIGTERM)
    {
        shutDown = true;
    }
    exit (EXIT_SUCCESS); //default action
}


int main(int argc, char *argv[]){
	static int MAX_CLIENTS = 10;
	int socket_fd;
	int result;
	int client_sockets[MAX_CLIENTS];
	int client_count = 0;
	int new_socket;
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
	char displayName[100];
	pthread_t thread_id;
	time_t currTime = time(NULL);

	for (int i = 1; i < argc; i++) {
		if (strcmp(argv[i], "-v") == 0 || strcmp(argv[i], "--verbose") == 0) {
			verbose = 1;
			tempArgc--;
		}
	}

	if (tempArgc == 3) {
		ip_address = argv[1];
		port = atoi(argv[2]);
		strcpy(displayName, argv[3]);
	}
	if (tempArgc == 1) {
		strcpy(displayName, argv[1]);
	}

	char message[1024];
	sprintf(message, "Selected Address: %s\n Selected Port: %d\nDisplay Name: %s\n", ip_address, port, displayName);
	verbose_log(verbose, serverLog_filename, message);
	message[0] = '\0';

  	verbose_log(verbose, serverLog_filename, "Verbose output enabled");

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    verbose_log(verbose, serverLog_filename, "Server Started");

    sprintf(message,"\n Process ID is (%d) \n", getpid());
    verbose_log(verbose, serverLog_filename, message);

    if( signal(SIGTERM, handleSIGTERM) == SIG_ERR )
    {
        verbose_log(verbose, serverLog_filename, "Cannot handle SIGTERM!\n");
        exit(-1);
    }

	// Error handle socket
	if (socket_fd == -1) {
		verbose_log(verbose, serverLog_filename, "Socket Error");
		exit(EXIT_FAILURE);
	}

	// Create the address structure
	memset(&server_address, 0, sizeof(server_address)); // Sets to 0
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr = INADDR_ANY;
	server_address.sin_port = htons(port);

	// Bind to IP address
	result = bind(socket_fd, (struct sockaddr*) &server_address, sizeof(server_address));
	if (result < 0) {
		verbose_log(verbose, serverLog_filename, "Bind Error");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}

	result = listen(socket_fd, 5);
	verbose_log(verbose, serverLog_filename, "Listening to socket\n");

	while ((new_socket = accept(socket_fd, (struct sockaddr*) &client_address, &client_length)) >= 0) {
		if (client_count < MAX_CLIENTS) {
            client_sockets[client_count++] = new_socket;

            sprintf(message, "New client connected, total clients: %d\n", client_count);
			verbose_log(verbose, serverLog_filename, message);

            // Create a thread to handle each client
            if (pthread_create(&thread_id, NULL, client_handler, (void *)&new_socket) != 0) {
                verbose_log(verbose, serverLog_filename, "Thread creation failed\n");
                close(new_socket);
            }
        } else {
            printf("Max clients reached, rejecting new connection\n");
            close(new_socket);  // Reject new connection if max clients are reached
        }
    }
	/* Delete?
	if (client_fd == -1){
		verbose_log(verbose, serverLog_filename, "Accept Error");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}
	*/
	printf("test"); //FIXME: testing purposes only
	/*
	while((bytes_read = recv(client_fd, buffer, sizeof(buffer)-1, 0))> 0){
		buffer[bytes_read] = '\0';


	    char messageSent[100];
	    sprintf(messageSent, "%s: %s", displayName, buffer);
	    verbose_log(verbose, messageLog_filename, messageSent);

		verbose_log(verbose, serverLog_filename, "Received Request from Client");
	}
	*/
	//Shutting down server
	/*FIXME: loop and close each client and thread
    if (shutDown) {

        close(socket_fd);
        verbose_log(verbose, serverLog_filename, "Server shutting down\n");
        close(client_fd);

        verbose_log(verbose, serverLog_filename, "\nCaught SIGTERM! Cleaning up... \n");

        sprintf(message,"\n The program was terminated at: %s\n", ctime(&currTime));
        verbose_log(verbose, serverLog_filename, message);
    }
	*/

	return 0;

}
