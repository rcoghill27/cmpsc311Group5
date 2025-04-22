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
#include <sys/types.h>
#include <sys/socket.h>
#include <ifaddrs.h>
#include <netdb.h>

#define MAX_CLIENTS 10

int client_sockets[MAX_CLIENTS];
int socket_fd;
int client_count = 0;
bool shutDown = false;
int verbose = 0;
char serverLog_filename[] = "serverLog.txt";
char messageLog_filename[] = "messageLog.txt";

void get_timestamp(char* timestamp, size_t len){
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    strftime(timestamp, len, "%m-%d-%Y %H:%M:%S", tm_info);
}

// Function to get the first non-loopback IPv4 address
char* get_ip_address() {
	static char host[NI_MAXHOST]; // static to ensure it persists after function returns
	struct ifaddrs *ifaddr, *ifa;
	int family, s;

	// Error if you can't get the interface address
	if (getifaddrs(&ifaddr) == -1) {
		perror("getifaddrs");
		return NULL;
	}

	// Loop through each address
	for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
		// Skip the interface if it doesn't have an IP
		if (ifa->ifa_addr == NULL)
			continue;

		family = ifa->ifa_addr->sa_family;

		// IPv4 Addresses
		if (family == AF_INET) {
			s = getnameinfo(ifa->ifa_addr, sizeof(struct sockaddr_in),
				host, NI_MAXHOST, NULL, 0, NI_NUMERICHOST);
			if (s != 0) {
				printf("getnameinfo() failed: %s\n", gai_strerror(s));
				continue;
			}
			// Skip loopback addresses (127.0.0.1)
			if (strcmp(host, "127.0.0.1") != 0) {
				freeifaddrs(ifaddr); // Free the memory used by getifaddrs
				return host;
			}
		}
	}
	freeifaddrs(ifaddr); // Free the memory used by getifaddrs
	return NULL; // No non-loopback IPv4 address found
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
static void * client_handler (void* arg) {
	int client_pos = *(int*) arg;
	ssize_t bytes_read;
	ssize_t bytes_sent;
	char buffer[1024];
	char displayName[64];
	char messageSent[1098];

	pthread_detach(pthread_self());

	sprintf(buffer, "Thread %d created, socket: %d", (int) pthread_self(), client_sockets[client_pos]);
	verbose_log(verbose, serverLog_filename, buffer);

	while((bytes_read = recv(client_sockets[client_pos], buffer, sizeof(buffer)-1, 0))> 0){
		buffer[bytes_read] = '\0';

	    sprintf(messageSent, "%s: %s", displayName, buffer);
	    verbose_log(verbose, messageLog_filename, messageSent);

		verbose_log(verbose, serverLog_filename, "Received Request from Client");

		for (int i = 0; i < MAX_CLIENTS; i++) {
			if (client_sockets[i] >= 0) {
				bytes_sent = send(client_sockets[i], messageSent, strlen(messageSent), 0);
				if (bytes_sent == -1) {
					verbose_log(verbose, serverLog_filename, "Failed to send message");
				}
			}
		}
	}

	sprintf(buffer, "Client %d disconnecting", (int) client_pos);
	verbose_log(verbose, serverLog_filename, buffer);
	client_sockets[client_pos] = -1;
	client_count--;
	sprintf(buffer, "Client count: %d", (int) client_count);
	verbose_log(verbose, serverLog_filename, buffer);

	sprintf(buffer, "Thread %d exiting now", (int) pthread_self());
	verbose_log(verbose, serverLog_filename, buffer);
	pthread_exit(NULL);

}


static void handleSIGTERM(int sigNum) {
        time_t currTime = time(NULL);
	ssize_t bytes_sent;
	char message[1024];

        if (sigNum == SIGINT || sigNum == SIGTERM)
        {


			for (int i = 0; i < MAX_CLIENTS; i++) {
//				message = "Server shutting down.";
				sprintf(message, "ServerShutdown&#Server is shutting down.");
				if (client_sockets[i] >= 0) {
					bytes_sent = send(client_sockets[i], message, strlen(message), 0);
					if (bytes_sent == -1) {
						verbose_log(verbose, serverLog_filename, "Failed to send message");
					}
				}
			}


                close(socket_fd);
                verbose_log(verbose, serverLog_filename, "\nCaught SIGTERM! Cleaning up... \n");

                verbose_log(verbose, serverLog_filename, "Server shutting down\n");
                for(int i = 0; i < MAX_CLIENTS; i++) {
                        close(client_sockets[i]);
                }

                sprintf(message,"\nThe program was terminated at: %s\n", ctime(&currTime));
                verbose_log(verbose, serverLog_filename, message);

        }
        exit (EXIT_SUCCESS); //default action
}


int main(int argc, char *argv[]){
	int result;
	int curr_client_socket = 0;
	int new_socket;
	char *ip_address = get_ip_address();
	struct sockaddr_in server_address;
	struct sockaddr_storage client_address;
	socklen_t client_length = sizeof(client_address);
	int port = 8080;
	char buffer[1024];
	ssize_t bytes_read;
	int tempArgc = argc - 1;
	pthread_t thread_id;
	time_t currTime = time(NULL);

	for (int i = 0; i < MAX_CLIENTS; i++) {
		client_sockets[i] = -1;
	}

	for (int i = 1; i < argc; i++) {
		if (strcmp(argv[i], "-v") == 0 || strcmp(argv[i], "--verbose") == 0) {
			verbose = 1;
			tempArgc--;
		}
	}

	if(ip_address != NULL) {
		char message1[200];
		sprintf(message1, "Default IPv4 address: %s", ip_address);
		verbose_log(verbose, serverLog_filename, message1);
	} else{
		verbose_log(verbose, serverLog_filename, "No non-loopback IPv4 address found.\n");
	}

	if (tempArgc == 3) {
		ip_address = argv[1];
		port = atoi(argv[2]);
	}

	char message[1024];
	sprintf(message, "Selected Address: %s\n\tSelected Port: %d", ip_address, port);
	verbose_log(verbose, serverLog_filename, message);
	message[0] = '\0';

  	verbose_log(verbose, serverLog_filename, "Verbose output enabled");

	// Create a socket
	socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    verbose_log(verbose, serverLog_filename, "Server Started");

    sprintf(message,"Process ID is (%d)", getpid());
    verbose_log(verbose, serverLog_filename, message);

    if( signal(SIGINT, handleSIGTERM) == SIG_ERR || signal(SIGTERM, handleSIGTERM) == SIG_ERR)
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
	verbose_log(verbose, serverLog_filename, "Listening to socket");

	while ((new_socket = accept(socket_fd, (struct sockaddr*) &client_address, &client_length)) >= 0) {
		if (client_count < MAX_CLIENTS) {
			for (int i = 0; i < MAX_CLIENTS; i++) {
				if (client_sockets[i] == -1) {
					curr_client_socket = i;
					break;
				}
			}

            client_sockets[curr_client_socket] = new_socket;
			client_count++;
            sprintf(message, "New client connected, total clients: %d\n", client_count);
			verbose_log(verbose, serverLog_filename, message);

            // Create a thread to handle each client
            if (pthread_create(&thread_id, NULL, client_handler, (void *)&curr_client_socket) != 0) {
                verbose_log(verbose, serverLog_filename, "Thread creation failed");
                close(new_socket);
				client_sockets[curr_client_socket] = -1;
				client_count--;
            }

        } else {
            verbose_log(verbose, serverLog_filename, "Max clients reached, rejecting new connection");
            close(new_socket);  // Reject new connection if max clients are reached
        }
    }


	return 0;

}