#include "fs.h"
#include "ext.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/* constant of how many bits in one freemap entry */
#define SFS_NBITS_IN_FREEMAP_ENTRY (sizeof(u32)*8)

/* in-memory superblock (in consistent with the disk copy) */
static sfs_superblock_t sb;
/* freemap, u32 array (in consistent with the disk copy) */
static u32 *freemap;
/* file descriptor table */
static fd_struct_t fdtable[SFS_MAX_OPENED_FILES];
/* 
 * Flush the in-memory freemap to disk 
 */
static void sfs_flush_freemap() {
    size_t i = 0;
    blkid bid = 1;
    char *p = (char *) freemap;
    /* TODO: write freemap block one by one
     * USE OF I????? */
    for (bid = 1; bid <= sb.nfreemap_blocks; bid++) {
        sfs_write_block(freemap + (BLOCK_SIZE * i), bid);
        i = bid*BLOCK_SIZE;
    }
}

/* 
 * Allocate a free block, mark it in the freemap and flush the freemap to disk
 */
static blkid sfs_alloc_block() {
    u32 size = sb.nfreemap_blocks * BLOCK_SIZE / sizeof (u32);
    u32 i, j;
    /* Implementation-independent way of saying entry is all 1's */
    u32 entry_full = pow(2, SFS_NBITS_IN_FREEMAP_ENTRY) - 1;
    /* TODO: find a freemap entry that has a free block */
    for (i = 0; i < size; i++) {
        if (freemap[i] != entry_full) {
            /* We now have an entry with space: find the space*/
            for (j = 0; j < (sizeof (u32) * 8); j++) {
                if ((freemap[i] & (1 << j)) == 0) {
                    /* Space found; mark as used and return block id */
                    freemap[i] = (freemap[i] | (1 << j));
                    sfs_flush_freemap();
                    return (j  + 1 + (i * SFS_NBITS_IN_FREEMAP_ENTRY));
                }
            }
        }
    }
    return 0;
}

/*
 * Free a block, unmark it in the freemap and flush
 */
static void sfs_free_block(blkid bid) {
    bid--;
    int entry_loc = (bid/8)/sizeof(u32);
    int bit_loc = (bid - (entry_loc * SFS_NBITS_IN_FREEMAP_ENTRY));
    freemap[entry_loc] = (freemap[entry_loc] & ~(1 << bit_loc));
    sfs_flush_freemap();
}

/* 
 * Resize a file.
 * This file should be opened (in the file descriptor table). The new size
 * should be larger than the old one (not supposed to shrink a file)
 */
static void sfs_resize_file(int fd, u32 new_size) {
    /* the length of content that can be hold by a full frame (in bytes) */
    int frame_size = BLOCK_SIZE * SFS_FRAME_COUNT;
    /* old file size */
    int old_size = fdtable[fd].inode.size;
    /* how many frames are used before resizing */
    int old_nframe = (old_size + frame_size - 1) / frame_size;
    /* how many frames are required after resizing */
    int new_nframe = (new_size + frame_size - 1) / frame_size;
    int i, j;
    blkid frame_bid = 0;
    sfs_inode_frame_t frame;
    int new_frame_bids_size = new_nframe - old_nframe;

    /* check if new frames are required */
    if (new_frame_bids_size <= 0) {
        return;
    }
    /* allocate frames */
    
    blkid new_frame_bids[new_frame_bids_size];
    for (i = 0; i < (new_frame_bids_size); i++) {
        new_frame_bids[i] = sfs_alloc_block();
    }
    /* i is now the last index; j will server as a ref to the next index
     to be filled later */
    i = new_frame_bids_size - 1;
    j = 0;
    

    /* TODO: add the new frame to the inode frame list
       Note that if the inode is changed, you need to write it to the disk
     * Two possibilities here.
     * 1. No frames (file size = 0), so we must modify first frame and
     * potentially more
     * 2. Already an initial frame, so go to end and tack on frames
     */
    sfs_inode_t *mem_inode = &fdtable[fd].inode;
    
    
    if (mem_inode->first_frame == 0) {
        
        frame_bid = new_frame_bids[j];
        mem_inode->first_frame = frame_bid;
        int k;
        for (k = 0; k < sizeof (frame.content) / sizeof(blkid); k++) {
            frame.content[k] = sfs_alloc_block();
        }
        frame.next = 0;
        
        sfs_write_block((char *) &frame, frame_bid);
        j++;
    }
    else {
        frame_bid = mem_inode->first_frame;
        sfs_read_block((char *) &frame, frame_bid);
        
        while (frame.next != 0) {
            frame_bid = frame.next;
            sfs_read_block((char *) &frame, frame_bid);
        }
    }
    /* We now have at least 1 frame and it's block id
     * We can now add extra frames if necessary */
    while (j <= i) {
        frame.next = new_frame_bids[j];
        sfs_write_block((char *) &frame, frame_bid);
        
        frame_bid = frame.next;
        frame.next = 0;
        int k;
        for (k = 0; k < sizeof (frame.content) / sizeof(blkid); k++) {
            frame.content[k] = sfs_alloc_block();
        }
        sfs_write_block((char *) &frame, frame_bid);
        j++;
    }
    
    /* Update inode */
    mem_inode->size += frame_size * new_frame_bids_size;
    sfs_write_block( mem_inode, fdtable[fd].inode_bid);
    
    
}

/*
 * Get the bids of content blocks that hold the file content starting from cur
 * to cur+length. These bids are stored in the given array.
 * The caller of this function is supposed to allocate the memory for this
 * array. It is guaranteed that cur+length<size
 * 
 * This function returns the number of bids being stored to the array.
 */
static u32 sfs_get_file_content(blkid *bids, int fd, u32 cur, u32 length) {
        
        /* the starting block of the content */
        u32 start = cur / BLOCK_SIZE;
        /* the ending block of the content */
        u32 end = (cur + length) / BLOCK_SIZE;
        u32 i = 0, j = 0, frame_number = 0;
        sfs_inode_frame_t frame;
        
        sfs_read_block((char *) &frame, fdtable[fd].inode.first_frame);
        
        /* Get to the start block; i is the current block
         * we are looking at.  We may/may not read it, depending if
         * we have arrived at the correct location */
        
        while (i <= end) {
            if (i < start) {
                /* Not yet at start, move on to next block */
                i++;
            }
            else {
                /* Read data*/
                bids[j] = frame.content[i % SFS_FRAME_COUNT];
                j++;
                i++;
            }
            
            /* If next i is in another frame, load new frame */
            if ((i / SFS_FRAME_COUNT) > frame_number) {
                sfs_read_block((char *) &frame, frame.next);
                frame_number++;
            }
        }

        
        return end - start + 1;
}

/*
 * Find the directory of the given name.
 *
 * Return block id for the directory or zero if not found
 */
static blkid sfs_find_dir(char *dirname) {
    blkid dir_bid = 0;
    sfs_dirblock_t dir;
    
    /* TODO: start from the sb.first_dir, treverse the linked list */
    sfs_read_block((char *) &sb, 0);
    dir_bid = sb.first_dir;
    
    while (dir_bid != 0) {
        sfs_read_block((char*) &dir, dir_bid);
        if (strcmp(dir.dir_name, dirname) == 0) {
            return dir_bid;
        }
        dir_bid = dir.next_dir;
    }

    return 0;
}

/*
 * Create a SFS with one superblock, one freemap block and 1022 data blocks
 *
 * The freemap is initialized be 0x3(11b), meaning that
 * the first two blocks are used (sb and the freemap block).
 *
 * This function always returns zero on success.
 */
int sfs_mkfs() {
    /* one block in-memory space for freemap (avoid malloc) */
    static char freemap_space[BLOCK_SIZE];
    int i;
    sb.magic = SFS_MAGIC;
    sb.nblocks = 1024;
    sb.nfreemap_blocks = 1;
    sb.first_dir = 0;
    for (i = 0; i < SFS_MAX_OPENED_FILES; ++i) {
        /* no opened files */
        fdtable[i].valid = 0;
    }
    sfs_write_block(&sb, 0);
    freemap = (u32 *) freemap_space;
    memset(freemap, 0, BLOCK_SIZE);
    /* just to enlarge the whole file */
    sfs_write_block(freemap, sb.nblocks);
    /* initializing freemap */
    freemap[0] = 0x3; /* 11b, freemap block and sb used*/
    sfs_write_block(freemap, 1);
    memset(&sb, 0, BLOCK_SIZE);
    return 0;
}

/*
 * Load the super block from disk and print the parameters inside
 */
sfs_superblock_t *sfs_print_info() {
    sfs_read_block(&sb, 0);
    printf("Reading SFS info from disk. Stand by.\n"
            "Magic number: %u\nNumber of blocks: %u\n"
            "Number of freemap blocks: %u\nBlock ID of first directory: %u\n",
            sb.magic, sb.nblocks, sb.nfreemap_blocks, sb.first_dir);
    return &sb;
}

/*
 * Create a new directory and return 0 on success.
 * If the dir already exists, return -1.
 */
int sfs_mkdir(char *dirname) {
    sfs_read_block((char *) &sb, 0);
    /* TODO: test if the dir exists */
    if (sfs_find_dir(dirname) != 0) {
        return -1;
    }
    /* TODO: insert a new dir to the linked list */
    blkid dir_bid = sb.first_dir;
    sfs_dirblock_t dir;
    /* Find empty directory spot */
    /* No existing directories*/
    if (dir_bid == 0) {
        strncpy(dir.dir_name, dirname, 120);
        dir.next_dir = 0;
        memset(dir.inodes,0,sizeof(dir.inodes));
        blkid new_block = sfs_alloc_block();
        sfs_write_block((char *) &dir, new_block);
        sb.first_dir = new_block;
        sfs_write_block((char *) &sb, 0);
    }
    /* Already directories present; iterate and find room */
    else {
        while (dir_bid != 0) {
            sfs_read_block((char *) &dir, dir_bid);
            if (dir.next_dir == 0) {
                /* We create the new directory 
                 * Step 1: update pointer of existing */
                blkid new_bid = sfs_alloc_block();
                dir.next_dir = new_bid;
                sfs_write_block((char *) &dir, dir_bid);
                /* Now we fill in our new block */
                strncpy(dir.dir_name, dirname, 120);
                memset(dir.inodes, 0, sizeof(dir.inodes));
                dir.next_dir = 0;
                sfs_write_block((char *) &dir, new_bid);
                dir_bid = 0;
            }
            else {
                dir_bid = dir.next_dir;
            }
        }
        
    }
    return 0;
}

/*
 * Remove an existing empty directory and return 0 on success.
 * If the dir does not exist or still contains files, return -1.
 */
int sfs_rmdir(char *dirname) {
    /* TODO: check if the dir exists */
    sfs_read_block((char *) &sb, 0);
    
    blkid dir_bid = sfs_find_dir(dirname);
    if (dir_bid == 0) {
        return -1;
    }
    
    /* TODO: check if no files */
    sfs_dirblock_t dir;
    sfs_read_block((char *) &dir, dir_bid);
    
    int i;
    for (i = 0; i < (sizeof (dir.inodes) / sizeof (blkid)); i++) {
        if (dir.inodes[i] != 0) {
            return -1;
        }
    }
    
    /* Get link of directory we will delete to patch the list later */
    blkid next_dir_blkid = dir.next_dir;

    /* Go thru the linked list and delete the dir */

    blkid dir_bid2 = sb.first_dir;
    /* Special case: delete root dir*/
    if (dir_bid2 == dir_bid) {
        sfs_free_block(dir_bid);
        sb.first_dir = 0;
        sfs_write_block((char *) &sb, 0);
    } 
    
    else {
        while (dir_bid2 != 0) {
            sfs_read_block((char *) &dir, dir_bid2);
            if (dir.next_dir == dir_bid) {
                dir.next_dir = next_dir_blkid;
                sfs_free_block(dir_bid);
                sfs_write_block((char *) &dir, dir_bid2);
            }
            dir_bid2 = dir.next_dir;
        }
    }

    return 0;
}

/*
 * Print all directories. Return the number of directories.
 */
int sfs_lsdir() {
    /* TODO: go thru the linked list */
    sfs_read_block((char *) &sb, 0);
    
    blkid dir_bid = sb.first_dir;
    sfs_dirblock_t dir;
    int num_dir = 0;
    int i;
    printf("Directory listing:\n");
    
    while (dir_bid != 0) {
        sfs_read_block((char *) &dir, dir_bid);
        num_dir++;
        
        for (i = 1; i <= num_dir; i++) {
            printf("\t");
        }
        
        printf("%s\n" , dir.dir_name);

        dir_bid = dir.next_dir;
    }
    
    return num_dir;
}

/*
 * Open a file. If it does not exist, create a new one.
 * Allocate a file desriptor for the opened file and return the fd.
 */
int sfs_open(char *dirname, char *name) {
    blkid dir_bid = 0, inode_bid = 0;
    sfs_inode_t inode;
    sfs_dirblock_t dir;
    int fd;
    int i;

    fd = -1;
    /* TODO: find a free fd number */
    for (i = 0; i < sizeof(fdtable) / sizeof(fd_struct_t); i++ ) {
        if (fdtable[i].valid == 0) {
            fd = i;
            break;
        }
    }
    /* No free fd numbers*/
    if (fd == -1) {
        return -1;
    }

    /* TODO: find the dir first */
    dir_bid = sfs_find_dir(dirname);
    if (dir_bid == 0) {
        return -1;
    }
    
    sfs_read_block((char *) &dir, dir_bid);

    /* TODO: traverse the inodes to see if the file exists.
       If it exists, load its inode. Otherwise, create a new file.
     */
    for (i = 0; i < (sizeof (dir.inodes) / sizeof (blkid)); i++) {

        if (dir.inodes[i] != 0) {
            sfs_read_block(&inode, dir.inodes[i]);

            if (strcmp(inode.file_name, name) == 0) {
                /* File exists, load */
                fdtable[fd].cur = 0;
                fdtable[fd].dir_bid = dir_bid;
                fdtable[fd].inode_bid = dir.inodes[i];
                memcpy(&fdtable[fd].inode, &inode, sizeof (inode));
                fdtable[fd].valid = 1;
                return fd;
            }
        }

    }
    /* File does not exist, create new file */
    for (i = 0; i < (sizeof (dir.inodes) / sizeof (blkid)); i++) {
        
        /* Empty inode, use it*/
        if (dir.inodes[i] == 0) {
            blkid inode_bid = sfs_alloc_block();
            dir.inodes[i] = inode_bid;
            sfs_write_block((char *) &dir, dir_bid);
            
            strncpy(inode.file_name, name, SFS_MAX_FILENAME_LEN);
            inode.first_frame = 0;
            inode.size = 0;
            sfs_write_block(&inode, inode_bid);
            
            fdtable[fd].cur = 0;
            fdtable[fd].dir_bid = dir_bid;
            fdtable[fd].inode_bid = inode_bid;
            fdtable[fd].valid = 1;
            memcpy(&fdtable[fd].inode, &inode, sizeof (inode));
            break;
        }
    }
    
    return fd;
}

/*
 * Close a file. Just mark the valid field to be zero.
 */
int sfs_close(int fd) {
    /* TODO: mark the valid field */
    fdtable[fd].valid = 0;
    return 0;
}

/*
 * Remove/delete an existing file
 *
 * This function returns zero on success.
 */
int sfs_remove(int fd) {
    blkid frame_bid, dir_bid, inode_bid;
    sfs_dirblock_t dir;
    sfs_inode_t inode;
    sfs_inode_frame_t frame;
    int i;

    /* TODO: update dir */
    dir_bid = fdtable[fd].dir_bid;
    sfs_read_block((char *) &dir, dir_bid);
    inode_bid = fdtable[fd].inode_bid;
    
    for (i = 0; i < sizeof( dir.inodes ) / sizeof (blkid); i++) {
        if (dir.inodes[i] == inode_bid) {
            dir.inodes[i] = 0;
            sfs_write_block((char * ) &dir, dir_bid);
            break;
        }
    }

    /* TODO: free inode and all its frames */
    sfs_read_block((char *) &inode, inode_bid);
    frame_bid = inode.first_frame;
    
    while (frame_bid != 0) {
        sfs_read_block((char *) &frame, frame_bid);
        sfs_free_block(frame_bid);
        frame_bid = frame.next;
        
    }
    
    sfs_free_block(inode_bid);

    /* TODO: close the file */
    sfs_close(fd);
    
    return 0;
}

/*
 * List all the files in all directories. Return the number of files.
 */
int sfs_ls() {
    /* TODO: nested loop: traverse all dirs and all containing files*/
    blkid dir_bid = sb.first_dir;
    sfs_dirblock_t dir;
    sfs_inode_t inode;
    int i;
    int num_files = 0;
    
    printf("Filesystem list:\n");
    
    while (dir_bid != 0) {
        sfs_read_block((char *) &dir, dir_bid);
        printf("Directory %s\n", dir.dir_name);
        for (i = 0; i < sizeof (dir.inodes) / sizeof(blkid); i++) {
            if (dir.inodes[i] != 0) {
                sfs_read_block((char *) &inode, dir.inodes[i]);
                printf("\t%s\n", inode.file_name);
                num_files++;
            }
        }
        printf("\n");
        dir_bid = dir.next_dir;
    }
    return num_files;
}

/*
 * Write to a file. This function can potentially enlarge the file if the 
 * cur+length exceeds the size of file. Also you should be aware that the
 * cur may already be larger than the size (due to sfs_seek). In such
 * case, you will need to expand the file as well.
 * 
 * This function returns number of bytes written.
 */
int sfs_write(int fd, void *buf, int length) {
    int remaining, offset, to_copy;
    blkid *bids;
    char *p = (char *) buf;
    char tmp[BLOCK_SIZE];
    u32 cur = fdtable[fd].cur;

    /* TODO: check if we need to resize */
    if (( cur + length) > fdtable[fd].inode.size) {
        sfs_resize_file(fd, cur + length);
    }
    

    /* TODO: get the block ids of all contents (using sfs_get_file_content() */
    bids = malloc( sizeof(blkid) * (fdtable[fd].inode.size / BLOCK_SIZE));
    
    sfs_get_file_content(bids, fd, cur, length);

    /* TODO: main loop, go through every block, copy the necessary parts
       to the buffer, consult the hint in the document. Do not forget to 
       flush to the disk.
     */
    
    remaining = length;
    offset = cur % BLOCK_SIZE;
    int bytes_completed = 0;
    
    while (remaining != 0) {
        
        /* Want to write whole block*/
        if ((remaining + offset) >= BLOCK_SIZE) {
            to_copy = BLOCK_SIZE - offset;
        }
        /* Want to write partial block */
        else {
            to_copy = remaining;
        }
        
        blkid block_id = (cur / BLOCK_SIZE);
        /* Partial write processing */
        if (to_copy != BLOCK_SIZE) {
            
            sfs_read_block(tmp, bids[block_id]);
            
            /* Combine into one block */
            memcpy(&tmp[offset], p + bytes_completed, to_copy);
            
            sfs_write_block(tmp, bids[block_id]);
            
        }
        
        /* Full write*/
        else {
            sfs_write_block(p + bytes_completed, bids[block_id]);
            
        }
        
        /* Update variables */
        bytes_completed += to_copy;
        remaining -= to_copy;
        offset = 0;
        cur += to_copy;
    }
    /* TODO: update the cursor and free the temp buffer
       for sfs_get_file_content()
     */
    fdtable[fd].cur = cur;
    free(bids);
    return bytes_completed;
}

/*
 * Read from an opend file. 
 * Read can not enlarge file. So you should not read outside the size of 
 * the file. If the read exceeds the file size, its result will be truncated.
 *
 * This function returns the number of bytes read.
 */
int sfs_read(int fd, void *buf, int length) {
    int remaining, to_copy, offset;
    blkid *bids;
    char *p = (char *) buf;
    char tmp[BLOCK_SIZE];
    u32 cur = fdtable[fd].cur;

    /* TODO: check if we need to truncate */
    if (cur > fdtable[fd].inode.size) {
        return 0;
    }
    else if (( cur + length - 1) > fdtable[fd].inode.size) {
        length = fdtable[fd].inode.size - cur;
    }
    
    /* TODO: similar to the sfs_write() */
    

    /* Get the block ids of all contents (using sfs_get_file_content() */
    bids = malloc( sizeof(blkid) * (fdtable[fd].inode.size / BLOCK_SIZE));
    
    sfs_get_file_content(bids, fd, cur, length);
    
    remaining = length;
    offset = cur % BLOCK_SIZE;
    int bytes_completed = 0;
    
    while (remaining != 0) {
        
        /* Want to read whole block*/
        if ((remaining + offset) >= BLOCK_SIZE) {
            to_copy = BLOCK_SIZE - offset;
        }
        /* Want to read partial block */
        else {
            to_copy = remaining;
        }
        
        blkid block_id = (cur / BLOCK_SIZE);

        /* Get block */
        sfs_read_block(tmp, bids[block_id]);

        /* Copy to buffer, update counters */
        memcpy(p + bytes_completed, &tmp[offset], to_copy);
        
        /* Update variables */
        bytes_completed += to_copy;
        remaining -= to_copy;
        offset = 0;
        cur += to_copy;
    }
    /* TODO: update the cursor and free the temp buffer
       for sfs_get_file_content()
     */
    fdtable[fd].cur = cur;
    free(bids);
    return bytes_completed;
}

/* 
 * Seek inside the file.
 * Loc is the starting point of the seek, which can be:
 * - SFS_SEEK_SET represents the beginning of the file.
 * - SFS_SEEK_CUR represents the current cursor.
 * - SFS_SEEK_END represents the end of the file.
 * Relative tells whether to seek forwards (positive) or backwards (negative).
 * 
 * This function returns 0 on success.
 */
int sfs_seek(int fd, int relative, int loc) {
    /* TODO: get the old cursor, change it as specified by the parameters */
    if (loc == SFS_SEEK_SET) {
        fdtable[fd].cur = relative;
    }
    else if ((loc == SFS_SEEK_CUR) || (loc == SFS_SEEK_END)) {
        fdtable[fd].cur += relative;
    }
    else {
        return 1;
    }
    return 0;
}

/*
 * Check if we reach the EOF(end-of-file).
 * 
 * This function returns 1 if it is EOF, otherwise 0.
 */
int sfs_eof(int fd) {
    if (fdtable[fd].cur > fdtable[fd].inode.size ) {
        return 1;
    }
    else {
        return 0;
    }
}
