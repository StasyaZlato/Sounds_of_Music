import sys

from tda import CompositionForTDA


def process_file(paths):
    for path in paths:
        composition = CompositionForTDA(path)
        composition.tda()


def main():
    args = sys.argv[1].split()

    if len(args) == 1:
        filename = args[0]
        process_file([filename])
    else:
        filenames = args
        process_file(filenames)


if __name__ == "__main__":
    main()
