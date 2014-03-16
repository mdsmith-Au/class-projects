import sys
import random
import copy

def parse_input_file(f_name):
    """Read and parse the 'problem definition' file with name 'f_name'."""
    f_lines = [[w.strip() for w in l.split(' ')] for l in open(f_name).readlines()]
    word_list = []
    i = 0
    # Read the available word list, with words presented one-to-a-line from
    # the first line of the file up to the initial state description.
    while (f_lines[i][0] != 'STATE'):
        word_list.append(f_lines[i][0])
        i = i + 1
    i = i + 1
    # Now, parse the start state description, which comes after the definition
    # of the available word list.
    start_state = {}
    while (f_lines[i][0] != 'STATE'):
        start_state[f_lines[i][0]] = f_lines[i][1]
        i = i + 1
    return [word_list, start_state]

def random_state(state_dict, word_list):
    """Generate a random mapping of across/down positions to the words in
    word_list, given the board structure implied by state_dict."""
    max_idx = len(word_list) - 1
    for key in state_dict.keys():
        state_dict[key] = word_list[random.randint(0,max_idx)]
    return state_dict

def print_state(state_dict, out_file=' '):
    """Output the given state (an assignment of words to positions)."""
    # Assuming a properly formatted state dict, print the state to some file,
    # or stdout if no file is given, in the required format.
    if (out_file == ' '):
        out_file = sys.stdout
    print >>out_file, "STATE START"
    for (key, val) in state_dict.items():
        print >>out_file, "{0} {1}".format(key, val)
    print >>out_file, "STATE END"
    return

def print_state_list(state_list, out_file_name):
    """Print a list of state dicts to a file with the given name."""
    out_file = open(out_file_name,'w')
    for state_dict in state_list:
        print_state(state_dict, out_file)
    out_file.close()
    return

def parse_state_file(f_name):
    """Parse the given file into a list of state dicts."""
    try:
        f_lines = [l.strip() for l in open(f_name).readlines()]
    except:
        print "Could not open file: {0}.".format(str(f_name))
        sys.exit(0)
    state_dicts = []
    new_state = 0
    # Run a scan over all lines in the file indicated by f_name
    i = 0
    while (i < len(f_lines)):
        state_lines = []
        # Set aside the lines in the file describing the current state
        if (f_lines[i] == 'STATE START'):
            i = i + 1
            while (f_lines[i] != 'STATE END'):
                state_lines.append(f_lines[i])
                i = i + 1
        else:
            # Skip lines of unknown purpose
            i = i + 1
        # Parse the lines describing the current state into a dict. Each line
        # in a state description (aside from the special start/end lines)
        # should be, e.g., "A1 word1" to map "word1" to position 1 across, or
        # "D4 word2" to map "word2" to position 4 down. For python purposes, we
        # make a dict with keys corresponding to across/down positions and
        # values corresponding to the words mapped to those positions.
        s_dict = {}
        for s_line in state_lines:
            position, word = s_line.split(' ')
            s_dict[position] = word
        state_dicts.append(s_dict)
        i = i + 1
    return state_dicts

def hill_climb(state_dict, word_list):
    """Executes the well-known hill-climbing algorithm,
    using state_dict as a starting point."""

    continue_climb = True
    while (continue_climb):

        neighbours = []
        # Num conflicts  in current state
        num_conflicts = score_state(state_dict, word_list)

        # Create list of neighbours.  Will contain combinations
        # of D1,D2,D3, A1,A2,A3 in the 3x3 case
        for j in state_dict:
            for i in word_list:
                temp_state = copy.deepcopy(state_dict)
                temp_state[j] = i
                neighbours.append(temp_state)

        # Keep track of min # conflicts; init to large #
        min_conflicts = sys.maxsize

        # Neighbours now generated; find values
        for neighbour in neighbours:
            # Better option found than previously; set as best
            if score_state(neighbour,word_list) < min_conflicts:
                best_neighbour = neighbour
                min_conflicts = score_state(neighbour,word_list)

        # Best result we found not good enough (optimun), leave
        if num_conflicts <= min_conflicts:
            continue_climb = False

        # Result better than before
        else:
            state_dict = best_neighbour

    return state_dict



def count_conflicts(state_dict):
    """Count the number of across/down word conflicts in the given state."""
    indices = set()
    across_words = {}
    down_words = {}
    # Infer the "board size" from the set of keys in state_dict. This means we
    # aren't assuming a specific board dimension (e.g. 3 x 3). Simultaneously,
    for (key, val) in state_dict.items():
        idx = int(key[1:]) - 1
        indices.add(idx)
        if (key[0] == 'A'):
            across_words[idx] = val
        elif (key[0] == 'D'):
            down_words[idx] = val
        else:
            print "Bad state: {0}".format(str(state_dict))
            sys.exit(0)
    indices = list(indices)
    indices.sort()
    max_idx = max(indices)
    # Get lists of across and down words, sorted by position. If the board
    # shape implied by the collection of across/down word assignments is not
    # square, exit and print some (hopefully helpful) feedback.
    try:
        across_sorted = [across_words[i] for i in range(max_idx+1)]
        down_sorted = [down_words[i] for i in range(max_idx+1)]
    except:
        print "Mismatching across/down assignments: {0:d} x {1:d}".format( \
                len(across_words.keys()), len(down_words.keys()))
        print "  across assignments: {0:s}".format(str(across_words))
        print "  down assignments: {0:s}".format(str(across_words))
        sys.exit(0)
    # Check for conflicts between across/down words.
    conflicts = 0
    for i in range(len(across_sorted)):
        a_word = across_sorted[i]
        if (len(a_word) != (max_idx + 1)):
            print "Wrong length word '{0:s}' for {1:d} x {1:d} board.".format(a_word, (max_idx + 1))
            sys.exit(0)
        for j in range(len(a_word)):
            d_word = down_sorted[j]
            if (len(d_word) != (max_idx + 1)):
                print "Wrong length word '{0:s}' for {1:d} x {1:d} board.".format(a_word, (max_idx + 1))
                sys.exit(0)
            if (a_word[j] != d_word[i]):
                conflicts = conflicts + 1
    return conflicts

def is_full_state(state_dict, word_list):
    """Check if all across/down positions in this state are associated with
    some word from word_list."""
    word_set = set(word_list)
    full_state = 1
    for (key, value) in state_dict.items():
        if not (value in word_set):
            full_state = 0
    return full_state

def is_valid_state(start_state, proposed_state):
    """Check that the set of across/down locations described by start_state are
    all present in proposed_state."""
    valid_state = 1
    for key in start_state.keys():
        if not proposed_state.has_key(key):
            print "key: {0:s} is missing from dict {1:s}".format(str(key),str(proposed_state))
            valid_state = 0
    return valid_state

def score_state(state_dict, word_list):
    """Count the number of across/down word conflicts present in the state
    encoded by state_dict. Scoring is only performed for "full states", i.e.
    those in which all across/down positions are mapped to words in word_list.
    """
    if is_full_state(state_dict, word_list):
        return count_conflicts(state_dict)
    else:
        print "{0:s} is not a full state.".format(str(state_dict))
        return -1

def check_solution(input_file, output_file):
    """This function will be used in grading your submitted code. If you pass
    the names of the input file used to define a problem instance and the name
    name of the output file produced by your code as arguments to this, the
    returned result and displayed output will let you know if your program
    produced an acceptable output from the given input. Note that the first
    state recorded in output_file should be equivalent to the initial state
    encoded in input_file. If this condition does not hold, you will receive
    no credit."""
    [word_list, start_state] = parse_input_file(input_file)
    output_states = parse_state_file(output_file)
    # First, check for equivalence of start_state and first output state.
    for (key, value) in start_state.items():
        if not output_states[0].has_key(key):
            print "Mismatched start state in {0} and first state in {1}.".format(input_file, output_file)
        else:
            if not (output_states[0][key] == value):
                print "Mismatched start state in {0} and first state in {1}.".format(input_file, output_file)
    # Check values of all valid states in output_file
    out_state_vals = [score_state(s, word_list) for s in output_states]
    out_state_vals = [val for val in out_state_vals if (val >= 0)]
    min_val = min(out_state_vals)
    start_val = score_state(start_state, word_list)
    print "Found {0:d} conflicts in start state from {1:s}.".format(start_val, input_file)
    print "Found {0:d} conflicts in best state from {1:s}.".format(min_val, output_file)
    return {'start_val': start_val, 'min_val': min_val}



########################
# General instructions #
#----------------------#########################################################
# The current file can be run from the command line in either of two modes.    #
#                                                                              #
# To run a simple demo, which reads from "input_file" and writes a set of      #
# states (in the format which the grading script can understand) to            #
# "output_file", type: "python csp_base_code.py 1 input_file output_file".     #
#                                                                              #
# To run a demo of the grading process, which also serves to verify that the   #
# output of your code will be parseable by the grading script, given the       #
# file input_file which was input to your code and the file output_file which  #
# your code wrote, type: "python csp_base_code.py 2 input_file output_file".   #
#                                                                              #
# Note: Basically, you want the "score" associated your input/output file pair #
#       to be as _large_ as possible, corresponding to a greater reduction in  #
#       conflicts from the start state in input_file to the least conflicted   #
#       state described in output_file.                                        #
#                                                                              #
# Note: For students coding in java, your code should be executable as         #
#       follows: "java csp_solver.java input_file output_file", where          #
#       input_file will be in the same format as the example input that was    #
#       disseminated alongside this file, and output_file is such that running #
#       the current file in "grading" mode (i.e. mode 2) gives a reasonable    #
#       result. If your executable java file is not named csp_solver.java,     #
#       you will receive no credit.                                            #
#                                                                              #
# Note: For students coding in python, your code should be executable as       #
#       follows: "python csp_solver.py input_file output_file", with the       #
#       meaning of this incantation the same as described for java code.       #
################################################################################

#####################################
# Input / output format description #
#-----------------------------------############################################
# A valid state description, i.e. one which will be understood by the grading  #
# code (this file, basically), starts with the words "STATE START" on a        #
# single line and concludes with "STATE END" on a single line. All lines in    #
# the meat of a state description, i.e. those following some "STATE START"     #
# and preceeding its corresponding "STATE END", should comprise two terms: a   #
# location "O#" and a word "word" in the format "O# word". Here, "O" is an     #
# orientation character (either "A" or "D" for across/down), and "#" is the    #
# index of a valid board position with that orientation (e.g. one across is    #
# given by "A1" and three down is given by "D3". "word" can be any word, but   #
# board states will only be scored if all words present in a state description #
# were in the relevant input file's word list. Board states are expected to    #
# encode square boards, with an equal number of across and down positions, all #
# indexed by integers > 0. For a board with dimension N x N, all words mapped  #
# to some across/down position should have length N.                           #
#                                                                              #
# This rather tedious description is captured more succinctly in the starting  #
# state encoded in the file "example_input.txt" that is being distributed      #
# along with the current file. Boards used in testing submissions are not      #
# guaranteed to be of size 3 x 3, so don't code this assumption into your      #
# submitted work. The state description format generalizes easily.             #
################################################################################

############################################
# More info about python code in this file #
#------------------------------------------#####################################
# Code in this file assumes that state descriptions are encoded in a python    #
# dictionary (a hashmap or whatever), with keys in the form "O#" and values in #
# the form "word", with "O#" and "word" as described above for input/output    #
# formatting. For students providing solutions in python, the functions you    #
# will find most immediately useful are probably "parse_input_file", which     #
# parses an input file of the format that will be used in grading into a list  #
# of valid words and a dictionary representation of an initial state, and      #
# "print_state_list", which prints a list of state dicts into some file. The   #
# state dicts passed to print_state_list should be appropriately formatted.    #
#                                                                              #
# Again, your code should reproduce the given initial state as the first state #
# in its output file. You should also use "check_solution" to check if the     #
# output file produced by your code for a given input file is well-formatted   #
# and encodes some solution which improves upon the initial state encoded in   #
# the relevant input file. Feel free to use the other functions in this file   #
# too, and they should be well-enough documented for easy use.                 #
################################################################################

if __name__=="__main__":
    if (len(sys.argv) != 3):
        print "usage: python {0} input_file output_file".format(sys.argv[0])
        sys.exit(0)
    else:
        in_file = sys.argv[1]
        out_file_name = sys.argv[2]

    [word_list, start_state] = parse_input_file(in_file)

    out_file = open(out_file_name, 'w')

    #print "Printing {0} copies of start state to {1}.".format(copies, str(out_file))

    print_state(start_state, out_file)

    solution_found = False
    num_iterations = 0

    while(not solution_found):

        rand_state = random_state(start_state,word_list)
        climbed_state = hill_climb(rand_state, word_list)
        num_iterations = num_iterations + 1

        print_state(climbed_state, out_file)

        if (count_conflicts(climbed_state) == 0) or (num_iterations > 1000):
            solution_found = True
	    print str(climbed_state)


    out_file.close()

    print "Parsing states described by {0}.".format(out_file_name)
    state_dicts = parse_state_file(out_file_name)

    print "{0:s} described {1:d} states.".format(out_file_name, len(state_dicts))

    print "Checking the score of output file {0}, given input file {1}.".format(out_file_name, in_file)
    result = check_solution(in_file, out_file_name)


