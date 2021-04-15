import json
import os
import sys

from composition import Composition
from processing_utils import get_json_answer_path


def process_file(paths, chord_duration, path_to_model):
    compositions = []
    for path in paths:
        composition = Composition(path, chord_duration)
        composition.librosa_make_graphics()
        composition.process_composition_ann(path_to_model)
        compositions.append(composition)

    path_to_json = get_json_answer_path()

    with open(os.path.join(path_to_json, "answer_ann.json"), 'w', encoding='utf-8') as f:
        json.dump(compositions, f, ensure_ascii=False, indent=3, cls=Composition.CompositionEncoder)


def main():
    args = sys.argv[1].split()

    chord_duration = float(args[0])

    path_to_model = args[1]

    if len(args) == 3:
        filename = args[2]
        process_file([filename], chord_duration, path_to_model)
    else:
        filenames = args[2:]
        process_file(filenames, chord_duration, path_to_model)


if __name__ == "__main__":
    main()
