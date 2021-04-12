package tasks;

import pojo.CompositionsResponse;
import utils.PythonCaller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AnnTask extends BasePreprocessingTask {
    public AnnTask(List<String> paths, double chordDuration) {
        super(paths, chordDuration);
    }

    @Override
    CompositionsResponse executeScriptWithResponse() throws Exception {
        Path pathToModel = Paths.get("../scripts/resources/ann_model").toAbsolutePath();

        StringBuilder args = new StringBuilder()
                .append(chordDuration).append(" ")
                .append(pathToModel).append(" ")
                .append(String.join(" ", paths));

        PythonCaller.executePythonScipt("../scripts/ann_process_compositions.py", args.toString());

        String pathToResponse = "answer_ann.json";

        return parseJson(pathToResponse);
    }
}
