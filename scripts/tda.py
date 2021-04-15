import os
import pathlib

import librosa
from gtda.homology import VietorisRipsPersistence
from gtda.plotting import plot_diagram


class CompositionForTDA:

    def __init__(self, filename):
        self.filename = filename
        self.y, self.sr = self.load()

        self.tonnetz = librosa.feature.tonnetz(self.y, self.sr)

    def get_file_name_from_path(self):
        head, tail = os.path.split(self.filename)
        index = tail.index('.')
        return tail[:index]

    def load(self):
        return librosa.load(self.filename, sr=None)

    def tda(self):
        VR = VietorisRipsPersistence(homology_dimensions=[0, 1, 2])  # Parameter explained in the text
        diagrams = VR.fit_transform(self.tonnetz[None, :, :])
        fig = plot_diagram(diagrams[0])

        dirname = self.get_file_name_from_path()

        path_to_diagram = pathlib.Path(__file__).parent.parent.absolute()
        path_to_diagram = os.path.join(path_to_diagram, "generated", dirname)

        try:
            pathlib.Path(path_to_diagram).mkdir(parents=True, exist_ok=True)
        except OSError:
            print("[ERROR] Creation of the directory %s failed" % dirname)
        else:
            print("[INFO] Successfully created the directory %s " % dirname)

        filename = os.path.join(path_to_diagram, "persistence_diagram.png")

        fig.write_image(filename)
