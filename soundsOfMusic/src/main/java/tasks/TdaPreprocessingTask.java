package tasks;

import javafx.concurrent.Task;
import utils.PythonCaller;

import java.util.List;

public class TdaPreprocessingTask extends Task<Void> {
    List<String> paths;

    public TdaPreprocessingTask(List<String> paths) {
        this.paths = paths;
    }

    @Override
    protected Void call() throws Exception {
        String pathToScript = BasePreprocessingTask.getPathToScript("tda_process_composition.py");

        String args = String.join(" ", paths);

        PythonCaller.executePythonScipt(pathToScript, args);

        return null;
    }
}
