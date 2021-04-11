CHORDS_DICT = {'a': 0, 'am': 1, 'bm': 2, 'c': 3, 'd': 4, 'dm': 5, 'e': 6, 'em': 7, 'f': 8, 'g': 9}

CHORDS_DICT_REVERSED = ['a', 'am', 'bm', 'c', 'd', 'dm', 'e', 'em', 'f', 'g']

CHORDS_DICT_FOURIER = ['c', 'c#', 'd', 'd#', 'e', 'f', 'f#', 'g', 'g#', 'a', 'a#', 'b',
                       'cm', 'c#m', 'dm', 'd#m', 'em', 'fm', 'f#m', 'gm', 'g#m', 'am', 'a#m', 'bm']

FREQ_TABLE = [[16, 33, 65, 131, 262, 523, 1047, 2093, 4186],
              [17, 35, 69, 139, 277, 554, 1109, 2217, 4435],
              [18, 37, 73, 147, 294, 587, 1175, 2349, 4699],
              [19, 39, 78, 156, 311, 622, 1245, 2489, 4978],
              [21, 41, 82, 165, 330, 659, 1319, 2637, 5274],
              [22, 44, 87, 175, 349, 698, 1397, 2794, 5588],
              [23, 46, 93, 185, 370, 740, 1480, 2960, 5920],
              [25, 49, 98, 196, 392, 784, 1568, 3136, 6272],
              [26, 52, 104, 208, 415, 831, 1661, 3322, 6645],
              [28, 55, 110, 220, 440, 880, 1760, 3520, 7040],
              [29, 58, 117, 233, 466, 932, 1865, 3729, 7459],
              [31, 62, 123, 247, 494, 988, 1976, 3951, 7902]]

NOTES_LABELS = ['c', 'c#', 'd', 'd#', 'e', 'f', 'f#', 'g', 'g#', 'a', 'a#', 'b']
NOTES = 12
OCTAVES = 9  # the range table covers

MAX_FREQ = 8000
