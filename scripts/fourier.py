import numpy as np
import scipy
from pydub import AudioSegment
from pydub.utils import make_chunks

from constants import FREQ_TABLE, NOTES_LABELS, NOTES, OCTAVES, MAX_FREQ


def get_err_margins():
    err_margins = [0, 1]
    curr_margin = 2
    for _ in range(OCTAVES - 2):
        err_margins.append(curr_margin)
        curr_margin *= 2
    return err_margins


def frequency_to_note(freq):
    err_margins = get_err_margins()
    if freq < 0 or freq > MAX_FREQ:
        return 'invalid'
    for i in range(NOTES):
        for j in range(OCTAVES):
            if abs(freq - FREQ_TABLE[i][j]) <= err_margins[j]:
                return NOTES_LABELS[i] + str(j)
    return 'unknown'


def are_adjacent_notes(first, second):
    if len(first) > 1:
        first = first[:-1]
    if len(second) > 1:
        second = second[:-1]
    return abs(ord(first) - ord(second)) == 1 or first == 'a' and second == 'g' or \
        first == 'g' and second == 'a' or first == second


def check_chord_valid(chord, note_to_add):
    if len(note_to_add) > 2:
        return False
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
        if curr_note not in chord and check_chord_valid(chord, curr_note):
            chord.append(curr_note)
    return chord


def transform(samples, sampling_rate):
    n = len(samples)
    t = 1 / sampling_rate
    rb = 1.0 / (2.0 * t)
    yf = scipy.fft.fft(samples)
    xf = np.linspace(0.0, rb, n // 2)
    return xf, yf


def get_frequencies(xf, yf):
    n = len(yf)
    found_frequencies = []
    tmp = yf[:n // 2]
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
    root_i = NOTES_LABELS.index(root)
    third_i = NOTES_LABELS.index(third)
    diff = third_i - root_i
    if diff == 4 or diff == -8:
        return 'major'
    if diff == 3 or diff == -9:
        return 'minor'
    return '!! error !!'


def determine_fifth(root, fifth):
    root_i = NOTES_LABELS.index(root)
    fifth_i = NOTES_LABELS.index(fifth)
    diff = fifth_i - root_i
    if diff == 7 or diff == -5:
        return 'perfect'
    if diff == 8 or diff == -4:
        return 'aug'
    if abs(diff) == 6:
        return 'dim'
    return '!! error !!'


def determine_chord(notes):
    if len(notes) != 3:
        return 'error'
    notes.sort()
    # figure out root note
    # in case it's G
    if ord(notes[2][0]) - ord(notes[1][0]) > 2:
        tmp = notes[2]
        notes = notes[:-1]
        notes.insert(0, tmp)
    root_note = notes[0]
    # figure out interval for major/minor/etc
    # fifth = determine_fifth(notes[0], notes[2])
    third = determine_third(notes[0], notes[1])
    if third == 'major':
        return root_note
    elif third == 'minor':
        return root_note + 'm'
    else:
        return 'error'


def split(path, chunk_duration_ms):
    my_audio = AudioSegment.from_file(path, "wav")
    chunks = make_chunks(my_audio, chunk_duration_ms)
    chunk_names = []
    for i, chunk in enumerate(chunks):
        chunk_name = "{0}.wav".format(i)
        chunk_names.append(chunk_name)
        # print ("exporting", chunk_name)
        chunk.export(chunk_name, format="wav")
    return chunk_names


def write_chords_to_file(chords, filepath):
    file = open(filepath, 'w')
    for chord in chords:
        file.write(chord + ';')
    file.close()


def get_chord(samples, sampling_rate):
    get_err_margins()
    xf, yf = transform(samples, sampling_rate)
    freqs = get_frequencies(xf, yf)
    notes = get_notes(freqs)
    chord_notes = get_chord_notes(notes)
    return determine_chord(chord_notes)
