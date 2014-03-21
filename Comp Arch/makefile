cc=gcc

CFLAGS=-g -I -Wall

DEPS = ecse425proj.h ecse425projOPT.h
_OBJ = main.o ecse425proj.o ecse425projOPT.o
OBJ = $(patsubst %,$(ODIR)/%,$(_OBJ))
ODIR=build


$(ODIR)/%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

ecse425proj: $(OBJ)
	gcc -o $(ODIR)/$@ $^ $(CFLAGS) $(LIBS)

.PHONY: clean

clean:
	rm -f $(ODIR)/*.o
