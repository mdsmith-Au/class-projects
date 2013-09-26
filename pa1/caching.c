/*
 * Michael Smith
 * ID: 260481943
 * ECSE 427 Assignment 1
 */
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
struct cache_entry *ring_buffer; /* Actual cache buffer */
int cache_size; /* Current cache size */
int next_insert_pos; /* Where to insert the next cache entry */

/**
 * This function will return the index the given block has been given in the
 * cache. Otherwise, it will return -1, signifying that the block does 
 * not exist.
 */
int find_cached_entry(int block_id);


int init_cache(int nblocks) {

    cache_blocks = nblocks;
    /* Allocate memory in a contiguous block, like a real cache */
    ring_buffer = malloc(cache_blocks * sizeof(struct cache_entry));
    int i;
    /* Initialize cache data */
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
    /* Write dirty data.  We disable the cache so that write_block_entry
     * actually writes to disk */
    cache_enabled = 0;
    int i;
    for (i = 0; i < cache_size; ++i) {
        struct cache_entry *entry = &ring_buffer[i];
        if (entry->is_dirty == 1) {
            mydisk_write_block(entry->block_id, entry->content);
        }
    }
    
    /* Free memory, reset variables */
    free(ring_buffer);
    cache_size = 0;
    next_insert_pos = 0;
    return 0;
}

void *get_cached_block(int block_id) {
    /* Get entry, if -1, entry not found, else return content */
    int cached_entry_pos = find_cached_entry(block_id);

    if (cached_entry_pos == -1) {
        return NULL;
    }

    else {
        return ring_buffer[cached_entry_pos].content;
    }
}

void *create_cached_block(int block_id) {

    /* Entry already exists, return content */
    int existing_entry = find_cached_entry(block_id);
    if (existing_entry != -1) {
        return ring_buffer[existing_entry].content;
    }
    
    
    /* Check entry at our insert position.  It may be empty,
     * but the is_dirty parameter will be 0 then (set in init)
     * so there is no segmentation fault to worry about */
    struct cache_entry *entry = &ring_buffer[next_insert_pos];
    int orig_block_id = entry->block_id;
    if (entry->is_dirty == 1) {
        // Write to disk (cache must be disabled to actually write to disk)
        cache_enabled = 0;
        mydisk_write_block(orig_block_id,entry->content);
        cache_enabled = 1;
    }
    
    /* Now that a dirty block (if any) has been taken care of, we write
     * (or overwrite) an entry. Content is set to zero, but memory
     * has already been allocated so we do not use free or malloc. */
    entry->block_id = block_id;
    entry->is_dirty = 0;
    memset(entry->content,0,BLOCK_SIZE);
    
    /* Generally, next position to insert is the next in sequence
     * except at the end, where we go back to 0 */
    next_insert_pos++;
    if (next_insert_pos == (cache_blocks -1 )) {
        next_insert_pos = 0;
    }
    
    /* Increment size if we are adding entries, otherwise, the buffer is full */
    if (orig_block_id == -1) {
        cache_size++;
    }
    return entry->content;
}

void mark_dirty(int block_id) {
    int entry = find_cached_entry(block_id);
    /* We assume the entry exists */
    ring_buffer[entry].is_dirty = 1;
}

int find_cached_entry(int block_id) {
    int i;
    /* We examine all entries until we get a match.  We then return the
     * index of that entry. Otherwise, we return -1 to signify no match. */
    for (i = 0; i < cache_size; ++i) {
        if (ring_buffer[i].block_id == block_id) {
            return i;
        }
    }
    return -1;
}

