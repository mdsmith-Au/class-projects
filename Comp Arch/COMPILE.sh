#!/bin/bash

# Set the compile flags
# Always include debug symbols (-g) when using valgrind.
# Sometimes you might want to turn on optimizations (-O2 or -O3), in
# particular for the competition.
FLAGS="-g -Wall"

# List all the c files here
SOURCE="main.c ecse425proj.c ecse425projOPT.c"

# create directory "build" if it doesn't exist
[ -d build ] || mkdir build

# compile! (this will generate an executable named "ecse425proj" in
# the directory "build/")
gcc $FLAGS $SOURCE -o build/ecse425proj