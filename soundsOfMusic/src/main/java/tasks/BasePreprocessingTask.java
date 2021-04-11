package tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import pojo.Composition;
import pojo.CompositionsResponse;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class BasePreprocessingTask extends Task<CompositionsResponse> {
    List<String> paths;
    double chordDuration;

    public BasePreprocessingTask(List<String> paths, double chordDuration) {
        this.paths = paths;
        this.chordDuration = chordDuration;
    }

    @Override
    protected CompositionsResponse call() throws Exception {
        return executeScriptWithResponse();
    }

    abstract CompositionsResponse executeScriptWithResponse() throws Exception;

    CompositionsResponse parseJson(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<Composition> compositions = Arrays.asList(mapper.readValue(Paths.get(path).toFile(), Composition[].class));

        return new CompositionsResponse(compositions);
    }
}
