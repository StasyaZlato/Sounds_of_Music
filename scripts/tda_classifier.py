from os import walk

from gtda.diagrams import PersistenceEntropy
from gtda.homology import VietorisRipsPersistence
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from tqdm import tqdm
import pickle
from pydub import AudioSegment
import os

import librosa

from composition import Composition
from composition import get_frequency

import numpy as np
import pandas as pd

dataset_folder = "/Users/nastya_iva/Downloads/MEMD_audio/"

X_data = []
# X_data_lib = []
y_data = []

i = 0

df = pd.read_csv("/Users/nastya_iva/Downloads/metadata/metadata_2013.csv")

df = df[["song_id", "Genre"]]

files = df["song_id"].tolist()

# files = list(map(lambda f: dataset_folder + str(f) + ".mp3", files))


# for genre in tqdm(genres, desc="processing genres"):
#     folder = dataset_folder + "/" + genre
#     files = []
#     for (dirpath, dirnames, filenames) in walk(data):
#         files.extend(filenames)
#         break

for file in tqdm(files, desc="processing tracks"):
    print(file)
    id = file
    file = dataset_folder + str(file) + ".mp3"
    # if file.endswith(".wav"):
    #     continue
    # file = folder + "/" + file
    if file.endswith(".mp3"):
        sound = AudioSegment.from_mp3(file)
        sound.export(file.replace(".mp3", ".wav"), format="wav")
        # os.remove(file)
        file = file.replace("mp3", "wav")

    tonnetz_file_name = file.replace("wav", "npy")
    tonnetz_librosa_file_name = file.replace(".wav", "_1.npy")
    composition = Composition(file)
    composition.process_composition_fourier()

    dict_freqs = get_frequency(list(map(lambda x: x.chord, composition.chords)), is_fourier=True)

    tonnetz_ch = [["f#", "c#", "g#", "d#"],
                  ["a#m", "fm", "cm", "gm"],
                  ["a#", "f", "c", "g"],
                  ["dm", "am", "em", "bm"],
                  ["d", "a", "e", "b"],
                  ["f#m", "c#m", "g#m", "d#m"]]
    tonnetz = [[dict_freqs[chord] for chord in line] for line in tonnetz_ch]
    np.save(tonnetz_file_name, np.array(tonnetz))

    y, sr = librosa.load(file, sr=None)
    lib_tonnetz = librosa.feature.tonnetz(y, sr)
    np.save(tonnetz_librosa_file_name, lib_tonnetz)

    # tonnetz = np.load(file)
    X_data.append(tonnetz)

    y_data.append(df.query('song_id==' + str(id))['Genre'].item())
    os.remove(file)
i += 1

print("dataset loaded")

VR = VietorisRipsPersistence(homology_dimensions=[0, 1, 2])  # Parameter explained in the text
diagrams = VR.fit_transform(X_data)

print("persistence diagrams ready")

PE = PersistenceEntropy()
features = PE.fit_transform(diagrams)

print("feature calculated")

X_train, X_valid, y_train, y_valid = train_test_split(features, y_data)
model = RandomForestClassifier()
model.fit(X_train, y_train)
print(model.score(X_valid, y_valid))

model_filename = 'random_forest'
pickle.dump(model, open(model_filename, 'wb'))
