from gtda.homology import VietorisRipsPersistence
from gtda.plotting import plot_diagram
import librosa
import os
from pathlib import Path


class CompositionForTDA:

    def __init__(self, filename):
        self.filename = filename
        self.y, self.sr = self.load()

        self.tonnetz = librosa.feature.tonnetz(self.y, self.sr)

    def get_file_name_from_path(self):
        head, tail = os.path.split(self.filename)
        index = tail.index('.')
        return os.path.join("generated_images", tail[:index])

    def load(self):
        return librosa.load(self.filename, sr=None)

    def tda(self):
        VR = VietorisRipsPersistence(homology_dimensions=[0, 1, 2])  # Parameter explained in the text
        diagrams = VR.fit_transform(self.tonnetz[None, :, :])
        fig = plot_diagram(diagrams[0])

        dirname = self.get_file_name_from_path()
        try:
            Path("generated_images/").mkdir(parents=True, exist_ok=True)
            Path(dirname).mkdir(parents=True, exist_ok=True)
        except OSError:
            print("Creation of the directory %s failed" % dirname)
        else:
            print("Successfully created the directory %s " % dirname)

        path_to_graph = os.path.join(dirname, "persistence_diagram.png")
        fig.write_image(path_to_graph)
