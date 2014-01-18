#include "client/dfs_client.h"
#include "datanode/ext.h"
#include <sys/stat.h>
#include <errno.h>
#include <unistd.h>

int connect_to_nn(char* address, int port) {
    assert(address != NULL);
    assert(port >= 1 && port <= 65535);

    //Create a socket and connect it to the server (address, port)
    //assign return value to client_socket 
    return create_client_tcp_socket(address, port);
}

int modify_file(char *ip, int port, const char* filename, int file_size, int start_addr, int end_addr) {
    // Connect to namenode, open file
    int namenode_socket = connect_to_nn(ip, port);
    if (namenode_socket == INVALID_SOCKET) return -1;
    FILE* file = fopen(filename, "rb");
    assert(file != NULL);

    //Fill the request with type, file size, name and send to namenode
    dfs_cm_client_req_t request;
    strcpy(request.file_name, filename);
    request.file_size = file_size;
    request.req_type = 3;
    send_data(namenode_socket, &request, sizeof (request));

    //Receive the response
    dfs_cm_file_res_t response;
    receive_data(namenode_socket, &response, sizeof (response));

    //Allocate a buffer to read data from a local file
    char *buffer = malloc(sizeof (char) * (end_addr - start_addr + 1));
    memset(buffer, 0, sizeof (char) * (end_addr - start_addr + 1));

    // Read data as specified by start and end
    fseek(file, start_addr, SEEK_SET);
    fread(buffer, end_addr - start_addr + 1, 1, file);

    /* Data to modify may span more than one block.
     * Thus, we calculate a start block id and iterate through all blocks 
     * that need to be processed
     */
    int current_id = start_addr / DFS_BLOCK_SIZE;
    int data_sent = 0;

    while (data_sent < (end_addr - start_addr)) {
        // Create a new request, this time to the datanode
        dfs_cli_dn_req_t request2;
        memset(&request2, 0, sizeof (request2));

        // Set request type, block id, name, and content to be written
        request2.op_type = 1;
        request2.block.block_id = current_id;
        strcpy(request2.block.owner_name, filename);
        memcpy(&request2.block.content, buffer + data_sent, DFS_BLOCK_SIZE);

        // Open connection, send request
        int dn_socket = create_client_tcp_socket(response.query_result.block_list[current_id].loc_ip, response.query_result.block_list[current_id].loc_port);
        assert(dn_socket != INVALID_SOCKET);
        send_data(dn_socket, &request2, sizeof (request2));

        // Prepare for another datanode if necessary
        current_id++;
        data_sent += DFS_BLOCK_SIZE;
    }

    free(buffer);
    fclose(file);
    return 0;
}

int push_file(int namenode_socket, const char* local_path) {
    assert(namenode_socket != INVALID_SOCKET);
    assert(local_path != NULL);
    FILE* file = fopen(local_path, "rb");
    assert(file != NULL);

    // Create the push request (to the namenode)
    dfs_cm_client_req_t request;

    // Fill in request with file name
    strcpy(request.file_name, local_path);
    
    //Get file size using library, pass it to the request
    struct stat info;
    stat(local_path, &info);
    request.file_size = (int) info.st_size;
    request.req_type = 1;

    send_data(namenode_socket, &request, sizeof (request));

    // Receive namenode response
    dfs_cm_file_res_t response;
    receive_data(namenode_socket, &response, sizeof (response));


    // Go through all blocks
    int i;
    for (i = 0; i < response.query_result.blocknum; i++) {
        
        /* Create a new request, and fill it with proper type,
         * data from the file, block id and name
        */
        dfs_cli_dn_req_t request2;
        
        request2.op_type = 1;
        
        fseek(file, response.query_result.block_list[i].block_id * DFS_BLOCK_SIZE, SEEK_SET);
        fread(request2.block.content, sizeof (char), DFS_BLOCK_SIZE, file);
        
        request2.block.block_id = response.query_result.block_list[i].block_id;
        strcpy(request2.block.owner_name, response.query_result.block_list[i].owner_name);
        
        // Connect to datanode and send request
        int dn_socket = create_client_tcp_socket(response.query_result.block_list[i].loc_ip, response.query_result.block_list[i].loc_port);
        assert(dn_socket != INVALID_SOCKET);
        send_data(dn_socket, &request2, sizeof (request2));
    }

    fclose(file);
    return 0;
}

int pull_file(int namenode_socket, const char *filename) {
    assert(namenode_socket != INVALID_SOCKET);
    assert(filename != NULL);
    FILE* file = fopen(filename, "wb");

    //Fill in request with type and filename, send
    dfs_cm_client_req_t request;
    strcpy(request.file_name, filename);
    request.req_type = 0;
    send_data(namenode_socket, &request, sizeof (request));

    // Get response
    dfs_cm_file_res_t response;
    receive_data(namenode_socket, &response, sizeof (response));

    // Process response, block by block
    int i;
    for (i = 0; i < response.query_result.blocknum; i++) {

        // Create a new request for the datanodes
        dfs_cli_dn_req_t request2;
        request2.op_type = 0;
        request2.block.block_id = response.query_result.block_list[i].block_id;
        strcpy(request2.block.owner_name, response.query_result.block_list[i].owner_name);

        // Connect to datanode, send
        int dn_socket = create_client_tcp_socket(response.query_result.block_list[i].loc_ip, response.query_result.block_list[i].loc_port);
        assert(dn_socket != INVALID_SOCKET);
        send_data(dn_socket, &request2, sizeof (request2));

        // Read response from datanode, write to disk (reassemble file)
        dfs_cli_dn_req_t response2;
        receive_data(dn_socket, &response2, sizeof (response2));

        fseek(file, response2.block.block_id * DFS_BLOCK_SIZE, SEEK_SET);
        fwrite(response2.block.content, sizeof (char), DFS_BLOCK_SIZE, file);
    }

    fclose(file);
    return 0;
}

dfs_system_status *get_system_info(int namenode_socket) {
    assert(namenode_socket != INVALID_SOCKET);

    // Create status request (type 2 = query)
    dfs_cm_client_req_t request;
    memset(request.file_name, 0, sizeof (request.file_name));
    request.file_size = 0;
    request.req_type = 2;

    // Allocate memory for response
    dfs_system_status* response;
    response = malloc(sizeof (dfs_system_status));

    // Send request and receive response, return it
    send_data(namenode_socket, &request, sizeof (request));
    receive_data(namenode_socket, response, sizeof (dfs_system_status));

    return response;
}

int send_file_request(char **argv, char *filename, int op_type) {
    int namenode_socket = connect_to_nn(argv[1], atoi(argv[2]));
    if (namenode_socket < 0) {
        return -1;
    }

    int result = 1;
    switch (op_type) {
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

dfs_system_status *send_sysinfo_request(char **argv) {
    int namenode_socket = connect_to_nn(argv[1], atoi(argv[2]));
    if (namenode_socket < 0) {
        return NULL;
    }
    dfs_system_status* ret = get_system_info(namenode_socket);
    close(namenode_socket);
    return ret;
}
