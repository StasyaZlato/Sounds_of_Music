import os
import pathlib
from json import JSONEncoder

import librosa
import librosa.display
import numpy as np
from matplotlib import pyplot as plt
from torch import is_tensor

import neural_network
from constants import CHORDS_DICT_REVERSED, CHORDS_DICT_FOURIER
from fourier import get_chord


def get_frequency(collection, is_fourier):
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

    if cnt == 0:
        return {key: 0 for key, value in freq.items()}

    return {key: round(value / cnt, 2) for key, value in freq.items()}


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
        self.plot_histogram(dict_freq)
        self.chords = list(
            map(lambda x: CompositionChord(dict_freq[CHORDS_DICT_REVERSED[x.item()]], CHORDS_DICT_REVERSED[x.item()]),
                chords_from_ann))

    def get_chords_with_fourier(self):
        chords_from_fourier = []
        for sample in self.chords_librosa:
            next_chord = get_chord(sample, self.sr)
            if next_chord != 'error':
                chords_from_fourier.append(next_chord)
        dict_freq = get_frequency(chords_from_fourier, True)
        self.plot_histogram(dict_freq)
        self.chords = list(map(lambda x: CompositionChord(dict_freq[x], x), chords_from_fourier))

    def process_composition_ann(self, path_to_model):
        self.stream()
        self.get_features()
        self.get_chords_with_ann(path_to_model)

    def process_composition_fourier(self):
        self.stream()
        self.get_chords_with_fourier()

    def get_file_name_from_path(self):
        head, tail = os.path.split(self.filename)
        index = tail.index('.')
        return tail[:index]

    def librosa_make_graphics(self):
        dirname = self.get_file_name_from_path()
        path_to_graphs = pathlib.Path(__file__).parent.parent.absolute()

        path_to_graphs = os.path.join(path_to_graphs, "generated", dirname)

        print("[INFO] Path to graphs is %s" % path_to_graphs)

        try:
            pathlib.Path(path_to_graphs).mkdir(parents=True, exist_ok=True)
        except OSError:
            print("[ERROR] Creation of the directory %s failed" % dirname)
        else:
            print("[INFO] Successfully created the directory %s " % dirname)

        self.create_waveplot(path_to_graphs)
        self.create_chromagram(path_to_graphs)

    def create_waveplot(self, dirname):
        plt.figure(figsize=(14, 5))
        librosa.display.waveplot(self.y, sr=self.sr)

        filename = os.path.join(dirname, "waveplot.png")

        path_to_waveplot = pathlib.Path(__file__).parent.parent.absolute()

        plt.savefig(os.path.join(path_to_waveplot, filename))

    def create_chromagram(self, dirname):
        X = librosa.stft(self.y)
        Xdb = librosa.amplitude_to_db(abs(X))
        plt.figure(figsize=(14, 5))
        librosa.display.specshow(Xdb, sr=self.sr, x_axis='time', y_axis='hz')
        plt.colorbar()

        filename = os.path.join(dirname, "chromagram.png")

        path_to_chromagram = pathlib.Path(__file__).parent.parent.absolute()

        plt.savefig(os.path.join(path_to_chromagram, filename))

    def plot_histogram(self, frequencies):
        dirname = self.get_file_name_from_path()
        plt.figure(figsize=(14, 5))

        dict_items = frequencies.items()
        dict_keys = list(map(lambda item: item[0][0].upper() + item[0][1:], dict_items))
        dict_values = list(map(lambda item: item[1], dict_items))

        plt.bar(dict_keys, dict_values)

        path_to_histogram = pathlib.Path(__file__).parent.parent.absolute()
        path_to_histogram = os.path.join(path_to_histogram, "generated", dirname)
        print("INFO: path to histogram %s" % path_to_histogram)

        plt.savefig(os.path.join(path_to_histogram, "histogram.png"))

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
