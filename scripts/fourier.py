import librosa
import pydub
import numpy as np
import scipy
import os
import sys

from pydub import AudioSegment
from pydub.utils import make_chunks
from constants import freq_table, notes_labels, notes, octaves, max_freq

def get_err_margins():
    err_margins = [0, 1]
    curr_margin = 2
    for _ in range(octaves - 2):
        err_margins.append(curr_margin)
        curr_margin *= 2
    return err_margins

def frequency_to_note(freq):
    err_margins = get_err_margins()
    if freq < 0 or freq > max_freq:
        return 'invalid'
    for i in range(notes):
        for j in range(octaves):
            if abs(freq - freq_table[i][j]) <= err_margins[j]:
                return notes_labels[i] + str(j)
    return 'unknown'
    
def are_adjacent_notes(first, second):
    if len(first) > 1:
        first = first[:-1]
    if len(second) > 1:
        second = second[:-1]
    return abs(ord(first) - ord(second)) == 1 or first == 'A' and second == 'G' \
           or first == 'G' and second == 'A' or first == second
           
def check_chord_valid(chord, note_to_add):
    for note in chord:
        if are_adjacent_notes(note, note_to_add):
            return False
    return True
    
def get_chord_notes(notes):
    chord = []
    curr_index = 0
    while len(chord) < 3 and curr_index < len(notes):
        curr_note = notes[curr_index]
        curr_index += 1
        if curr_note == 'unknown':
            continue
        else:
            curr_note = curr_note[:-1]
        if not curr_note in chord and check_chord_valid(chord, curr_note):
            chord.append(curr_note)
        
    return chord
    
def transform(samples, sampling_rate):
    n = len(samples)
    t = 1 / sampling_rate
    rb = 1.0 / (2.0 * t)
    yf = scipy.fft.fft(samples)
    xf = np.linspace(0.0, rb, n//2)
    return xf, yf
    
def get_frequencies(xf, yf):
    n = len(yf)
    found_frequencies = []
    tmp = yf[:n//2]
    for _ in range(len(yf)):
        curr_max_index = np.argmax(tmp)
        found_frequencies.append(xf[curr_max_index])
        tmp = np.delete(tmp, curr_max_index)
        if tmp.size == 0 or xf[curr_max_index] > 8000:
            break
    return found_frequencies
    
def get_notes(freqs):
    notes = []
    for f in freqs:
        notes.append(frequency_to_note(f))
    return notes
    
def determine_third(root, third):
    root_i = notes_labels.index(root)
    third_i = notes_labels.index(third)
    diff = third_i - root_i
    if diff == 4 or diff == -8:
        return 'major'
    if diff == 3 or diff == -9:
        return 'minor'
    return '!! error !!'
    
def determine_fifth(root, fifth):
    root_i = notes_labels.index(root)
    fifth_i = notes_labels.index(fifth)
    diff = fifth_i - root_i
    if diff == 7 or diff == -5:
        return 'perfect'
    if diff == 8 or diff == -4:
        return 'aug'
    if abs(diff) == 6:
        return 'dim'
    return '!! error !!'
    
def determine_chord(notes):
    notes.sort()
    # figure out root note
    # in case it's G
    if ord(notes[2][0]) - ord(notes[1][0]) > 2:
        tmp = notes[2]
        notes = notes[:-1]
        notes.insert(0, tmp)
    root_note = notes[0]
    # figure out interval for major/minor/etc
    fifth = determine_fifth(notes[0], notes[2])
    third = determine_third(notes[0], notes[1])
    if fifth == 'perfect':
        if third == 'major':
            return root_note
        elif third == 'minor':
            return root_note + 'm'
        else:
            return 'error'
    elif fifth == 'aug':
        if third == 'major':
            return root_note + fifth
        else:
            return 'error'
    elif fifth == 'dim':
        if third == 'minor':
            return root_note + fifth
        else:
            return 'error'
    else:
        return 'error'
        
def split(path, chunk_duration_ms):
    myaudio = AudioSegment.from_file(path, "wav") 
    chunks = make_chunks(myaudio, chunk_duration_ms)
    chunk_names = []
    for i, chunk in enumerate(chunks): 
        chunk_name = "{0}.wav".format(i) 
        chunk_names.append(chunk_name)
        #print ("exporting", chunk_name) 
        chunk.export(chunk_name, format="wav") 
    return chunk_names
    
def write_chords_to_file(chords, filepath):
    file = open(filepath, 'w')
    for chord in chords:
        file.write(chord + ';')
    file.close()
    
def process_file(path):
    chunks = split(path, 1000)
    chords = []
    for chunk in chunks:
        next_chord = get_chord_from_sample(chunk)
        os.remove(chunk)
        determined = determine_chord(next_chord)
        if chord != 'error':
            chords.append(determined)
    write_chords_to_file(chords, "test.csv")
    
# filename = sys.argv[1]
# process_file(filename)

def get_chord(samples, sampling_rate):
    # samples, sampling_rate = librosa.load(filepath, sr=None, mono=True, offset=0.0, duration=None)
    # if len(samples) / sampling_rate > 5:
    #     chord = ['too long']
    #     return chord
    get_err_margins()
    xf, yf = transform(samples, sampling_rate)
    freqs = get_frequencies(xf, yf)
    notes = get_notes(freqs)
    chord_notes = get_chord_notes(notes)
    return determine_chord(chord_notes)