import sys
from composition import Composition
import json

def process_file(paths):
    compositions = []
    for path in paths:
        composition = Composition(path)
        composition.process_composition_fourier()
        compositions.append(composition)

    with open("answer.json", 'w', encoding='utf-8') as f:
        json.dump(compositions, f, ensure_ascii=False, indent=3, cls=Composition.CompositionEncoder)


def main():
    if len(sys.argv) == 2:
        filename = sys.argv[1]
        process_file([filename])
    else:
        filenames = sys.argv[1:]
        process_file(filenames)


if __name__ == "__main__":
    main()