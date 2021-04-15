package tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import pojo.Composition;
import pojo.CompositionsResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
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

    public static String getPathToScript(String scriptName) throws URISyntaxException {
        String pathToScript;

        String path = new File(BasePreprocessingTask.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();

        if (path.endsWith("jar")) {
            pathToScript = Path.of(Paths.get(path).getParent().toString(), "scripts/" + scriptName)
                    .toAbsolutePath().toString();
        } else {
            pathToScript = Paths.get("../scripts/" + scriptName).toAbsolutePath().toString();
        }

        return pathToScript;
    }

    public static String getPathToAnswer(String answer) throws URISyntaxException {
        String pathToAnswer;

        String path = new File(BasePreprocessingTask.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();

        if (path.endsWith("jar")) {
            pathToAnswer = Path.of(Paths.get(path).getParent().toString(), "generated/" + answer)
                    .toAbsolutePath().toString();
        } else {
            pathToAnswer = Paths.get("../generated/" + answer).toAbsolutePath().toString();
        }

        return pathToAnswer;
    }
}
