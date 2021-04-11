package tasks;

import pojo.CompositionsResponse;
import utils.PythonCaller;

import java.io.IOException;
import java.util.List;

public class FourierTask extends BasePreprocessingTask {

    public FourierTask(List<String> paths, double chordDuration) {
        super(paths, chordDuration);
    }

    @Override
    CompositionsResponse executeScriptWithResponse() throws Exception {
        StringBuilder args = new StringBuilder()
                .append(chordDuration).append(" ")
                .append(String.join(" ", paths));

        PythonCaller.executePythonScipt("../scripts/fourier_process_compositions.py", args.toString());

        String pathToResponse = "answer_fourier.json";

        return parseJson(pathToResponse);
    }
}
