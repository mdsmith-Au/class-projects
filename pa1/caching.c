#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mydisk.h"

/* The cache entry struct */
struct cache_entry {
    int block_id;
    int is_dirty;
    char content[BLOCK_SIZE];
};

int cache_blocks; /* number of blocks for the cache buffer */
struct cache_entry *ring_buffer;
int cache_size;
int next_insert_pos;

/**
 * This function will return the index the given block has been given in the
 * cache. Otherwise, it will return -1, signifying that the block does 
 * not exist.
 */
int find_cached_entry(int block_id);


int init_cache(int nblocks) {
    /* TODO: allocate proper data structure (ring buffer)
     * initialize entry data so that the the ring buffer is empty
     */
    cache_blocks = nblocks;
    ring_buffer = malloc(cache_blocks * sizeof(struct cache_entry));
    int i;
    // Initialize ring data
    for (i = 0; i < cache_blocks; ++i) {
        ring_buffer[i].block_id = -1;
        ring_buffer[i].is_dirty = 0;
    }
    cache_size = 0;
    cache_enabled = 1;
    next_insert_pos = 0;
    return 0;
}

int close_cache() {
    // Write dirty data
    int i;
    
    for (i = 0; i < cache_size; ++i) {
        struct cache_entry *entry = &ring_buffer[i];
        if (entry->is_dirty == 1) {
            mydisk_write_block(entry->block_id, entry->content);
        }
    }
    
    // Free memory, reset variables
    free(ring_buffer);
    cache_enabled = 0;
    cache_size = 0;
    return 0;
}

void *get_cached_block(int block_id) {
    // Get entry, if -1, entry not found, else return content
    int cached_entry_pos = find_cached_entry(block_id);

    if (cached_entry_pos == -1) {
        return NULL;
    }

    else {
        return ring_buffer[cached_entry_pos].content;
    }
}

void *create_cached_block(int block_id) {
    /* TODO: create a new entry, insert it into the ring buffer
     * It might kick an exisitng entry.
     * Remember to write dirty block back to disk
     * Note that: think if you can use mydisk_write_block() to 
     * flush dirty blocks to disk
     */
    // Entry already exists, return position
    int existing_entry = find_cached_entry(block_id);
    if (existing_entry != -1) {
        return ring_buffer[existing_entry].content;
    }
    
    // Existing entry in cache - if dirty, we write it to disk
    // It could be that there is actually no entry, but init sets is_dirty
    // to 0 in that case
    struct cache_entry *entry = &ring_buffer[next_insert_pos];
    int orig_block_id = entry->block_id;
    if (entry->is_dirty == 1) {
        // Write to disk (cache must be disabled to actually write to disk)
        cache_enabled = 0;
        mydisk_write_block(orig_block_id,entry->content);
        cache_enabled = 1;
    }
    // Now that a dirty block (if any) has been taken care of, we write
    // (or overwrite) an entry
//    free(entry->content);
//    entry = malloc(sizeof(struct cache_entry));
    entry->block_id = block_id;
    entry->is_dirty = 0;
    // Generally, next position to insert is the next in sequence
    // Except at the end, where we go back to 0
    next_insert_pos++;
    if (next_insert_pos == (cache_blocks -1 )) {
        next_insert_pos = 0;
    }
    if (orig_block_id == -1) {
        cache_size++;
    }
    return entry->content;
}

void mark_dirty(int block_id) {
    int entry = find_cached_entry(block_id);
    // We assume the entry exists
    ring_buffer[entry].is_dirty = 1;
}

int find_cached_entry(int block_id) {
    int i;
    /*
     * We examine all entries until we get a match
     */
    for (i = 0; i < cache_size; ++i) {
        if (ring_buffer[i].block_id == block_id) {
            return i;
        }
    }
    return -1;
}

