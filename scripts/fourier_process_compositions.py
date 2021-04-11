import json
import sys

from composition import Composition
from create_graphics import librosa_make_graphics


def process_file(paths, chord_duration):
    compositions = []
    print(paths)
    for path in paths:
        librosa_make_graphics(path)
        composition = Composition(path, chord_duration)
        composition.process_composition_fourier()
        compositions.append(composition)

    with open("answer_fourier.json", 'w', encoding='utf-8') as f:
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
