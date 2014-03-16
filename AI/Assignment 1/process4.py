import pprint


if __name__=="__main__":

    def prune(list_to_store, position):

        words = ['add', 'ado', 'age', 'ago', 'aid', 'ail', 'aim', 'air', 'and', 'any', 'ape', 'apt', 'arc', 'are', 'ark', 'arm', 'art', 'ash', 'ask', 'auk', 'awe', 'awl', 'aye', 'bad', 'bag', 'ban', 'bat', 'bee', 'boa', 'ear', 'eel', 'eft', 'far', 'fat', 'fit', 'lee', 'oaf', 'rat', 'tar', 'tie']
        for i in words:
             letter_others_start_with = i[position]
             for j in words:
                 if letter_others_start_with == j[0]:
                     list_to_store.append(j)

    list_middle = []
    list_end = []

    prune(list_middle, 1)
    prune(list_end, 2)

    print("middle list " + str(list_middle))
    print("end list " + str(list_end))
    
    final_list = set(list_middle) & set(list_end)

    print("Final list: " + str(final_list))
    print("Lengh: " + str(len(final_list)))
