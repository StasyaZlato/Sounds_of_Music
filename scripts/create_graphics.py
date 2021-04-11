import librosa
import librosa.display
from matplotlib import pyplot as plt
import os

def get_file_name_from_path(filepath):
    head, tail = os.path.split(filepath)
    index = tail.index('.')
    return tail[:index]

def librosa_make_graphics(audio_path):
    dirname = get_file_name_from_path(audio_path)
    print(dirname)
    try:
        os.mkdir(dirname)
    except OSError:
        print ("Creation of the directory %s failed" % dirname)
    else:
        print ("Successfully created the directory %s " % dirname)

    y, sr = librosa.load(audio_path)

    plt.figure(figsize=(14, 5))
    librosa.display.waveplot(y, sr=sr)
    filename = os.path.join(dirname, "waveplot.png")
    print(filename)
    plt.savefig(filename)
    
    X = librosa.stft(y)
    Xdb = librosa.amplitude_to_db(abs(X))
    plt.figure(figsize=(14, 5))
    librosa.display.specshow(Xdb, sr=sr, x_axis='time', y_axis='hz')
    plt.colorbar()
    filename = os.path.join(dirname, "chromagram.png")
    print(filename)
    plt.savefig(filename)