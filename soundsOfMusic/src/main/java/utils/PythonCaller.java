package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PythonCaller {
    public static int executePythonScipt(String scriptPath, String args) throws Exception {
        String pythonExecutable = getPythonExecutable();
        System.out.println("python is: " + pythonExecutable);
        if (pythonExecutable.equals("none")) {
            throw new Exception("No python executable");
        }

        if (!Files.exists(Paths.get(scriptPath))) {
            System.out.println("Error getting script");
            return -1;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, args);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);


        Process process = processBuilder.start();

        return process.waitFor();
    }

    public static String getPythonExecutable() throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("python3", "--version");
        Process process = processBuilder.start();

        if (process.waitFor() == 0) {
            return "python3";
        }

        processBuilder = new ProcessBuilder("python", "--version");
        process = processBuilder.start();

        if (process.waitFor() == 0) {
            return "python";
        }

        return "none";
    }
}