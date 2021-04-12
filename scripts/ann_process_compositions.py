import json
import sys

from composition import Composition


def process_file(paths, chord_duration, path_to_model):
    compositions = []
    for path in paths:
        composition = Composition(path, chord_duration)
        composition.librosa_make_graphics()
        composition.process_composition_ann(path_to_model)
        compositions.append(composition)

    with open("answer_ann.json", 'w', encoding='utf-8') as f:
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
