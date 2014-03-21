- You can use the provided compile script to compile the code.

- Do not modify the header file or change any of the existing function
  definitions.  The TAs will recompile your code using their own
  header file with the same function definitions in order to verify
  the correctness of your answers.

- You will need to complete some of the function definitions in the
  header files such as the functions matMult and matVecMult which will
  contain your naive (unoptimized) algorithms.

- Write your optimized methods using the function names provided in
  the header file ecse425projOPT.h, use the implementation file
  ecse425projOPT.c to code your implementation in.

- Each function in the provided header file assumes correct memory
  allocation by the user.

- Choose one matrix format for all input matrices. Do not mix
  row-major with column-major formats, especially for matrix-matrix
  multiply functions.

- Compile your code without linking with any external library.  The
  TAs will compile your code the very same way as the sample program.
  If you have any special requirements for the compilation process,
  you need to state it clearly.

- To simulate your program with valgrind (cachegrind), make sure that
  you turn off results verification and display functions in order not
  to interfere with your simulation.  Execute the following command
  from the build/ directory:
      $> valgrind --tool=cachegrind ./ecse425proj -operation 0 -N 100

  You can add your specific options to valgrind.  This command will
  display some output and produce a file with more details named as
  "cachegrind.out.<pid>", where <pid> is the process id.  You can
  obtain better presentation of these details such as miss-rate
  results per function call or even per code line, by executing the
  following command: $> cg_annotate cachegrind.out.<pid> Refer to the
  valgrind manual section "cachegrind" for more details.
