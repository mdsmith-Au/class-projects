#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mydisk.h"

/* The cache entry struct */
struct cache_entry
{
	int block_id;
	int is_dirty;
	char content[BLOCK_SIZE];
};

int cache_blocks;  /* number of blocks for the cache buffer */
struct cache_entry *ring_buffer;
int cache_size;

/**
 * This function will return the index the given block has been given in the
 * cache. Otherwise, it will return -1, signifying that the block does 
 * not exist.
 */
int find_cached_entry(int block_id);


int init_cache(int nblocks)
{
	/* TODO: allocate proper data structure (ring buffer)
	 * initialize entry data so that the the ring buffer is empty
	 */
        cache_blocks = nblocks;
        ring_buffer = malloc(cache_blocks * sizeof(cache_entry));
        cache_size = 0;
        cache_enabled = 1;
	return 0;
}

int close_cache()
{
        /* TODO: release the memory for the ring buffer */
        free(ring_buffer);
        cache_enabled = 0;
        cache_size = 0;
        return 0;
}

void *get_cached_block(int block_id)
{
        // Get entry, if -1, entry not found, else return content
        int cached_entry_pos = find_cached_entry(block_id);
        if (cached_entry_pos == -1){
            return NULL;
        }
        else {
            return ring_buffer[cached_entry_pos].content;
        }
}

void *create_cached_block(int block_id)
{
	/* TODO: create a new entry, insert it into the ring buffer
	 * It might kick an exisitng entry.
	 * Remember to write dirty block back to disk
	 * Note that: think if you can use mydisk_write_block() to 
	 * flush dirty blocks to disk
	 */
	return NULL;
}

void mark_dirty(int block_id)
{
	/* TODO: find the entry and mark it dirty */
}

int find_cached_entry(int block_id){
    int i;
    /*
     * We examine all entries until we get a match
     */
    for (i = 0; i < cache_size; ++i){
        if (ring_buffer[i].block_id == block_id){
            return i;
        }
    }
    return -1;
}

