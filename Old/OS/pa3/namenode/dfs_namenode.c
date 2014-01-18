#include "namenode/dfs_namenode.h"
#include <assert.h>
#include <unistd.h>
#include <errno.h>

dfs_datanode_t* dnlist[MAX_DATANODE_NUM];
dfs_cm_file_t* file_images[MAX_FILE_COUNT];
int fileCount;
int dncnt = 0;
int safeMode = 1;

int mainLoop(int server_socket) {
    while (safeMode == 1) {
        printf("the namenode is running in safe mode\n");
        sleep(5);
    }

    for (;;) {
        sockaddr_in client_address;
        socklen_t client_address_length = sizeof (client_address);

        //Accept the connection from the client and assign the return value to client_socket
        // This will block until it receives something
        int client_socket = accept(server_socket, (struct sockaddr *) &client_address, &client_address_length);

        assert(client_socket != INVALID_SOCKET);

        //Receive requests from client and fill it in request
        dfs_cm_client_req_t request;
        receive_data(client_socket, &request, sizeof (request));

        // Pass request along for processing, continue listening
        requests_dispatcher(client_socket, request);
        close(client_socket);
    }
    return 0;
}

static void *heartbeatService() {
    // Open socket, and register datanodes as they report in
    int socket_handle = create_server_tcp_socket(50030);
    register_datanode(socket_handle);
    close(socket_handle);
    return 0;
}

/**
 * start the service of namenode
 * argc - count of parameters
 * argv - parameters
 */
int start(int argc, char **argv) {
    assert(argc == 2);
    int i = 0;
    for (i = 0; i < MAX_DATANODE_NUM; i++) dnlist[i] = NULL;
    for (i = 0; i < MAX_FILE_COUNT; i++) file_images[i] = NULL;

    //Create a thread to handle heartbeat service
    create_thread(heartbeatService, NULL);

    //Create a socket to listen the client requests
    int server_socket = create_server_tcp_socket(atoi(argv[1]));

    assert(server_socket != INVALID_SOCKET);

    return mainLoop(server_socket);
}

int register_datanode(int heartbeat_socket) {
    for (;;) {
        sockaddr_in client_address;
        socklen_t client_address_length = sizeof (client_address);

        // Accept connection from datanode(s)
        int datanode_socket = accept(heartbeat_socket, (struct sockaddr *) &client_address, &client_address_length);

        printf("Heartbeat Connection: %s\n", strerror(errno));
        assert(datanode_socket != INVALID_SOCKET);

        //Receive datanode's status via datanode_socket
        dfs_cm_datanode_status_t datanode_status;
        receive_data(datanode_socket, &datanode_status, sizeof (datanode_status));

        // Update status in memory
        if (datanode_status.datanode_id < MAX_DATANODE_NUM) {
            //Fill dnlist
            //principle: a datanode with id of n should be filled in dnlist[n - 1] (n is always larger than 0)

            int pos = datanode_status.datanode_id - 1;

            // We have to allocate memory for datanodes that exist (but only once/datanode)
            if (dnlist[pos] == NULL) {
                dnlist[pos] = malloc(sizeof (dfs_datanode_t));
            }

            // Copy datanode information to struct in memory
            dnlist[pos]->dn_id = datanode_status.datanode_id;
            memcpy(&dnlist[pos]->ip, inet_ntoa(client_address.sin_addr), sizeof (dnlist[pos]->ip));
            dnlist[pos]->port = datanode_status.datanode_listen_port;

            // Update datanode counter
            int i = 0;
            dncnt = 0;
            for (; i < MAX_DATANODE_NUM; i++)
                if (dnlist[i] != NULL) {
                    dncnt++;
                }

            safeMode = 0;
        }
        close(datanode_socket);
    }
    return 0;
}

int get_file_receivers(int client_socket, dfs_cm_client_req_t request) {
    printf("Responding to request for block assignment of file '%s'!\n", request.file_name);

    dfs_cm_file_t** end_file_image = file_images + MAX_FILE_COUNT;
    dfs_cm_file_t** file_image = file_images;

    // Try to find if there is already an entry for that file
    while (file_image != end_file_image) {
        if (*file_image != NULL && strcmp((*file_image)->filename, request.file_name) == 0) break;
        ++file_image;
    }

    if (file_image == end_file_image) {
        // There is no entry for that file, find an empty location to create one
        file_image = file_images;
        while (file_image != end_file_image) {
            if (*file_image == NULL) break;
            ++file_image;
        }

        if (file_image == end_file_image) return 1;
        // Create the file entry
        *file_image = (dfs_cm_file_t*) malloc(sizeof (dfs_cm_file_t));
        memset(*file_image, 0, sizeof (**file_image));
        strcpy((*file_image)->filename, request.file_name);
        (*file_image)->file_size = request.file_size;
        (*file_image)->blocknum = 0;
    }

    // Number of blocks required to satisfy request
    int block_count = (request.file_size + (DFS_BLOCK_SIZE - 1)) / DFS_BLOCK_SIZE;

    int first_unassigned_block_index = (*file_image)->blocknum;
    (*file_image)->blocknum = block_count;
    int next_data_node_index = 0;

    //Assign data blocks to datanodes, round-robin style
    while (next_data_node_index < block_count) {
        dfs_cm_block_t block_data;
        memset(&block_data, 0, sizeof (block_data));

        // Copy information from known datanode information
        // Use of modulo with # of datanodes -> round robin
        block_data.dn_id = dnlist[next_data_node_index % dncnt]->dn_id;
        block_data.block_id = first_unassigned_block_index;
        memcpy(block_data.loc_ip, dnlist[next_data_node_index % dncnt]->ip, sizeof (block_data.loc_ip));
        block_data.loc_port = dnlist[next_data_node_index % dncnt]->port;
        strcpy(block_data.owner_name, request.file_name);

        // Store information in memory for later reference, and repeat for each block
        memcpy(&(*file_image)->block_list[next_data_node_index], &block_data, sizeof (block_data));
        next_data_node_index++;
        first_unassigned_block_index++;
    }

    // Send response back to client.  We can simply copy from existing
    // memory now
    dfs_cm_file_res_t response;
    memset(&response, 0, sizeof (response));

    memcpy(&response.query_result, (*file_image), sizeof (response.query_result));
    send_data(client_socket, &response, sizeof (response));

    return 0;
}

int get_file_location(int client_socket, dfs_cm_client_req_t request) {
    int i = 0;
    for (i = 0; i < MAX_FILE_COUNT; ++i) {

        dfs_cm_file_t* file_image = file_images[i];
        if (file_image == NULL) continue;
        if (strcmp(file_image->filename, request.file_name) != 0) continue;
        dfs_cm_file_res_t response;
        // File found: fill in response with information from memory & send

        printf("Responding to read for file %s\n", request.file_name);
        memcpy(&response.query_result, file_image, sizeof (dfs_cm_file_t));
        send_data(client_socket, &response, sizeof (response));

        return 0;
    }
    //FILE NOT FOUND
    return 1;
}

void get_system_information(int client_socket, dfs_cm_client_req_t request) {

    assert(client_socket != INVALID_SOCKET);

    //Fill the response with # of datanodes and info about them
    dfs_system_status response;
    response.datanode_num = dncnt;

    int i = 0;
    for (i = 0; i < dncnt; i++) {
        memcpy(&response.datanodes[i], dnlist[i], sizeof (dfs_datanode_t));
    }

    printf("Sending response to sys info request\n");
    send_data(client_socket, &response, sizeof (response));
}

int get_file_update_point(int client_socket, dfs_cm_client_req_t request) {
    int i = 0;
    for (i = 0; i < MAX_FILE_COUNT; ++i) {
        dfs_cm_file_t* file_image = file_images[i];
        if (file_image == NULL) continue;

        // File found, process
        if (strcmp(file_image->filename, request.file_name) != 0) continue;
        printf("Processing modify for file %s\n", request.file_name);

        dfs_cm_file_res_t response;

        // If we need to append, do so
        if (request.file_size > file_image->file_size) {

            // New # of blocks, how many new blocks we need, index of next block to process
            int block_count = (request.file_size + (DFS_BLOCK_SIZE - 1)) / DFS_BLOCK_SIZE;
            int blocks_needed = block_count - file_image->blocknum;
            int unassigned_block_index = file_image->blocknum;
            file_image->blocknum = block_count;
            file_image->file_size = request.file_size;

            int blocks_assigned = 0;
            while (blocks_assigned < blocks_needed) {

                // Create a new block info structure, and set parameters
                // See the write function for more info
                dfs_cm_block_t block_data;
                memset(&block_data, 0, sizeof (block_data));

                block_data.dn_id = dnlist[unassigned_block_index % dncnt]->dn_id;
                block_data.block_id = unassigned_block_index;

                memcpy(block_data.loc_ip, dnlist[unassigned_block_index % dncnt]->ip, sizeof (block_data.loc_ip));
                block_data.loc_port = dnlist[unassigned_block_index % dncnt]->port;

                strcpy(block_data.owner_name, request.file_name);

                // Update global block info about the file
                // Note that this will not update existing blocks (there is no need to)
                memcpy(&file_image->block_list[unassigned_block_index], &block_data, sizeof (block_data));

                // Continue if neccesary to assign more than 1 block
                unassigned_block_index++;
                blocks_assigned++;
            }
        }

        //Fill the response and send it back to the client
        memset(&response, 0, sizeof (response));

        // file_image now has been fully updated if necessary, so send it
        memcpy(&response.query_result, file_image, sizeof (response.query_result));
        send_data(client_socket, &response, sizeof (response));

        return 0;
    }
    //FILE NOT FOUND
    return 1;
}

int requests_dispatcher(int client_socket, dfs_cm_client_req_t request) {
    //0 - read, 1 - write, 2 - query, 3 - modify
    printf("Request received of type %d\n", request.req_type);
    switch (request.req_type) {
        case 0:
            get_file_location(client_socket, request);
            break;
        case 1:
            get_file_receivers(client_socket, request);
            break;
        case 2:
            get_system_information(client_socket, request);
            break;
        case 3:
            get_file_update_point(client_socket, request);
            break;
    }
    return 0;
}

int main(int argc, char **argv) {
    int i = 0;
    for (; i < MAX_DATANODE_NUM; i++)
        dnlist[i] = NULL;
    return start(argc, argv);
}
