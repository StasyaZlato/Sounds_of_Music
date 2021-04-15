import os
from pathlib import Path


def get_json_answer_path():
    path_to_json = Path(__file__).parent.parent.absolute()
    path_to_json = os.path.join(path_to_json, "generated")
    try:
        Path(path_to_json).mkdir(parents=True, exist_ok=True)
    except OSError:
        print("Creation of the generated directory failed for ")
    else:
        print("Successfully created the \"generated\" directory for answer")

    return path_to_json
