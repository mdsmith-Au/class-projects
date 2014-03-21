#ifndef ECSE425PROJ_H
#define ECSE425PROJ_H 

/* Specify the data layout type */
typedef enum { RowMaj, ColMaj} DLayout ;

/*
 * Assignes random data of type double to the vector
 * addressed by vPtr
 */
void randInitialize(int sz, double *vPtr);

/*
 * Displays a vector using matlab's format
 */
void displayVec(int sz, const double *vPtr);

/*
 * Displays a matrix using matlab's format
 * Assumes row-major format
 */
void displayMat(int rows, int cols, const double *vPtr);

/*
 * Computes the operation matrix-matrix multiply as:
 * matC = matA x matB
 * Assumes row-major format
 */
void matMult(int N, const double *matA, const double *matB, double *matC);

/*
 * Computes the operation matrix-vector multiply as:
 * vecC = matA x vecB
 * Assumes row-major format for matrix matA
 */
void matVecMult(int N, const double *matA, const double *vecB, double *vecC);

/*
 * Verifies the operation matrix-matrix multiply given data as:
 * matC = matA x matB
 * Assumes row-major format
 */
void verifyMatMult(int N, const double *matA, const double *matB,
        const double *matC);

/*
 * Verifies the operation matrix-vector multiply given data as:
 * vecC = matA x vecB
 * Assumes row-major format for matrix matA
 */
void verifyMatVecMult(int N, const double *matA, const double *vecB,
        const double *vecC);

/*
 * Computes the (squared) normalized l2 distance between two vectors as:
 * SUM|src - ref|^2 / SUM|ref|^2
 */
double compareVecs(int N, const double *src, const double *ref);


#endif /* ECSE425PROJ_H */
