package tasks;

import pojo.CompositionsResponse;
import utils.PythonCaller;

import java.util.List;

public class AnnTask extends BasePreprocessingTask {
    public AnnTask(List<String> paths, double chordDuration) {
        super(paths, chordDuration);
    }

    @Override
    CompositionsResponse executeScriptWithResponse() throws Exception {
        String pathToScript = BasePreprocessingTask.getPathToScript("ann_process_compositions.py");
        String pathToModel = BasePreprocessingTask.getPathToScript("resources/ann_model");

        StringBuilder args = new StringBuilder()
                .append(chordDuration).append(" ")
                .append(pathToModel).append(" ")
                .append(String.join(" ", paths));

        PythonCaller.executePythonScipt(pathToScript, args.toString());

        String pathToResponse = BasePreprocessingTask.getPathToAnswer("answer_ann.json");

        return parseJson(pathToResponse);
    }
}
