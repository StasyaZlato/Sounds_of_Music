from json import JSONEncoder

import librosa
import numpy as np

import neural_network
from constants import CHORDS_DICT_REVERSED, CHORDS_DICT_FOURIER, CHORDS_DICT
from fourier import get_chord
from torch import is_tensor
from matplotlib import pyplot as plt
from create_graphics import get_file_name_from_path
import os


def get_frequency(collection, is_fourier):
    freq = {}
    if is_fourier:
        freq = {key: 0 for key in CHORDS_DICT_FOURIER}
    else:
        freq = {key: 0 for key in CHORDS_DICT_REVERSED}

    cnt = 0
    for el in collection:
        if is_tensor(el):
            el = CHORDS_DICT_REVERSED[el.item()]
        freq[el] += 1
        cnt += 1

    return {key: value / cnt for key, value in freq.items()}


def plot_histogram(frequencies, filename):
    dirname = get_file_name_from_path(filename)
    plt.figure(figsize=(14, 5))
    plt.bar(frequencies.keys(), frequencies.values())
    res_file = os.path.join(dirname, "histogram.png")
    plt.savefig(res_file)


class Composition:
    def __init__(self, filename, chord_duration=2):
        self.filename = filename
        self.chord_duration = chord_duration

        self.y, self.sr = self.load()
        self.duration = librosa.get_duration(self.y, self.sr)

        self.chords_librosa = []

        self.chords = []
        self.chords_features = []

    def load(self):
        return librosa.load(self.filename, sr=None)

    def stream(self):
        file_duration = librosa.get_duration(self.y, self.sr)

        offset = 0

        while offset < file_duration:
            chord_y, chord_sr = librosa.load(self.filename, sr=self.sr, offset=offset, duration=self.chord_duration)
            offset += self.chord_duration

            self.chords_librosa.append(chord_y)

    def get_features(self):
        features = list(map(lambda x: librosa.feature.chroma_stft(y=x, sr=self.sr), self.chords_librosa))

        self.chords_features = [list(map(lambda x: np.mean(x).item(), chord)) for chord in features]

    def get_chords_with_ann(self, path_to_model):
        chords_from_ann = neural_network.predict_chords(self.chords_features, path_to_model)
        dict_freq = get_frequency(chords_from_ann, False)
        plot_histogram(dict_freq, self.filename)
        print(dict_freq)
        self.chords = list(
            map(lambda x: CompositionChord(dict_freq[CHORDS_DICT_REVERSED[x.item()]], CHORDS_DICT_REVERSED[x.item()]), chords_from_ann))

    def get_chords_with_fourier(self):
        chords_from_fourier = []
        for sample in self.chords_librosa:
            next_chord = get_chord(sample, self.sr)
            if next_chord != 'error':
                chords_from_fourier.append(next_chord)
        dict_freq = get_frequency(chords_from_fourier, True)
        plot_histogram(dict_freq, self.filename)
        self.chords = list(map(lambda x: CompositionChord(dict_freq[x], x), chords_from_fourier))

    def process_composition_ann(self, path_to_model):
        self.stream()
        self.get_features()
        self.get_chords_with_ann(path_to_model)

    def process_composition_fourier(self):
        self.stream()
        self.get_chords_with_fourier()

    class CompositionEncoder(JSONEncoder):
        def default(self, o):
            if isinstance(o, Composition):
                dct = o.__dict__

                dct_to_serialize = {
                    "filename": dct["filename"],
                    "sr": dct["sr"],
                    "duration": dct["duration"],
                    "chords": list(map(lambda x: x.encode(), dct["chords"]))}

                return dct_to_serialize
            return JSONEncoder.default(self, o)


class CompositionChord:
    def __init__(self, frequency, chord):
        self.frequency = frequency
        self.chord = chord

    def encode(self):
        return self.__dict__
