import json
from os import walk

import librosa
import numpy as np
import torch
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from torch import nn
from tqdm import tqdm, trange

from constants import CHORDS_DICT
from dataset_entity import DatasetEntity


def get_folders(path):
    folders = []
    for (dirpath, dirnames, filenames) in walk(path):
        folders.extend(dirnames)
        break

    return folders


def load_dataset(path):
    chord_folders = get_folders(path)

    files = {}
    for chord in chord_folders:
        for (dirpath, dirnames, filenames) in walk(path + "/" + chord):
            filenames = [path + "/" + chord + "/" + i for i in filenames]
            if chord in files.keys():
                files[chord] = files[chord].extend(filenames)
            else:
                files[chord] = filenames

    return files


def get_features(files, instrument):
    audio_with_feature_lst = []

    for chord in tqdm(files.keys(), desc="processing chord folders for {}".format(instrument)):
        chord_files = files[chord]

        for f in tqdm(chord_files, desc="processing files in {}".format(chord)):
            y, sr = librosa.load(f)

            feature = list(map(lambda x: np.mean(x).item(), librosa.feature.chroma_stft(y=y, sr=sr)))

            duration = librosa.get_duration(y, sr)

            audio_with_feature_lst.append(DatasetEntity(f, chord, feature, duration, instrument))
    return audio_with_feature_lst, instrument


def save_features_json(name, audio_with_feature_lst):
    with open(name, 'w', encoding='utf-8') as f:
        json.dump([ob.__dict__ for ob in audio_with_feature_lst], f, ensure_ascii=False, indent=3)


def as_audio_with_feature(dct):
    return DatasetEntity(dct['file'], dct['chord'], dct['feature'], dct['duration'], dct['instrument'])


def load_features_from_json(name):
    with open(name, "r", encoding='utf-8') as f:
        audios = json.load(f, object_hook=as_audio_with_feature)

    return audios


def create_dataset_json(useAll, path):
    if useAll:
        guitar_only = path + "/Guitar_Only"
        path_other = path + "/Other_Instruments"
        others = list(map(lambda x: path_other + "/" + x, get_folders(path_other)))

        guitar_only_dataset = get_features(load_dataset(guitar_only), "guitar")[0]

        save_features_json("resources/guitar_only.json", guitar_only_dataset)

        others_dataset = list(map(lambda x: get_features(load_dataset(x), x.split("/")[-1].lower()), others))

        for data in others_dataset:
            save_features_json("resources/" + data[1] + ".json", data[0])
    else:
        guitar_only = path + "/Guitar_Only"
        guitar_only_dataset = get_features(load_dataset(guitar_only), "guitar")[0]

        save_features_json("resources/guitar_only.json", guitar_only_dataset)


def create_model():
    model = nn.Sequential(nn.Linear(12, 128),
                          nn.ReLU(),
                          nn.Linear(128, 10),
                          nn.LogSoftmax(dim=1))
    criterion = nn.NLLLoss()
    optimizer = torch.optim.SGD(model.parameters(), lr=0.003)

    return model, criterion, optimizer


def form_dataset(useAll):
    files = ["resources/guitar_only.json", "resources/guitar.json", "resources/accordion.json", "resources/piano.json",
             "resources/violin.json"]

    if useAll:
        dataset = []
        for file in files:
            dataset.extend(load_features_from_json(file))
    else:
        dataset = load_features_from_json(files[0])

    X = list(map(lambda x: x.feature, dataset))
    y = list(map(lambda x: CHORDS_DICT[x.chord], dataset))

    X_train, X_test, y_train, y_test = map(torch.tensor, train_test_split(X, y, test_size=0.3, random_state=42))

    return X_train, X_test, y_train, y_test


def fit_model(model, criterion, optimizer, X_train, y_train, batch_size):
    X_batches = torch.split(X_train, batch_size)
    y_batches = torch.split(y_train, batch_size)

    epochs = 1000
    for e in trange(epochs, desc="epochs processing"):
        running_loss = 0
        for i in trange(len(X_batches), desc="epoch {}".format(e)):
            x = X_batches[i]
            y = y_batches[i]

            optimizer.zero_grad()

            output = model(x)

            loss = criterion(output, y)
            loss.backward()
            optimizer.step()

            running_loss += loss.item()
        else:
            print(f"Training loss: {running_loss / len(X_batches)}")

        torch.save(model.state_dict(), "resources/ann_model")


def load_model(path_to_model):
    model = create_model()[0]
    model.load_state_dict(torch.load(path_to_model))
    model.eval()
    return model


def test_model(X_test, y_test):
    model = load_model("resources/ann_model")

    predicted_test = torch.argmax(model(X_test), dim=1)
    print("accuracy test {}".format(accuracy_score(y_test, predicted_test)))


def predict_chords(X, path_to_model):
    model = load_model(path_to_model)
    return torch.argmax(model(torch.tensor(X)), dim=1)


def main():
    X_train, X_test, y_train, y_test = form_dataset(True)
    model, criterion, optimizer = create_model()

    fit_model(model, criterion, optimizer, X_train, y_train, 20)
    test_model(X_test, y_test)


if __name__ == "__main__":
    main()
