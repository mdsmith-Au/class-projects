#include "mydisk.h"
#include <string.h>
#include <stdio.h>

FILE *thefile;     /* the file that stores all blocks */
int max_blocks;    /* max number of blocks given at initialization */
int disk_type;     /* disk type, 0 for HDD and 1 for SSD */
int cache_enabled; /* is cache enabled? 0 no, 1 yes */

int mydisk_init(char const *file_name, int nblocks, int type)
{
	thefile = fopen(file_name, "wb+");
	char zero[] = "\0";
	int num_to_write = BLOCK_SIZE * nblocks;
	int num_written = 0;
	int counter = 0;
	while (counter < num_to_write){
		num_written += fwrite(zero, 1, 1, thefile);
		counter++;
	}
	if (num_written != num_to_write){
		return 1;
	}
	max_blocks = nblocks;
	disk_type = type;		
	return 0;
}

void mydisk_close()			
{
	fclose(thefile);
}

int mydisk_read_block(int block_id, void *buffer)
{
	if (block_id > max_blocks){
		return 1;
	}
	
	if (cache_enabled) {
		/* TODO: 1. check if the block is cached
		 * 2. if not create a new entry for the block and read from disk
		 * 3. fill the requested buffer with the data in the entry 
		 * 4. return proper return code
		 */
		 
		return 0;
	} else {
		/* TODO: use standard C functiosn to read from disk
		 */
		 //Seek to position
		 int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
		 if (seek_success == -1){
		 	// Error
		 	return 1;
		 }
		 
		 // Read file
		 int num_read = fread(buffer, 1, BLOCK_SIZE, thefile);
		 if (num_read != BLOCK_SIZE){
		 	return 1;
		 }
		return 0;
	}
}

int mydisk_write_block(int block_id, void *buffer)
{
	/* TODO: this one is similar to read_block() except that
	 * you need to mark it dirty
	 */
	 
	 /*
	 * TODO: MARK DIRTY! */
	 if (block_id > max_blocks){
	 	return 1;
	 }
	 
	 //Seek to position
	int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
	if (seek_success == -1){
		// Error
		return 1;
	}
	// Write file
	int num_written = fwrite(buffer, 1, BLOCK_SIZE, thefile);
	if (num_written != BLOCK_SIZE){
		return 1;
	}
	
	return 0;
}

int mydisk_read(int start_address, int nbytes, void *buffer)
{
	int offset, remaining, amount, block_id;
	int cache_hit = 0, cache_miss = 0;
	
	// Parameter check
	if ((start_address < 0) || (start_address + nbytes < start_address) || (start_address + nbytes >= (max_blocks * BLOCK_SIZE))){
		return 1;
	}
	
	// Init needed variables
	block_id = start_address / BLOCK_SIZE;
	remaining = nbytes;
	offset = start_address % BLOCK_SIZE;
	char *temp_buffer = malloc(BLOCK_SIZE);
	
	int bytes_completed = 0;
	while( remaining != 0){
		// Want to read all of this block or more
		if ((remaining + offset) >= BLOCK_SIZE){
			amount = BLOCK_SIZE - offset;
		}
		// Want to read only part of this block
		else{
			amount = remaining;
		}
		//Get block, copy
		mydisk_read_block(block_id,temp_buffer);
		memcpy(buffer + bytes_completed, &temp_buffer[offset], amount);
		// Update counters
		bytes_completed += amount;
		remaining -= amount;
		block_id++;
		// Offset only useful for first block
		offset = 0;
	}
	free(temp_buffer);
	
	/* TODO: 1. first, always check the parameters
	 * 2. a loop which process one block each time
	 * 2.1 offset means the in-block offset
	 * amount means the number of bytes to be moved from the block
	 * (starting from offset)
	 * remaining means the remaining bytes before final completion
	 * 2.2 get one block, copy the proper portion
	 * 2.3 update offset, amount and remaining
	 * in terms of latency calculation, monitor if cache hit/miss
	 * for each block access
	 */
	return 0;
}

int mydisk_write(int start_address, int nbytes, void *buffer)
{
	/* TODO: similar to read, except the partial write problem
	 * When a block is modified partially, you need to first read the block,
	 * modify the portion and then write the whole block back
	 */
	int offset, remaining, amount, block_id;
	int cache_hit = 0, cache_miss = 0;
	
	// Parameter check
	if ((start_address < 0) || (start_address + nbytes < start_address) || (start_address + nbytes >= (max_blocks * BLOCK_SIZE))){
		return 1;
	}
	
	// Init needed variables
	block_id = start_address / BLOCK_SIZE;
	remaining = nbytes;
	offset = start_address % BLOCK_SIZE;
	char *temp_buffer = malloc(BLOCK_SIZE);
	
	int bytes_completed = 0;
	while( remaining != 0){
		// Want to whole block (possibly after offset)
		if ((remaining + offset) >= BLOCK_SIZE){
			amount = BLOCK_SIZE - offset;
		}
		// Want to write only part of the block
		else {
			amount = remaining;
		}
		
		// We have a partial write; read first
		if (amount != BLOCK_SIZE) {
			mydisk_read_block(block_id,temp_buffer);
			// Combine into one block for writing
			memcpy(&temp_buffer[offset], &buffer[bytes_completed], amount);
			mydisk_write_block(block_id, temp_buffer);
		}
		// Full write
		else {
			mydisk_write_block(block_id, &buffer[bytes_completed]);
		}
		
		// Update counters
		bytes_completed += amount;
		remaining -= amount;
		block_id++;
		// Offset only useful for first block
		offset = 0;
	}
	free(temp_buffer);
	 
	return 0;
}
