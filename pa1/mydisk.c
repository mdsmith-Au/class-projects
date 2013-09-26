#include "mydisk.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

FILE *thefile; /* the file that stores all blocks */
int max_blocks; /* max number of blocks given at initialization */
int disk_type; /* disk type, 0 for HDD and 1 for SSD */
int cache_enabled; /* is cache enabled? 0 no, 1 yes */

int mydisk_init(char const *file_name, int nblocks, int type) {
    
    thefile = fopen(file_name, "wb+");
    if (thefile == NULL) {
        return 1;
    }
    
    char zero[] = "\0";
    int num_to_write = BLOCK_SIZE * nblocks;
    int num_written = 0;
    int counter = 0;
    max_blocks = nblocks;
    disk_type = type;
    
    /* Fill file with zeros */
    while (counter < num_to_write) {
        num_written += fwrite(zero, 1, 1, thefile);
        counter++;
    }
    
    /* Unable to write everything; exit */
    if (num_written != num_to_write) {
        return 1;
    }
    return 0;
}

void mydisk_close() {
    fclose(thefile);
}

int mydisk_read_block(int block_id, void *buffer) {
    if (block_id > max_blocks) {
        return 1;
    }

    if (cache_enabled) {
        
        void *cache = get_cached_block(block_id);
        
        /* Cache miss: read from disk and copy to cache */
        if (cache == NULL) {
            cache = create_cached_block(block_id);
            
            /* Seek to position */
            int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
            if (seek_success == -1) {
                return 1;
            }
            
            /* Read file to buffer*/
            int num_read = fread(buffer, 1, BLOCK_SIZE, thefile);
            if (num_read != BLOCK_SIZE) {
                return 1;
            }
            
            /* Copy to cache before exiting */
            memcpy(cache, buffer, BLOCK_SIZE);
            return 0;
        }
        /* Cache hit - read from cache */
        else {
            memcpy(buffer,cache,BLOCK_SIZE);
            return -1;
        }
        
    } 
    
    /* Cache not enabled - normal read/write*/
    else {
        /* Seek to position */
        int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
        if (seek_success == -1) {
            return 1;
        }

        /* Read file to buffer*/
        int num_read = fread(buffer, 1, BLOCK_SIZE, thefile);
        if (num_read != BLOCK_SIZE) {
            return 1;
        }
        return 0;
    }
}

int mydisk_write_block(int block_id, void *buffer) {

    if (block_id > max_blocks) {
        return 1;
    }

    if (cache_enabled) {
        void *cache = get_cached_block(block_id);

        /* Cache miss - execute write as usual */
        if (cache == NULL) {
            
            /* Seek to position */
            int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
            if (seek_success == -1) {
                return 1;
            }
            /* Write to disk */
            int num_written = fwrite(buffer, 1, BLOCK_SIZE, thefile);
            if (num_written != BLOCK_SIZE) {
                return 1;
            }
            return 0;
        }
        /* Cache hit */
        else {
            /* Replace existing content in cache, mark dirty */
            memcpy(cache, buffer, BLOCK_SIZE);
            mark_dirty(block_id);
            return -1;
        }
    }
    
   /* Cache not enabled - read/write as usual */
    else {
        /*Seek to position and write fily*/
        int seek_success = fseek(thefile, block_id * BLOCK_SIZE, SEEK_SET);
        if (seek_success == -1) {
            return 1;
        }
        
        int num_written = fwrite(buffer, 1, BLOCK_SIZE, thefile);
        if (num_written != BLOCK_SIZE) {
            return 1;
        }
        
        return 0;
    }
}

int mydisk_read(int start_address, int nbytes, void *buffer) {
    int offset, remaining, amount, block_id;
    int cache_hit = 0, cache_miss = 0;

    /* Parameter check */
    if ((start_address < 0) || (start_address + nbytes < start_address) || (start_address + nbytes >= (max_blocks * BLOCK_SIZE))) {
        return 1;
    }

    /* Init needed variables */
    block_id = start_address / BLOCK_SIZE;
    remaining = nbytes;
    offset = start_address % BLOCK_SIZE;
    char *temp_buffer = malloc(BLOCK_SIZE);
    int bytes_completed = 0;

    /* Process blocks as necessary */
    while (remaining != 0) {
        /* Want to read all (after offset) of this block or more */
        if ((remaining + offset) >= BLOCK_SIZE) {
            amount = BLOCK_SIZE - offset;
        }            
        
        /* Want to read only part of this block */
        else {
            amount = remaining;
        }
        
        /*Get block, check cache miss/hit */
        int read_return = mydisk_read_block(block_id, temp_buffer);
        
        if (read_return == -1) {
            cache_hit++;
        }
        else {
            cache_miss++;
        }
        /* Copy to buffer, update counters */
        memcpy(buffer + bytes_completed, &temp_buffer[offset], amount);
        bytes_completed += amount;
        remaining -= amount;
        block_id++;
        /* Offset only useful for first block, set to 0 */
        offset = 0;
    }
    free(temp_buffer);
    
    /* Calculate and print latency. disk_type = 0 is HDD, 1 is SSD */
    int latency;
    if (disk_type == 0) {
        latency = (cache_hit * MEMORY_LATENCY + HDD_SEEK + (cache_miss * HDD_READ_LATENCY));
    }
    else {
        latency = (cache_hit * MEMORY_LATENCY + (cache_miss * SSD_READ_LATENCY));
    }
    report_latency(latency);
    
    return 0;
}

int mydisk_write(int start_address, int nbytes, void *buffer) {

    int offset, remaining, amount, block_id;
    int read_cache_hit = 0, read_cache_miss = 0;
    int write_cache_hit = 0, write_cache_miss = 0;

    /* Parameter check, same as read */
    if ((start_address < 0) || (start_address + nbytes < start_address) || (start_address + nbytes >= (max_blocks * BLOCK_SIZE))) {
        return 1;
    }

    /* Init needed variables */
    block_id = start_address / BLOCK_SIZE;
    remaining = nbytes;
    offset = start_address % BLOCK_SIZE;
    char *temp_buffer = malloc(BLOCK_SIZE);
    int bytes_completed = 0;

    /* Process blocks as necessary */
    while (remaining != 0) {
        /* Want to write whole block (possibly after offset) */
        if ((remaining + offset) >= BLOCK_SIZE) {
            amount = BLOCK_SIZE - offset;
        }            
        
        /* Want to write only part of the block */
        else {
            amount = remaining;
        }

        /* We have a partial write; read first, monitor latency */
        if (amount != BLOCK_SIZE) {
            int read_return = mydisk_read_block(block_id, temp_buffer);
            
            if (read_return == -1) {
                read_cache_hit++;
            } else {
                read_cache_miss++;
            }
            
            /* Combine into one block for writing to disk; monitor latency */
            memcpy(&temp_buffer[offset], buffer + bytes_completed, amount);
            int write_return = mydisk_write_block(block_id, temp_buffer);
            
            if (write_return == -1) {
                write_cache_hit++;
            } else {
                write_cache_miss++;
            }
        }

        /* Full write */
        else {
            int write_return = mydisk_write_block(block_id, buffer + bytes_completed);

            if (write_return == -1) {
                write_cache_hit++;
            } else {
                write_cache_miss++;
            }
        }

        /* Update counters */
        bytes_completed += amount;
        remaining -= amount;
        block_id++;
        /* Offset only useful for first block */
        offset = 0;
    }
    free(temp_buffer);

    /* Calculate and print latency */
    int read_latency, write_latency;
    
    /* Hard Drive = 0, SSD = 1 */
    if (disk_type == 0) {
        /* There may not be any reads, and thus no seek */
        if ((read_cache_hit == 0) && (read_cache_miss == 0)) {
            read_latency = 0;
        } 
        /* At least one read, so we consider seek */
        else {
            read_latency = (read_cache_hit * MEMORY_LATENCY + HDD_SEEK + (read_cache_miss * HDD_READ_LATENCY));
        }
        /* On the other hand, there will *always* be a write */
        write_latency = (write_cache_hit * MEMORY_LATENCY + HDD_SEEK + (write_cache_miss * HDD_WRITE_LATENCY));
    } 
    
    else {
        /* No seek to deal with here - if both hit + miss = 0, read = 0 for SSD */
        read_latency = (read_cache_hit * MEMORY_LATENCY + (read_cache_miss * SSD_READ_LATENCY));
        /* Write same as above */
        write_latency = (write_cache_hit * MEMORY_LATENCY + (write_cache_miss * SSD_WRITE_LATENCY));
    }
    report_latency(read_latency + write_latency);

    return 0;
}
