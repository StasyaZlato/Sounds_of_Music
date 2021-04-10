import sys
from composition import Composition
import json


def process_file(paths, chord_duration):
    compositions = []
    for path in paths:
        composition = Composition(path, chord_duration)
        composition.process_composition_ann()
        compositions.append(composition)

    with open("answer.json", 'w', encoding='utf-8') as f:
        json.dump(compositions, f, ensure_ascii=False, indent=3, cls=Composition.CompositionEncoder)


def main():
    chord_duration = int(sys.argv[1])

    if len(sys.argv) == 3:
        filename = sys.argv[2]
        process_file([filename], chord_duration)
    else:
        filenames = sys.argv[2:]
        process_file(filenames, chord_duration)


if __name__ == "__main__":
    main()
