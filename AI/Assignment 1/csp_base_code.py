import copy
import sys
import random

def parse_input_file(f_name):
    """Read and parse the 'problem definition' file with name 'f_name'."""
    f_lines = [[w.strip() for w in l.split(' ')] for l in open(f_name).readlines()]
    word_list = []
    i = 0
    while (f_lines[i][0] != 'STATE'):
        word_list.append(f_lines[i][0])
        i = i + 1
    i = i + 1
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
    if (out_file == ' '):
        out_file = sys.stdout
    print >>out_file, "STATE START"
    for (key, val) in state_dict.items():
        print >>out_file, "{0} {1}".format(key, val)
    print >>out_file, "STATE END"
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
            i = i + 1
        # Parse the description of the current state into a dict
        s_dict = {}
        for s_line in state_lines:
            position, word = s_line.split(' ')
            s_dict[position] = word
        state_dicts.append(s_dict)
        i = i + 1
    return state_dicts

def count_conflicts(state_dict):
    """Count the number of across/down word conflicts in the given state."""
    indices = set()
    across_words = {}
    down_words = {}
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
    # Infer "board size" from the key/value pairs in the state dict.
    max_idx = max(indices)
    # Get lists of across and down words, sorted by position.
    across_sorted = [across_words[i] for i in range(max_idx+1)]
    down_sorted = [down_words[i] for i in range(max_idx+1)]
    # Check for conflicts between across/down words
    conflicts = 0
    for i in range(len(across_sorted)):
        a_word = across_sorted[i]
        for j in range(len(a_word)):
            d_word = down_sorted[j]
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
    """Check the number of across/down word conflicts are present in the state
    encoded by state_dict. Scoring is only performed for "full states", i.e.
    those in which all across/down positions are mapped to words in word_list.
    """
    if is_full_state(state_dict, word_list):
        return count_conflicts(state_dict)
    else:
        print "{0:s} is not a full state.".format(str(state_dict))
        return -1

def check_solution(input_file, output_file):
    """This is function will be used in grading your submitted code. If you
    pass the names of the input file used to define a problem instance and the
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


def hill_climb(state_dict, word_list):
    """Executes the well-known hill-climbing algorithm,
    using state_dict as a starting point."""

    continue_climb = True
    while (continue_climb):

        neighbours = []
        num_conflicts = score_state(state_dict, word_list)

        # Get letters of A1
        first_letter_a1 = state_dict["A1"][0]
        second_letter_a1 = state_dict["A1"][1]
        third_letter_a1 = state_dict["A1"][2]

        # Get letters of D1
        first_letter_d1 = state_dict["D1"][0]
        second_letter_d1 = state_dict["D1"][1]
        third_letter_d1 = state_dict["D1"][2]

        # Generate pruned word lists of appropriate letters to start with
        word_list_d1 = []
        for i in word_list:
            if i[0] == first_letter_a1:
                word_list_d1.append(i)

        word_list_d2 = []
        for i in word_list:
            if i[0] == second_letter_a1:
                word_list_d2.append(i)

        word_list_d3 = []
        for i in word_list:
            if i[0] == third_letter_a1:
                word_list_d3.append(i)

        word_list_a1 = []
        for i in word_list:
            if i[0] == first_letter_d1:
                word_list_a1.append(i)

        word_list_a2 = []
        for i in word_list:
            if i[0] == second_letter_d1:
                word_list_a2.append(i)

        word_list_a3 = []
        for i in word_list:
            if i[0] == third_letter_d1:
                word_list_a3.append(i)

        # Create list of neighbours.  Will contain combinations
        # of D1,D2,D3, A1,A2,A3
        for i in word_list_d1:
            temp_state = copy.deepcopy(state_dict)
            temp_state["D1"] = i
            neighbours.append(temp_state)

        for i in word_list_d2:
            temp_state = copy.deepcopy(state_dict)
            temp_state["D2"] = i
            neighbours.append(temp_state)

        for i in word_list_d3:
            temp_state = copy.deepcopy(state_dict)
            temp_state["D3"] = i
            neighbours.append(temp_state)

        for i in word_list_a1:
            temp_state = copy.deepcopy(state_dict)
            temp_state["A1"] = i
            neighbours.append(temp_state)

        for i in word_list_a2:
            temp_state = copy.deepcopy(state_dict)
            temp_state["A2"] = i
            neighbours.append(temp_state)

        for i in word_list_a3:
            temp_state = copy.deepcopy(state_dict)
            temp_state["A3"] = i
            neighbours.append(temp_state)

        #print("neighbours: " + str(neighbours))

        min_conflicts = sys.maxsize

        # Neighbours now generated; find values
        for neighbour in neighbours:
            if score_state(neighbour,word_list) < min_conflicts:
                best_neighbour = neighbour
                min_conflicts = score_state(neighbour,word_list)

        # Best result we found not good enough (optimun), leave
        if num_conflicts <= min_conflicts:
            continue_climb = False

        else:
            state_dict = best_neighbour

    return state_dict


################
# Instructions #
#--------------#################################################################
# This file can be run from the command line in either of two modes.           #
#                                                                              #
# To run a simple demo, which reads from "input_file" and writes a set of      #
# states (in the format which the grading script can understand) to            #
# "output_file", type: "python this_file 1 input_file output_file".            #
#                                                                              #
# To run a demo of the grading process, which also serves to verify that the   #
# output of your code will be parseable by the grading script, given the       #
# file input_file which was input to your code and the file output_file which  #
# was output by your code, type: "python this_file 2 input_file output_file".  #
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
################################################################################

if __name__=="__main__":
    if (len(sys.argv) != 4):
        print "usage: python {0} mode input_file output_file".format(sys.argv[0])
        sys.exit(0)
    else:
        mode = int(sys.argv[1])
        in_file = sys.argv[2]
        out_file_name = sys.argv[3]
    if (mode == 1):
        print "Parsing input file: {0}".format(in_file)
        [word_list, start_state] = parse_input_file(in_file)

        out_file = open(out_file_name, 'w')

        #print "Printing {0} copies of start state to {1}.".format(copies, str(out_file))

        print_state(start_state, out_file)

        solution_found = False

        while(not solution_found):

            rand_state = random_state(start_state,word_list)
            climbed_state = hill_climb(rand_state, word_list)


            print_state(climbed_state, out_file)

            if (count_conflicts(climbed_state) == 0):
                solution_found = True


        out_file.close()

        print "Parsing states described by {0}.".format(out_file_name)
        state_dicts = parse_state_file(out_file_name)


        print "{0:s} described {1:d} states.".format(out_file_name, len(state_dicts))
    elif (mode == 2):
        print "Checking the score of output file {0}, given input file {1}.".format(out_file_name, in_file)
        result = check_solution(in_file, out_file_name)
    else:
        print "Mode {0:d} does not exist!".format(mode)



