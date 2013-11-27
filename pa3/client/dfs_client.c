#include "client/dfs_client.h"
#include "datanode/ext.h"
#include <sys/stat.h>
#include <errno.h>

int connect_to_nn(char* address, int port)
{
	assert(address != NULL);
	assert(port >= 1 && port <= 65535);
	//Create a socket and connect it to the server (address, port)
	//assign return value to client_socket 
	return create_client_tcp_socket( address, port);
}

int modify_file(char *ip, int port, const char* filename, int file_size, int start_addr, int end_addr)
{
	int namenode_socket = connect_to_nn(ip, port);
	if (namenode_socket == INVALID_SOCKET) return -1;
	FILE* file = fopen(filename, "rb");
	assert(file != NULL);

	//TODO:fill the request and send
	dfs_cm_client_req_t request;
	
	//TODO: receive the response
	dfs_cm_file_res_t response;

	//TODO: send the updated block to the proper datanode

	fclose(file);
	return 0;
}

int push_file(int namenode_socket, const char* local_path)
{
	assert(namenode_socket != INVALID_SOCKET);
	assert(local_path != NULL);
	FILE* file = fopen(local_path, "rb");
	assert(file != NULL);

	// Create the push request
	dfs_cm_client_req_t request;

	//TODO:fill the fields in request and
        strcpy(request.file_name, local_path);
        //Get file size
        struct stat info;
        stat(local_path, &info);
        request.file_size = (int)info.st_size;
        request.req_type = 1;
        
        send_data(namenode_socket, &request, sizeof(request));
	
	//TODO:Receive the response
	dfs_cm_file_res_t response;
        receive_data(namenode_socket, &response, sizeof(response));
        

	//TODO: Send blocks to datanodes one by one
        int i;
        for (i = 0 ; i < response.query_result.blocknum; i++) {
            dfs_cli_dn_req_t request2;
            request2.op_type = 1;
            fseek(file, response.query_result.block_list[i].block_id * DFS_BLOCK_SIZE, SEEK_SET);
            fread(request2.block.content, sizeof(char), DFS_BLOCK_SIZE, file);
            request2.block.block_id = response.query_result.block_list[i].block_id;
            strcpy(request2.block.owner_name, response.query_result.block_list[i].owner_name);
            int dn_socket = create_client_tcp_socket(response.query_result.block_list[i].loc_ip, response.query_result.block_list[i].loc_port);
            printf("Connection to DN: %s\n",strerror(errno));
            send_data(dn_socket, &request2, sizeof(request2));
        }

	fclose(file);
	return 0;
}

int pull_file(int namenode_socket, const char *filename) {
    assert(namenode_socket != INVALID_SOCKET);
    assert(filename != NULL);
    FILE* file = fopen(filename, "wb");

    //TODO: fill the request, and send
    dfs_cm_client_req_t request;
    strcpy(request.file_name, filename);
    request.req_type = 0;
    send_data(namenode_socket, &request, sizeof (request));

    //TODO: Get the response
    dfs_cm_file_res_t response;
    receive_data(namenode_socket, &response, sizeof (response));

    //TODO: Receive blocks from datanodes one by one
    int i;
    for (i = 0; i < response.query_result.blocknum; i++) {
        dfs_cli_dn_req_t request2;
        request2.op_type = 0;
        
        request2.block.block_id = response.query_result.block_list[i].block_id;
        strcpy(request2.block.owner_name, response.query_result.block_list[i].owner_name);
        int dn_socket = create_client_tcp_socket(response.query_result.block_list[i].loc_ip, response.query_result.block_list[i].loc_port);
        printf("Connection to DN: %s\n", strerror(errno));
        
        send_data(dn_socket, &request2, sizeof (request2));
        
        // Response from datanode
        dfs_cli_dn_req_t response2;
        receive_data(dn_socket, &response2, sizeof(response2));
        
        fseek(file, response2.block.block_id * DFS_BLOCK_SIZE, SEEK_SET);
        fwrite(response2.block.content, sizeof (char), DFS_BLOCK_SIZE, file);
    }

//    FILE *file = fopen(filename, "wb");
    //TODO: resemble the received blocks into the complete file
    fclose(file);
    return 0;
}

dfs_system_status *get_system_info(int namenode_socket)
{
	assert(namenode_socket != INVALID_SOCKET);
	//TODO fill the result and send 
	dfs_cm_client_req_t request;
        memset(request.file_name, 0, sizeof(request.file_name));
        request.file_size = 0;
        // 2 : query datanodes
        request.req_type = 2;
	
	//TODO: get the response
	dfs_system_status* response;
        response = malloc(sizeof(dfs_system_status));
        
        send_data(namenode_socket, &request, sizeof(request));
        receive_data(namenode_socket, response, sizeof(dfs_system_status));

	return response;
}

int send_file_request(char **argv, char *filename, int op_type)
{
	int namenode_socket = connect_to_nn(argv[1], atoi(argv[2]));
	if (namenode_socket < 0)
	{
		return -1;
	}

	int result = 1;
	switch (op_type)
	{
		case 0:
			result = pull_file(namenode_socket, filename);
			break;
		case 1:
			result = push_file(namenode_socket, filename);
			break;
	}
	close(namenode_socket);
	return result;
}

dfs_system_status *send_sysinfo_request(char **argv)
{
	int namenode_socket = connect_to_nn(argv[1], atoi(argv[2]));
	if (namenode_socket < 0)
	{
		return NULL;
	}
	dfs_system_status* ret =  get_system_info(namenode_socket);
	close(namenode_socket);
	return ret;
}
