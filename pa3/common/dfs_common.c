#include "common/dfs_common.h"
#include <sys/socket.h>
#include <sys/types.h>
#include <pthread.h>
/**
 * create a thread and activate it
 * entry_point - the function exeucted by the thread
 * args - argument of the function
 * return the handler of the thread
 */
inline pthread_t * create_thread(void * (*entry_point)(void*), void *args)
{
	pthread_t * thread;
        pthread_create(thread, NULL, entry_point, args);

	return thread;
}

/**
 * create a socket and return
 */
int create_tcp_socket()
{
	return socket(AF_INET, SOCK_STREAM, 0);
}

/**
 * create the socket and connect it to the destination address
 * return socket file descriptor
 */
int create_client_tcp_socket(char* address, int port)
{
	assert(port >= 0 && port < 65536);
	int socket = create_tcp_socket();
	if (socket == INVALID_SOCKET) return -1;
	//TODO: connect it to the destination port
        struct sockaddr_in socketAddress;
        struct hostent *server;
        
        server = gethostbyname(address);
        if (server == NULL) {
            return -1;
        }
        memset(socketAddress, 0, sizeof(socketAddress));
        
        socketAddress.sin_family = AF_INET;
        socketAddress.sin_port = htons(port);
        memcpy(socketAddress.sin_addr.s_addr, server_h_addr, server->h_length);
        
        if (connect(socket,(struct sockaddr *) &socketAddress, sizeof(socketAddress)) < 0 ) {
            return -1;
        }
        return socket;
}

/**
 * create a socket listening on the certain local port and return
 */
int create_server_tcp_socket(int port)
{
	assert(port >= 0 && port < 65536);
	int socket = create_tcp_socket();
	if (socket == INVALID_SOCKET) return -1;
	//TODO: listen on local port
        
        struct sockaddr_in socketAddress;
        memset(socketAddress, 0, sizeof(socketAddress));
        
        socketAddress.sin_family = AF_INET;
        socketAddress.sin_port = htons(port);
        socketAddress.sin_addr.s_addr = INADDR_ANY;
        
        int iSetOption = 1;
        setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, (char*)&iSetOption,
        sizeof(iSetOption));
        
        if ( bind( socket, (struct sockaddr *) &socketAddress, sizeof(socketAddress) ) < 0 ) {
            return -1;
        }
        
        if ( listen(socket, 10) < 0) {
            return -1;
        }
        return socket;
}

/**
 * socket - connecting socket
 * data - the buffer containing the data
 * size - the size of buffer, in byte
 */
void send_data(int socket, void* data, int size)
{
	assert(data != NULL);
	assert(size >= 0);
	if (socket == INVALID_SOCKET) return;
        
        send(socket, data, size, 0);
}

/**
 * receive data via socket
 * socket - the connecting socket
 * data - the buffer to store the data
 * size - the size of buffer in byte
 */
void receive_data(int socket, void* data, int size)
{
	assert(data != NULL);
	assert(size >= 0);
	if (socket == INVALID_SOCKET) return;
	
        recv(socket, data, size, 0);
}
