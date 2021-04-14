from os import walk

from gtda.diagrams import PersistenceEntropy
from gtda.homology import VietorisRipsPersistence
from sklearn.ensemble import RandomForestClassifier, ExtraTreesClassifier, BaggingClassifier
from sklearn.model_selection import train_test_split
from tqdm import tqdm
import pickle
from pydub import AudioSegment
import os
from constants import NOTES_LABELS

from sklearn.preprocessing import StandardScaler
from sklearn.datasets import make_moons, make_circles, make_classification
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier
from sklearn.naive_bayes import GaussianNB

from sklearn.pipeline import make_pipeline


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


def process_dataset():
    files = df["song_id"].tolist()

    notes_dict = {NOTES_LABELS[i]: i for i in range(len(NOTES_LABELS))}

    for file in tqdm(files, desc="processing tracks"):
        id = file
        file = dataset_folder + str(file) + ".mp3"

        if os.path.isfile(file.replace(".mp3", "_4.npy")):
            tonnetz = np.load(file.replace(".mp3", "_4.npy"))
            X_data.append(np.array(tonnetz))
            y_data.append(df.query('song_id==' + str(id))['Genre'].item())
            continue

        if file.endswith(".mp3"):
            sound = AudioSegment.from_mp3(file)
            sound.export(file.replace(".mp3", ".wav"), format="wav")
            file = file.replace("mp3", "wav")

        tonnetz_file_name = file.replace(".wav", "_4.npy")
        composition = Composition(file, chord_duration=1.5)
        chords_dct = composition.process_composition_fourier_for_tda_notes()

        # tonnetz = [[[0 for n in NOTES_LABELS] for n in NOTES_LABELS] for n in NOTES_LABELS]
        tonnetz = [[0 for m in NOTES_LABELS] for n in NOTES_LABELS]

        for n1 in range(len(NOTES_LABELS)):
            for n2 in range(len(NOTES_LABELS)):
                note_1 = NOTES_LABELS[n1]
                note_2 = NOTES_LABELS[n2]
                cnt = 0

                for chord in chords_dct:
                    if note_1 in chord and note_2 in chord:
                        cnt += 1.0
                cnt /= len(chords_dct)

                tonnetz[n1][n2] = cnt
                tonnetz[n2][n1] = cnt

        np.save(tonnetz_file_name, np.array(tonnetz))

        # tonnetz = np.load(file)
        os.remove(file)

        X_data.append(np.array(tonnetz))
        y_data.append(df.query('song_id==' + str(id))['Genre'].item())


def get_notes(file):
    matrix = np.load(file)

    # notes_dict = {NOTES_LABELS[i]: i for i in range(len(NOTES_LABELS))}
    notes = NOTES_LABELS

    st = set()

    matrix_tonnetz = [[0 for i in range(len(notes))] for j in range(len(notes))]

    for n in range(len(notes)):
        for n1 in range(len(notes)):
            for n2 in range(len(notes)):
                if tuple(sorted([n, n1, n2])) not in st:
                    st.add(tuple(sorted([n, n1, n2])))
                    val = matrix[n][n1][n2]
                    matrix_tonnetz[n][n1] += val
                    matrix_tonnetz[n1][n] += val
                    matrix_tonnetz[n][n2] += val
                    matrix_tonnetz[n2][n] += val
                    matrix_tonnetz[n1][n2] += val
                    matrix_tonnetz[n2][n1] += val

    return matrix_tonnetz


def load_dataset_2():
    files = df["song_id"].tolist()

    notes_dict = {NOTES_LABELS[i]: i for i in range(len(NOTES_LABELS))}

    for file in tqdm(files, desc="processing tracks"):
        id = file
        file = dataset_folder + str(file) + ".mp3"



        if os.path.isfile(file.replace(".mp3", ".npy")):
            tonnetz = get_notes(file.replace(".mp3", ".npy"))
            X_data.append(np.array(tonnetz))
            y_data.append(df.query('song_id==' + str(id))['Genre'].item())



def load_dataset_3():
    files = df["song_id"].tolist()

    # notes_dict = {NOTES_LABELS[i]: i for i in range(len(NOTES_LABELS))}

    for file in tqdm(files, desc="processing tracks"):
        id = file
        file = dataset_folder + str(file) + ".mp3"
        tonnetz_file = file.replace(".mp3", "_1.npy")

        if os.path.isfile(file.replace(".mp3", ".npy")):
            tonnetz = np.load(file.replace(".mp3", ".npy"))
            X_data.append(np.array(tonnetz))
            y_data.append(df.query('song_id==' + str(id))['Genre'].item())
            continue
        else:
            if file.endswith(".mp3"):
                sound = AudioSegment.from_mp3(file)
                sound.export(file.replace(".mp3", ".wav"), format="wav")
                file = file.replace("mp3", "wav")

            y, sr = librosa.load(file)
            tonnetz = librosa.feature.tonnetz(y, sr)

            os.remove(file)
            X_data.append(tonnetz)
            y_data.append(df.query('song_id==' + str(id))['Genre'].item())

# load_dataset_3()
process_dataset()

print("dataset loaded")

base_cls = ExtraTreesClassifier()

steps = [VietorisRipsPersistence(homology_dimensions=[0, 1, 2], metric="precomputed"),
         PersistenceEntropy(normalize=True),
         ExtraTreesClassifier(n_estimators=35, max_depth=None, min_samples_split=20, random_state=42)]

pipeline = make_pipeline(*steps)

pcs_train, pcs_valid, labels_train, labels_valid = train_test_split(X_data, y_data)

pipeline.fit(pcs_train, labels_train)

print(pipeline.score(pcs_valid, labels_valid))
print(pipeline.score(pcs_train, labels_train))



# VR = VietorisRipsPersistence(homology_dimensions=[0, 1, 2, 3])  # Parameter explained in the text
# diagrams = VR.fit_transform(np.array(X_data))
#
# print("persistence diagrams ready")
#
# PE = PersistenceEntropy()
# features = PE.fit_transform(diagrams)
#
# print("feature calculated")
#
# model = RandomForestClassifier()
# model.fit(features, y_train)
#
# X_valid_feature = PE.transform(VR.transform(X_valid))
#
# print(model.score(X_valid_feature, y_valid))
# print(model.score(features, y_train))
#
#
# model_filename = 'random_forest'
# pickle.dump(model, open(model_filename, 'wb'))
