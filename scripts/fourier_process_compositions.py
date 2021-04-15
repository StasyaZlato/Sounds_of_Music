import json
import os
import sys

from composition import Composition
from processing_utils import get_json_answer_path


def process_file(paths, chord_duration):
    compositions = []
    print(paths)
    for path in paths:
        composition = Composition(path, chord_duration)
        composition.librosa_make_graphics()
        composition.process_composition_fourier()
        compositions.append(composition)

    path_to_json = get_json_answer_path()
    print(path_to_json)

    with open(os.path.join(path_to_json, "answer_fourier.json"), 'w', encoding='utf-8') as f:
        json.dump(compositions, f, ensure_ascii=False, indent=3, cls=Composition.CompositionEncoder)


def main():
    args = sys.argv[1].split()
    print('args: ', args)

    chord_duration = float(args[0])

    if len(args) == 2:
        filename = args[1]
        process_file([filename], chord_duration)
    else:
        filenames = args[1:]
        process_file(filenames, chord_duration)


if __name__ == "__main__":
    main()
