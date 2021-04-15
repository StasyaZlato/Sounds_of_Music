package tasks;

import pojo.CompositionsResponse;
import utils.PythonCaller;

import java.util.List;

public class FourierTask extends BasePreprocessingTask {

    public FourierTask(List<String> paths, double chordDuration) {
        super(paths, chordDuration);
    }

    @Override
    CompositionsResponse executeScriptWithResponse() throws Exception {
        String pathToScript = BasePreprocessingTask.getPathToScript("fourier_process_compositions.py");
        System.out.println(pathToScript);

        StringBuilder args = new StringBuilder()
                .append(chordDuration).append(" ")
                .append(String.join(" ", paths));

        PythonCaller.executePythonScipt(pathToScript, args.toString());

        String pathToResponse = BasePreprocessingTask.getPathToAnswer("answer_fourier.json");


        return parseJson(pathToResponse);
    }
}
