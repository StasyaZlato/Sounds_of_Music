package utils;

import tasks.BasePreprocessingTask;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PythonCaller {
    public static int executePythonScipt(String scriptPath, String args) throws Exception {
        String pythonExecutable = getPythonExecutable();
        System.out.println("[INFO] python command is: " + pythonExecutable);
        if (pythonExecutable.equals("none")) {
            throw new Exception("No python executable");
        }

        if (!Files.exists(Paths.get(scriptPath))) {
            System.out.println("[ERROR] Error getting script");
            return -1;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, args);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);


        Process process = processBuilder.start();

        return process.waitFor();
    }

    public static String getPythonExecutable() throws IOException, URISyntaxException {
        String defaultPythonCommand = "python3";
        Path pathToPythonExecutorTxt;

        String path = new File(PythonCaller.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();

        if (path.endsWith("jar")) {
            pathToPythonExecutorTxt = Path.of(Paths.get(path).getParent().toString(), "python_executor.txt")
                    .toAbsolutePath();
        } else {
            pathToPythonExecutorTxt = Paths.get("../python_executor.txt").toAbsolutePath();
        }

        String pythonCommand = "";
        if (Files.exists(pathToPythonExecutorTxt)) {
            BufferedReader reader = new BufferedReader(new FileReader(pathToPythonExecutorTxt.toString()));
            pythonCommand = reader.readLine().trim();
            reader.close();
        }

        if (!pythonCommand.equals("python") && !pythonCommand.equals("python3")) {
            pythonCommand = defaultPythonCommand;
        }
        return pythonCommand;
    }
}