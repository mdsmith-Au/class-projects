#include "ecse425proj.h"
#include "ecse425projOPT.h"

#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>


#define PROBSIZE 10

int main(int argc, const char *argv[])
{
    // Seed the random number generator using time
    srand48((unsigned int) time(NULL));

    // Dimension of the operation with defaul value
    int N = PROBSIZE;

    // Specify operation: 0 MatMult; 1 MatVecMult
    int opr = 0;

    // Whether to verify the result or not
    int verif = 0;

    // Whether to display the result or not
    int disp = 0;

    // Parse command line
    {
        int arg_index = 1;
        int print_usage = 0;

        while (arg_index < argc)
        {
            if ( strcmp(argv[arg_index], "-N") == 0 )
            {
                arg_index++;
                N = atoi(argv[arg_index++]);
            }
            else if ( strcmp(argv[arg_index], "-operation") == 0 )
            {
                arg_index++;
                opr = atoi(argv[arg_index++]);
            }
            else if ( strcmp(argv[arg_index], "-help") == 0 )
            {
                print_usage = 1;
                break;
            }
            else if( strcmp(argv[arg_index], "-verif") == 0 )
            {
                arg_index++;
                verif = 1;
            }
            else if( strcmp(argv[arg_index], "-disp") == 0 )
            {
                arg_index++;
                disp = 1;
            }
            else
            {
                printf("***Invalid argument: %s\n", argv[arg_index]);
                print_usage = 1;
                break;
            }
        }

        if (print_usage)
        {
            printf("\n");
            printf("Usage: %s [<options>]\n", argv[0]);
            printf("\n");
            printf("  -N <N>          : problem size (default: %d)\n", PROBSIZE);
            printf("  -operation <ID> : Operation ID = 0 for MatMult or ID = 1 for MatVecMult\n");
            printf("  -verif          : Activate verification\n");
            printf("  -disp           : Display result (use only for small N!)\n");
            printf("  -help           : Display this message\n");
            printf("\n");
        }

        if (print_usage)
            return 0;
    }

    // Perform operation
    switch(opr)
    {
        case 0: /* Matrix-matrix multiply */
            {
                printf("Performing matrix-matrix multiply operation\n");
                double *matA, *matB, *matC1, *matC2;

                // Allocate memory
                matA = (double *) malloc(N*N * sizeof(double));
                matB = (double *) malloc(N*N * sizeof(double));
                matC1 = (double *) malloc(N*N * sizeof(double));
                matC2 = (double *) malloc(N*N * sizeof(double));

                // Initialize matrix values
                randInitialize(N*N,matA);
                randInitialize(N*N,matB);

                // Perform naive matA x matB = matC1
                clock_t tic, toc;
                double tm;

                tic = clock();
                matMult(N,matA,matB,matC1);
                toc = clock();
                tm = (double)(toc - tic) / CLOCKS_PER_SEC;
                printf("Elapsed time for naive mat-mat mult.: %f seconds\n",tm);

                // Perform optimized matA x matB = matC2
                tic = clock();
                matMult_opt(N,matA,matB,matC2);
                toc = clock();
                tm = (double)(toc - tic) / CLOCKS_PER_SEC;
                printf("Elapsed time for optimized mat-mat mult.: %f seconds\n",tm);

                // Verify results
                if(verif)
                    compareVecs(N,matC2,matC1);

                // Display results (don't use for large matrices)
                if(disp)
                {
                    displayMat(N,N,matA);
                    printf("\n");
                    displayMat(N,N,matB);
                    printf("\n");
                    displayMat(N,N,matC1);
                    printf("\n");
                }

                // Free memory
                free(matA);
                free(matB);
                free(matC1);
                free(matC2);
            }
            break;

        case 1: /* Matrix-vector multiply */
            {
                printf("Performing matrix-vector multiply operation\n");
                double *matA, *vecB, *vecC1,*vecC2;

                // Allocate memory
                matA = (double *) malloc(N*N * sizeof(double));
                vecB = (double *) malloc(N*N * sizeof(double));
                vecC1 = (double *) malloc(N*N * sizeof(double));
                vecC2 = (double *) malloc(N*N * sizeof(double));

                // Initialize values
                randInitialize(N*N,matA);
                randInitialize(N,vecB);

                // Perform naive matA x vecB = vecC1
                clock_t tic, toc;
                double tm;

                tic = clock();
                matVecMult(N,matA,vecB,vecC1);  // You can replace this with your optimized methdo
                toc = clock();
                tm = (double)(toc - tic) / CLOCKS_PER_SEC;
                printf("Elapsed time for naive mat-vec mult.: %f seconds\n",tm);

                // Perform optimized matA x vecB = vecC2
                tic = clock();
                matVecMult_opt(N,matA,vecB,vecC2);  // You can replace this with your optimized methdo
                toc = clock();
                tm = (double)(toc - tic) / CLOCKS_PER_SEC;
                printf("Elapsed time for optimized mat-vec mult.: %f seconds\n",tm);

                // Verify results
                if(verif)
                    compareVecs(N,vecC2,vecC1);

                // Display results (don't use for large matrices)
                if(disp)
                {
                    displayMat(N,N,matA);
                    printf("\n");
                    displayVec(N,vecB);
                    printf("\n");
                    displayVec(N,vecC1);
                    printf("\n");
                }

                // Free memory
                free(matA);
                free(vecB);
                free(vecC1);
                free(vecC2);
            }
            break;

        default:
            printf(" Invalid operation ID\n");
            return 0;
    }


    return 0;
}
